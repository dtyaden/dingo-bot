package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.Arrays;

public class PlayMusicCommand extends AbstractMessageEventAction{

    private final TrackScheduler scheduler;
    private final DefaultAudioPlayerManager manager;

    public PlayMusicCommand(MessageCreateEvent event, TrackScheduler scheduler, DefaultAudioPlayerManager manager) {
        super(event);
        this.scheduler = scheduler;
        this.manager = manager;
    }

    @Override
    public Mono<Void> execute(MessageCreateEvent event) {
        return Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(command -> manager.loadItem(command.get(1), scheduler))
                .then();
    }
}
