package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class StopAction extends AbstractMessageEventAction {

    private final TrackScheduler scheduler;

    public StopAction(MessageCreateEvent event, TrackScheduler scheduler) {
        super(event);
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute(List<String> args) {
        return Mono.justOrEmpty(scheduler.getPlayer())
                .doOnNext(player -> player.setPaused(true)).then();
    }
}
