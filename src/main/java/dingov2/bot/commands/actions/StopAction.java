package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class StopAction extends AbstractAction {

    private final TrackScheduler scheduler;

    public StopAction(DingoEventWrapper event, List<String> arguments, TrackScheduler scheduler) {
        super(event, arguments);
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute() {
        return Mono.justOrEmpty(scheduler.getPlayer())
                .doOnNext(player -> player.setPaused(true)).then();
    }
}
