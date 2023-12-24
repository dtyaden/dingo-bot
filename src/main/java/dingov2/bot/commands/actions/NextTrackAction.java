package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class NextTrackAction extends AbstractAction {

    private TrackScheduler scheduler;

    public NextTrackAction(DingoEventWrapper event, List<String> arguments, TrackScheduler scheduler) {
        super(event, arguments);
        this.scheduler = scheduler;
    }

    @Override
    public Mono<Void> execute() {
        scheduler.nextTrack();
        return Mono.empty();
    }
}
