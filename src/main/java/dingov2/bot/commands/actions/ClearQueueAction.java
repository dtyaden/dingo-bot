package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.discordjson.json.ApplicationCommandRequest;
import reactor.core.publisher.Mono;

import java.util.List;

public class ClearQueueAction extends AbstractAction {
    private TrackScheduler scheduler;

    public ClearQueueAction(DingoEventWrapper event, List<String> arguments, TrackScheduler scheduler) {
        super(event, arguments);
        this.scheduler = scheduler;
        this.commandRequest = ApplicationCommandRequest.builder()
                .name("Clear")
                .description("Remove all queued tracks")
                .build();
    }

    @Override
    public Mono<Void> execute() {
        scheduler.clearQueue();
        return Mono.empty();
    }
}
