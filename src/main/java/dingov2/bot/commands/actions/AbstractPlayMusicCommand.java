package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.AbstractAction;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class AbstractPlayMusicCommand extends AbstractAction {

    private Logger logger = LoggerFactory.getLogger(AbstractPlayMusicCommand.class);
    protected final TrackScheduler scheduler;
    private final DefaultAudioPlayerManager manager;
    private final DingoClient dingoClient;

    AudioTrackUtil util;

    public AbstractPlayMusicCommand(DingoEventWrapper event, List<String> arguments, TrackScheduler scheduler,
                                    DefaultAudioPlayerManager manager, DingoClient dingoClient) {
        super(event, arguments);
        this.scheduler = scheduler;
        this.manager = manager;
        this.dingoClient = dingoClient;
        util = new AudioTrackUtil();
    }
    public abstract void playCommand(String trackPath);
    @Override
    public Mono<Void> execute() {
        JoinAction join = new JoinAction(event, arguments, dingoClient);
        join.execute().subscribe();
        String trackPath = util.getTrack(arguments);
        playCommand(trackPath);
        return Mono.empty();
    }
}
