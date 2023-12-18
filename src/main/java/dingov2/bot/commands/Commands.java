package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.actions.*;
import dingov2.bot.services.DingoOpenAIQueryService;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parses input and returns an appropriate command to execute based on the parameters given.
 */
public class Commands extends HashMap<String, DingoOperation> {

    private final DingoClient dingoClient;
    private final Logger logger;
    private DingoOpenAIQueryService dingoOpenAIQueryService;
    private String commandSymbol = "$";
    private static final HashSet<String> votingEmojis = new HashSet<>(Arrays.asList("🗑", "💾"));
    private ConcurrentHashMap<Message, Disposable> messageDeletionQueues = new ConcurrentHashMap<>();
    private DingoOperation playMusic;

    private int messageTimeout = 30;
    public ReactionEmoji.Unicode floppyDiskEmoji = ReactionEmoji.unicode("💾");

    private void registerCommand(DingoOperation action, String... commandKeys) {
        for (String command : commandKeys) {
            this.put(command, action);
        }
    }

    public void loadCommands() {

        DefaultAudioPlayerManager manager = dingoClient.getDingoPlayer().getAudioplayerManager();
        TrackScheduler scheduler = dingoClient.getDingoPlayer().getScheduler();
        playMusic = event -> new PlayMusicImmediatelyAction(event, scheduler, manager, dingoClient);
        registerCommand(event -> new QueueAction(event, scheduler, manager, dingoClient), "queue", "add");
        registerCommand(playMusic, "play");
        registerCommand(event -> new LogoutAction(event, dingoClient), "logout");
        registerCommand(NavySeal::new, "pasta");
        registerCommand(event -> new StopAction(event, scheduler), "stop", "pause");
        registerCommand(event -> new VolumeAction(event, scheduler), "volume");
        registerCommand(DownloadAction::new, "download");
        registerCommand(ListAction::new, "list", "search", "find", "lookup");
        registerCommand(event -> new NextTrackAction(event, scheduler), "next", "skip");
        registerCommand(event -> new ClearQueueAction(event, scheduler), "clear", "clearqueue");
        registerCommand(DownloadAction::new, "download");
        registerCommand(event -> new QueryOpenAI(event, dingoOpenAIQueryService), "query");
    }

    public Commands(DingoClient dingoClient, DingoOpenAIQueryService dingoOpenAIQueryService) {
        super();
        this.dingoClient = dingoClient;
        this.dingoOpenAIQueryService = dingoOpenAIQueryService;
        loadCommands();
        logger = LoggerFactory.getLogger(Commands.class);
    }

    public void removeMessageAfterTimeout(MessageCreateEvent event){
        Flux<Long> jfc = Flux.interval(Duration.ofSeconds(messageTimeout));
        Disposable disposable = jfc.take(1).subscribe(uselessValue -> {
            event.getMessage().delete().subscribe();

        });
        messageDeletionQueues.putIfAbsent(event.getMessage(), disposable);
        event.getMessage().addReaction(floppyDiskEmoji).subscribe();
    }

    public void handleReaction(ReactionAddEvent event){
        // if message has more \💾 reactions than \🗑️ reactions, delete it.
        // If there are equal save and delete reactions queue for deletion.
        event.getMessage().subscribe(m -> m.getAuthor().ifPresent(author -> {
            if(author.getId().equals(author.getClient().getSelfId())){
                if(m.getReactions().stream().filter(reaction -> reaction
                        .getEmoji()
                        .asUnicodeEmoji()
                        .get()
                        .getRaw()
                        .equals(floppyDiskEmoji.getRaw())).anyMatch(reaction -> reaction
                        .getCount() > 1)){
                    logger.info("saving message " + m.getId());
                    messageDeletionQueues.getOrDefault(m, Mono.empty().subscribe()).dispose();
                    messageDeletionQueues.remove(m);
                }
            }
        }));
    }

    public void handleMessage(MessageCreateEvent event) {
        String message = event.getMessage().getContent();
        message = message.trim();
        User self = event.getClient().getSelf().block();

        // Clean up any messages that the bot sends after a certain amount of time.
        event.getMessage().getAuthor().ifPresent(author -> {
            if (author.getId().equals(author.getClient().getSelfId())) {
                logger.info("Marking message with appropriate save/delete emojis");
                removeMessageAfterTimeout(event);
            }
        });

        // determine if the bot is mentioned and remove the mention from the command.
        if (event.getMessage().getUserMentions().contains(self)){
            message = message.replaceAll("<@.*>", "").trim();
        }
        else if (message.startsWith("@dingo")){
            message = message.replaceFirst("@\\S*", "").trim();
        }
        else{
            // do nothing else with the message if the bot isn't mentioned
            return;
        }
        List<String> command = Arrays.asList(message.split(" "));
        // if splitting leaves an empty list, we don't need to do anything
        if (command.isEmpty()) {
            return;
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

        // tell the operation to build an action object and run it.
        operation.getAction(event).execute(commandArguments).subscribe(null, error -> {
            System.out.println(error.getMessage());
            error.printStackTrace();
        });
        removeMessageAfterTimeout(event);
    }

}
