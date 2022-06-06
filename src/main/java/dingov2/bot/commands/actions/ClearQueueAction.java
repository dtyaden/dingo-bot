package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class ClearQueueAction extends AbstractMessageEventAction {
    private TrackScheduler scheduler;

    public ClearQueueAction(MessageCreateEvent event, TrackScheduler scheduler) {
        super(event);
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute(List<String> args) {
        scheduler.clearQueue();
        return Mono.empty();
    }
}