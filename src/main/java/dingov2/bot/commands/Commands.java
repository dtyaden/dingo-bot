package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.actions.*;
import dingov2.bot.services.OpenAIQueryService;
import dingov2.bot.services.YouTubeService;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses input and returns an appropriate command to execute based on the parameters given.
 */
public class Commands extends HashMap<String, DingoOperation> {
    private static final NullOperation nullOp = new NullOperation();
    private ConcurrentHashMap<User, YoutubeSearchResultsContainer> searchResultsContainers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<User, DingoAction> previousActions = new ConcurrentHashMap<>();
    private final DingoClient dingoClient;
    private final Logger logger;
    private final OpenAIQueryService dingoOpenAIQueryService;
    private final YouTubeService youTubeService;
    private String commandSymbol = "$";
    private static final HashSet<String> votingEmojis = new HashSet<>(Arrays.asList("🗑", "💾"));
    private ConcurrentHashMap<Message, Disposable> messageDeletionQueues = new ConcurrentHashMap<>();
    private DingoOperation playMusic;
    private HashMap<User, MessageCreateEvent> previousMessages = new HashMap<>();
    private final int messageTimeout = 120;
    public ReactionEmoji.Unicode floppyDiskEmoji = ReactionEmoji.unicode("💾");
    private static final Pattern singleDigitNumberRegex = Pattern.compile("\\d");

    private void registerCommand(DingoOperation action, String... commandKeys) {
        for (String command : commandKeys) {
            this.put(command, action);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private YoutubeSearchResultsContainer createYouTubeSearchContainer(MessageCreateEvent event) {
        YoutubeSearchResultsContainer container = new YoutubeSearchResultsContainer(event.getMessage().getTimestamp());
        // if a message comes in without an author it should be caught by the calling method.
        searchResultsContainers.put(event.getMessage().getAuthor().get(), container);
        return container;
    }

    public void loadCommands() {
        DefaultAudioPlayerManager manager = dingoClient.getDingoPlayer().getAudioplayerManager();
        TrackScheduler scheduler = dingoClient.getDingoPlayer().getScheduler();
        playMusic = (event, args) -> new PlayMusicImmediatelyAction(event, args, scheduler, manager, dingoClient);
        registerCommand((event, args) -> new QueueAction(event, args, scheduler, manager, dingoClient), "queue", "add");
        registerCommand(playMusic, "play");
        registerCommand((event, args) -> new LogoutAction(event, args, dingoClient), "logout");
        registerCommand(NavySeal::new, "pasta");
        registerCommand((event, args) -> new StopAction(event, args, scheduler), "stop", "pause");
        registerCommand((event, args) -> new VolumeAction(event, args, scheduler), "volume");
        registerCommand(DownloadAction::new, "download");
        registerCommand(ListAction::new, "list", "search", "find", "lookup");
        registerCommand((event, args) -> new NextTrackAction(event, args, scheduler), "next", "skip");
        registerCommand((event, args) -> new ClearQueueAction(event, args, scheduler), "clear", "clearqueue");
        registerCommand(DownloadAction::new, "download");
        registerCommand((event, args) -> new QueryOpenAI(event, args, dingoOpenAIQueryService), "query");
        registerCommand((event, args) -> new SearchYoutubeAction(event, args, youTubeService, createYouTubeSearchContainer(event)), "yt", "youtube", "ytsearch");
    }

    public Commands(DingoClient dingoClient, OpenAIQueryService dingoOpenAIQueryService, YouTubeService youTubeService) {
        super();
        this.dingoClient = dingoClient;
        this.dingoOpenAIQueryService = dingoOpenAIQueryService;
        this.youTubeService = youTubeService;
        loadCommands();
        logger = LoggerFactory.getLogger(Commands.class);
    }

    public void removeMessageAfterTimeout(MessageCreateEvent event) {
        Flux<Long> jfc = Flux.interval(Duration.ofSeconds(messageTimeout));
        Disposable disposable = jfc.take(1).subscribe(uselessValue -> {
            event.getMessage().delete().subscribe();

        });
        messageDeletionQueues.putIfAbsent(event.getMessage(), disposable);
        event.getMessage().addReaction(floppyDiskEmoji).subscribe();
    }

    public void handleReaction(ReactionAddEvent event) {
        // if message has more \💾 reactions than \🗑️ reactions, delete it.
        // If there are equal save and delete reactions queue for deletion.
        event.getMessage().subscribe(m -> {
                    if (m.getReactions().stream().filter(reaction -> reaction
                            .getEmoji()
                            .asUnicodeEmoji()
                            .get()
                            .getRaw()
                            .equals(floppyDiskEmoji.getRaw())).anyMatch(reaction -> reaction
                            .getCount() > 1)) {
                        logger.info("saving message " + m.getId());
                        messageDeletionQueues.getOrDefault(m, Mono.empty().subscribe()).dispose();
                        messageDeletionQueues.remove(m);
                    }
                }
        );
    }

    private DingoAction getCommandFromMessage(String message, MessageCreateEvent event, User self) {
        // determine if the bot is mentioned and remove the mention from the command.
        if (event.getMessage().getUserMentions().contains(self)) {
            message = message.replaceAll("<@.*>", "").trim();
        } else if (message.startsWith("@dingo")) {
            message = message.replaceFirst("@\\S*", "").trim();
        } else {
            // do nothing else with the message if the bot isn't mentioned
            return new NullAction();
        }
        List<String> command = Arrays.asList(message.split(" "));
        // if splitting leaves an empty list, we don't need to do anything
        if (command.isEmpty()) {
            return new NullAction();
        }

        String commandTitle = command.get(0).toLowerCase();
        DingoOperation operation = this.get(commandTitle);
        List<String> commandArguments;

        // If there's no matching command, treat it as a play music command and use the command as the arguments
        if (operation == null) {
            operation = playMusic;
            commandArguments = command;
        } else {
            if (command.size() > 1) {
                commandArguments = command.subList(1, command.size());
            } else {
                commandArguments = new ArrayList<>();
            }
        }

        return operation.getAction(event, commandArguments);
    }

    private DingoAction handleYouTubeSearchResponse(String message, User author, MessageCreateEvent event) {
        String selectedVideoUrl = "";
        Matcher m = singleDigitNumberRegex.matcher(message);
        //TODO: Determine if user is responding to a YT search that was made within the last 5 minutes.
        if (!m.find()) {
            return null;
        }
        YoutubeSearchResultsContainer container = searchResultsContainers.get(author);
        if (container == null) {
            return null;
        }
        if (!container.getCreateInstant().isBefore(Instant.now().plus(messageTimeout, ChronoUnit.SECONDS))) {
            searchResultsContainers.remove(author);
            return null;
        }
        int selectedVideoNumber;
        selectedVideoNumber = Integer.parseInt(m.group());
        selectedVideoUrl = container.getUrl(selectedVideoNumber);
        return playMusic.getAction(event, Arrays.asList(selectedVideoUrl));
    }

    public void handleMessage(MessageCreateEvent event) {
        String message = event.getMessage().getContent();
        message = message.trim();
        User self = event.getClient().getSelf().block();
        String selectedVideoUrl = "";
        // throw if message doesn't have an author somehow lol
        User author = event.getMessage().getAuthor().orElseThrow(() -> new RuntimeException("Message did not have an author somehow. Most likely an API error."));
        // Clean up any messages that the bot sends after a certain amount of time.
        if (author.getId().equals(author.getClient().getSelfId())) {
            logger.info("Marking message with appropriate save/delete emojis");
            removeMessageAfterTimeout(event);
        }
        DingoAction action = handleYouTubeSearchResponse(message, author, event);
        if (action == null){
            action = getCommandFromMessage(message, event, self);
        }
        if(action == null){
            throw new RuntimeException("failed to create action for " + message);
        }
        action.execute().subscribe();
        removeMessageAfterTimeout(event);
    }

}
