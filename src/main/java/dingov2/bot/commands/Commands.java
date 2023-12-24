package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.actions.*;
import dingov2.bot.services.OpenAIQueryService;
import dingov2.bot.services.YouTubeService;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.ChatInputInteractionEventWrapper;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import dingov2.discordapi.MessageCreateEventWrapper;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.core.spec.InteractionCallbackSpec;
import discord4j.discordjson.json.ApplicationCommandRequest;
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
    private static final Pattern singleDigitNumberRegex = Pattern.compile("^\\d$");

    private void registerGuildCommand(ApplicationCommandRequest request) {
        dingoClient.getClient().getGuilds().subscribe(guild -> {
            dingoClient.getClient().getApplicationId().subscribe(applicationId -> {
                dingoClient.getClient()
                        .getApplicationService()
                        .createGuildApplicationCommand(applicationId, guild.id().asLong(), request).subscribe();
            });
        });
    }

    private void registerCommand(DingoOperation action, String description, String... commandKeys) {
        String name = commandKeys[0];
        for (String command : commandKeys) {
            registerGuildCommand(action.getOperation(name, description));
            this.put(command, action);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private YoutubeSearchResultsContainer createYouTubeSearchContainer(DingoEventWrapper event) {
        YoutubeSearchResultsContainer container = new YoutubeSearchResultsContainer(event.getTimestamp());
        // if a message comes in without an author it should be caught by the calling method.
        searchResultsContainers.put(event
                .getMember()
                .orElseThrow(() -> new RuntimeException("Message did not have a member associated with it somehow. Shouldn't happen ever.")), container);
        return container;
    }

    public void loadCommands() {
        DefaultAudioPlayerManager manager = dingoClient.getDingoPlayer().getAudioplayerManager();
        TrackScheduler scheduler = dingoClient.getDingoPlayer().getScheduler();
        playMusic = (event, args) -> new PlayMusicImmediatelyAction(event, args, scheduler, manager, dingoClient);
        registerCommand((event, args) -> new QueueAction(event, args, scheduler, manager, dingoClient), "Queue a new track to be played", "queue", "add");
        registerCommand(playMusic, "immediately play a new track", "play");
        registerCommand((event, args) -> new LogoutAction(event, args, dingoClient), "tell the bot to logout of discord. does not work.", "logout");
        registerCommand((event1, arguments) -> new NavySeal(event1, arguments), "what the fuck did you just fucking say about me you little bitch?!", "pasta");
        registerCommand((event, args) -> new StopAction(event, args, scheduler), "Immediately stop playback of the current track.", "stop", "pause");
        registerCommand((event, args) -> new VolumeAction(event, args, scheduler), "Set volume of music playback. Usage /volume {number from 1-100} ", "volume");
        registerCommand((event1, arguments) -> new DownloadAction(event1, arguments), "Download youtube video, convert to audio only. args = link to vid", "download");
        registerCommand((event1, arguments) -> new ListAction(event1, arguments), "Search downloaded files. Usage: /search {search keyword}", "search ", "list", "search", "find", "lookup");
        registerCommand((event, args) -> new NextTrackAction(event, args, scheduler), "Force the queue to skip to the next track.", "next", "skip");
        registerCommand((event, args) -> new ClearQueueAction(event, args, scheduler), "Remove all tracks from the queue.", "clear", "clearqueue");
        registerCommand((event, args) -> new QueryOpenAI(event, args, dingoOpenAIQueryService), "Send a query to gpt-4 on openAI. Costs real money but fuck it.", "query");
        registerCommand((event, args) -> new SearchYoutubeAction(event, args, youTubeService, createYouTubeSearchContainer(event)), "Search youtube and get top 5 results. Type the number you want to play and send it.", "yt", "youtube", "ytsearch");
    }

    public Commands(DingoClient dingoClient, OpenAIQueryService dingoOpenAIQueryService, YouTubeService youTubeService) {
        super();
        this.dingoClient = dingoClient;
        this.dingoOpenAIQueryService = dingoOpenAIQueryService;
        this.youTubeService = youTubeService;
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
        DingoEventWrapper eventWrapper;
        return playMusic.getAction(new MessageCreateEventWrapper(event), Collections.singletonList(selectedVideoUrl));
    }

    public void handleApplicationCommand(ChatInputInteractionEvent event, GatewayDiscordClient gatewayDiscordClient) {

        DingoOperation operation = get(event.getCommandName());
        var channelSnowflake = event.getInteraction().getChannelId();
        gatewayDiscordClient.getChannelById(channelSnowflake).subscribe(channel -> {
            StringBuilder argsBuilder = new StringBuilder();
            event.getOption("args").ifPresent(applicationCommandInteractionOption -> {
                applicationCommandInteractionOption.getValue().ifPresent(value -> {
                    argsBuilder.append(value.getRaw());
                });
            });
            DingoOperation action = get(event.getCommandName());
            if (action == null) {
                throw new RuntimeException("A slash command somehow didn't have a matching operation. This should never happen.");
            }
            event.reply(event.getCommandName() + " " + argsBuilder).then().subscribe();
            DingoAction dingoAction = action.getAction(new ChatInputInteractionEventWrapper(event), Collections.singletonList(argsBuilder.toString()));
            dingoAction.execute().subscribe();
        });
    }

    private DingoAction getCommandFromMessage(String message, MessageCreateEvent event) {
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

        return operation.getAction(new MessageCreateEventWrapper(event), commandArguments);
    }

    public void handleMessage(MessageCreateEvent event) {

        String message = event.getMessage().getContent();
        message = message.trim();
        User self = event.getClient().getSelf().block();
        Member member = event.getMember().orElseThrow(() -> new RuntimeException("Message did not have a member associated with it. Most likely something wrong/weird with the API"));
        // Clean up any messages that the bot sends after a certain amount of time.
        dingoClient.getClient().getSelf().subscribe(userData ->{
            if(userData.id().asString().equals(member.getId().asString())){
                logger.info("Marking message with appropriate save/delete emojis");
                removeMessageAfterTimeout(event);
            }
        });
        boolean dingoMentioned = event.getMessage().getUserMentions().contains(self);
        boolean isAtDingo = message.startsWith("@dingo");
        if (dingoMentioned) {
            message = message.replaceAll("<@.*>", "").trim();
        } else if (isAtDingo) {
            message = message.replaceFirst("@\\S*", "").trim();
        }
        var user = dingoClient.getClient().getUserById(member.getId());
        DingoAction action = handleYouTubeSearchResponse(message, member, event);
        // failed to get a youtube play action and dingo isn't mentioned. it's not a valid command.
        if (action == null) {
            if (!dingoMentioned && !isAtDingo) {
                return;
            }
            action = getCommandFromMessage(message, event);
        }

        if (action == null) {
            return;
        }
        action.execute().subscribe();
        removeMessageAfterTimeout(event);
    }
}
