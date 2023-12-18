package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public abstract class AbstractPlayMusicCommand extends AbstractMessageEventAction {

    private Logger logger = LoggerFactory.getLogger(AbstractPlayMusicCommand.class);
    protected final TrackScheduler scheduler;
    private final DefaultAudioPlayerManager manager;
    private final DingoClient dingoClient;

    AudioTrackUtil util;

    public AbstractPlayMusicCommand(MessageCreateEvent event, TrackScheduler scheduler,
                                    DefaultAudioPlayerManager manager, DingoClient dingoClient) {
        super(event);
        this.scheduler = scheduler;
        this.manager = manager;
        this.dingoClient = dingoClient;
        util = new AudioTrackUtil();
    }
    public abstract void playCommand(String trackPath);
    @Override
    public Mono<Void> execute(List<String> args) {
        JoinAction join = new JoinAction(event, dingoClient);
        join.execute().subscribe();
        String trackPath = util.getTrack(args);
        playCommand(trackPath);
        return Mono.empty();
    }
}
