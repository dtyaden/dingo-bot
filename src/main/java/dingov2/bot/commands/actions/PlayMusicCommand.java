package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PlayMusicCommand extends AbstractMessageEventAction {

    private Logger logger = LoggerFactory.getLogger(PlayMusicCommand.class);

    String path = "src/resources/sounds/";

    private final TrackScheduler scheduler;
    private final DefaultAudioPlayerManager manager;
    private final DingoClient dingoClient;

    public PlayMusicCommand(MessageCreateEvent event, TrackScheduler scheduler,
                            DefaultAudioPlayerManager manager, DingoClient dingoClient) {
        super(event);
        this.scheduler = scheduler;
        this.manager = manager;
        this.dingoClient = dingoClient;
    }

    public String getTrack(String arg){
        if (arg.contains("youtube") || arg.contains("youtu.be")){
            return arg;
        }
        logger.debug("didn't match youtube. making a file path instead");
        List<File> files = Arrays.asList(FileUtils.getFile(path).listFiles());

        Optional<File> match = files.stream().filter(file -> file.getName().toLowerCase().contains(arg.toLowerCase())).findFirst();
        return match.orElseThrow().getAbsolutePath();
    }

    @Override
    public Mono<Void> execute() {
        Mono<Void> playText = Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(command -> {
                    JoinAction join = new JoinAction(event, dingoClient);
                    join.execute().subscribe();
                    manager.loadItem(getTrack(command.get(1)), scheduler);
                })
                .then();
        return playText;
    }
}
