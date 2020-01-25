package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.music.DingoPlayer;
import dingov2.bot.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.logging.Logger;

public class VolumeAction extends AbstractMessageEventAction {


    private final TrackScheduler scheduler;

    public VolumeAction(MessageCreateEvent event, TrackScheduler scheduler) {
        super(event);
        this.scheduler = scheduler;
    }

    public Mono<String> getDesiredVolume(){
        return getArguments().map(list -> list.get(0));
    }

    @Override
    public Mono<Void> execute() {
        return getDesiredVolume()
                .doOnNext(str -> {
                    scheduler.getPlayer().setVolume(Integer.parseInt(str));
                    LoggerFactory.getLogger("idk").debug("setting volume to: " + Integer.parseInt(str));
                })
                .then();
    }
}
