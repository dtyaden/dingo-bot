package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dingov2.bot.commands.actions.*;
import dingov2.bot.commands.methodparameters.GetCommandFromMessageParameters;
import dingov2.bot.commands.methodparameters.YoutubeSearchResponseParameters;
import dingov2.bot.services.OpenAIQueryService;
import dingov2.bot.services.YouTubeService;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.ChatInputInteractionEventWrapper;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import dingov2.discordapi.MessageCreateEventWrapper;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.UserData;
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
public class MessageProcessor extends HashMap<String, DingoActionGenerator> {
    private static final NullOperation nullOp = new NullOperation();
    private ConcurrentHashMap<String, YoutubeSearchResultsContainer> searchResultsContainers = new ConcurrentHashMap<>();
    private ConcurrentHashMap<User, DingoAction> previousActions = new ConcurrentHashMap<>();
    private final DingoClient dingoClient;
    private final Logger logger;
    private final OpenAIQueryService dingoOpenAIQueryService;
    private final YouTubeService youTubeService;
    private String commandSymbol = "$";
    private static final HashSet<String> votingEmojis = new HashSet<>(Arrays.asList("🗑", "💾"));
    private ConcurrentHashMap<Message, Disposable> messageDeletionQueues = new ConcurrentHashMap<>();
    private DingoActionGenerator playMusic;
    private HashMap<User, MessageCreateEvent> previousMessages = new HashMap<>();
    private final int messageTimeout = 120;
    public ReactionEmoji.Unicode floppyDiskEmoji = ReactionEmoji.unicode("💾");
    private static final Pattern singleDigitNumberRegex = Pattern.compile("^\\d$");
    private DingoActionGenerator queryOpenAI;

    private void registerGuildCommand(ApplicationCommandRequest request) {
        dingoClient.getClient().getGuilds().subscribe(guild -> {
            dingoClient.getClient().getApplicationId().subscribe(applicationId -> {
                dingoClient.getClient()
                        .getApplicationService()
                        .createGuildApplicationCommand(applicationId, guild.id().asLong(), request).subscribe();
            });
        });
    }

    private void registerGlobalCommand(ApplicationCommandRequest request){
        dingoClient.getClient().getApplicationId().subscribe(applicationId -> dingoClient.getClient()
                .getApplicationService()
                .createGlobalApplicationCommand(applicationId, request));
    }

    private void registerCommand(DingoActionGenerator action, String description, String... commandKeys) {
        String name = commandKeys[0];
        for (String command : commandKeys) {
            if(dingoClient.isTestBuild()){
                registerGuildCommand(action.getOperation(name,description));
            }
            else {
                registerGlobalCommand(action.getOperation(name, description));
            }
            this.put(command, action);
        }
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private YoutubeSearchResultsContainer createYouTubeSearchContainer(DingoEventWrapper event) {
        YoutubeSearchResultsContainer container = new YoutubeSearchResultsContainer(event.getTimestamp());
        String userId = event
                .getMember()
                .orElseThrow(() -> new RuntimeException("Message did not have a member associated with it somehow. Shouldn't happen ever."))
                        .getId().asString();
        // if a message comes in without an author it should be caught by the calling method.
        searchResultsContainers.put(userId, container);
        return container;
    }

    public void loadCommands() {
        AudioPlayerManager manager = dingoClient.getDingoPlayer().getAudioplayerManager();
        TrackScheduler scheduler = dingoClient.getDingoPlayer().getScheduler();
        playMusic = (event, args) -> new PlayMusicImmediatelyAction(event, args, scheduler, manager, dingoClient);
        queryOpenAI = (event, args) -> new QueryOpenAI(event, args, dingoOpenAIQueryService);
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
        registerCommand(queryOpenAI, "Send a query to gpt-4 on openAI. Costs real money but fuck it.", "query");
        registerCommand((event, args) -> new SearchYoutubeAction(event, args, youTubeService, createYouTubeSearchContainer(event)), "Search youtube and get top 5 results. Type the number you want to play and send it.", "yt", "youtube", "ytsearch");
    }

    public MessageProcessor(DingoClient dingoClient, OpenAIQueryService dingoOpenAIQueryService, YouTubeService youTubeService) {
        super();
        this.dingoClient = dingoClient;
        this.dingoOpenAIQueryService = dingoOpenAIQueryService;
        this.youTubeService = youTubeService;
        logger = LoggerFactory.getLogger(MessageProcessor.class);
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

    private DingoAction handleYouTubeSearchResponse(YoutubeSearchResponseParameters params) {
        String selectedVideoUrl = "";
        Matcher m = singleDigitNumberRegex.matcher(params.getMessage());
        //TODO: Determine if user is responding to a YT search that was made within the last 5 minutes.
        if (!m.find()) {
            return null;
        }
        YoutubeSearchResultsContainer container = searchResultsContainers.get(params.getAuthor().id().asString());
        if (container == null) {
            return null;
        }
        if (!container.getCreateInstant().isBefore(Instant.now().plus(messageTimeout, ChronoUnit.SECONDS))) {
            searchResultsContainers.remove(params.getAuthor().id().asString());
            return null;
        }
        int selectedVideoNumber;
        selectedVideoNumber = Integer.parseInt(m.group());
        selectedVideoUrl = container.getUrl(selectedVideoNumber);
        DingoEventWrapper eventWrapper;
        return playMusic.getAction(new MessageCreateEventWrapper(params.getEvent()), Collections.singletonList(selectedVideoUrl));
    }

    public void handleApplicationCommand(ChatInputInteractionEvent event, GatewayDiscordClient gatewayDiscordClient) {

        DingoActionGenerator operation = get(event.getCommandName());
        var channelSnowflake = event.getInteraction().getChannelId();
        gatewayDiscordClient.getChannelById(channelSnowflake).subscribe(channel -> {
            StringBuilder argsBuilder = new StringBuilder();
            event.getOption("args").ifPresent(applicationCommandInteractionOption -> {
                applicationCommandInteractionOption.getValue().ifPresent(value -> {
                    argsBuilder.append(value.getRaw());
                });
            });
            DingoActionGenerator action = get(event.getCommandName());
            if (action == null) {
                throw new RuntimeException("A slash command somehow didn't have a matching operation. This should never happen.");
            }
            event.reply(event.getCommandName() + " " + argsBuilder).then().subscribe();
            DingoAction dingoAction = action.getAction(new ChatInputInteractionEventWrapper(event), Collections.singletonList(argsBuilder.toString()));
            dingoAction.execute().subscribe();
        });
    }

    private DingoAction getCommandFromMessage(GetCommandFromMessageParameters getCommandFromMessageParameters) {
        // Handle responses to a youtube search
        DingoAction youtubeResponseAction = handleYouTubeSearchResponse(getCommandFromMessageParameters.youtubeSearchResponseParameters());
        if (youtubeResponseAction != null) {
            return youtubeResponseAction;
        }
        // If we aren't handling a youtube search:
        // verify that our context is valid to actually continue retrieving a command for the message.
        // Messages are mostly not related to dingo bot commands and should be discarded asap.
        DingoActionGenerator defaultOperation = getCommandFromMessageParameters.defaultDingoOperationRetrieval().retrieveDefaultCommand();
        if (defaultOperation == null) {
            return null;
        }

        List<String> command = Arrays.asList(getCommandFromMessageParameters.message().split(" "));
        // if splitting leaves an empty list, we don't need to do anything
        if (command.isEmpty()) {
            return new NullAction();
        }
        String commandTitle = command.get(0).toLowerCase();
        DingoActionGenerator operation = this.get(commandTitle);
        List<String> commandArguments;

        // If there's no matching command, treat it as a play music command and use the command as the arguments
        if (operation == null) {
            operation = defaultOperation;
            commandArguments = command;
        } else {
            if (command.size() > 1) {
                commandArguments = command.subList(1, command.size());
            } else {
                commandArguments = new ArrayList<>();
            }
        }

        return operation.getAction(new MessageCreateEventWrapper(getCommandFromMessageParameters.event()), commandArguments);
    }

    public void handleMessage(MessageCreateEvent event) {

        String message = event.getMessage().getContent();
        message = message.trim();
        User self = event.getClient().getSelf().block();
        UserData author = event.getMessage().getData().author();

        if (dingoClient.getClient().getSelf().block().id().asString().equals(author.id().asString())){
            logger.info("Marking message with appropriate save/delete emojis");
            removeMessageAfterTimeout(event);
            return;
        }

        boolean dingoMentioned = event.getMessage().getUserMentions().contains(self);
        boolean isAtDingo = message.startsWith("@dingo");
        if (dingoMentioned) {
            message = message.replaceAll("<@.*>", "").trim();
        } else if (isAtDingo) {
            message = message.replaceFirst("@\\S*", "").trim();
        }
        final String sanitizedMessage = message;
        var user = dingoClient.getClient().getUserById(Snowflake.of(author.id()));

        // we need access to the channel which means that the command creation/retrieval needs to be done in a lambda...
        event.getMessage().getChannel().subscribe(channel -> {
            // attempt to get some kind of command from the message.
            DingoAction action = getCommandFromMessage(
                    new GetCommandFromMessageParameters(sanitizedMessage, event,
                            new DefaultDingoOperationRetrieval(channel, dingoMentioned || isAtDingo,
                                    queryOpenAI, playMusic),
                            new YoutubeSearchResponseParameters(sanitizedMessage, author, event)));
            // we are not supposed to handle this message, we're done.
            if (action == null) {
                return;
            }
            action.execute().subscribe();
            removeMessageAfterTimeout(event);
        });


    }
}
