package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class VolumeAction extends AbstractMessageEventAction {


    private final TrackScheduler scheduler;

    public VolumeAction(MessageCreateEvent event, List<String> arguments, TrackScheduler scheduler) {
        super(event, arguments);
        this.scheduler = scheduler;
    }

    public Mono<String> getDesiredVolume(){
        return getArguments().map(list -> list.get(0));
    }

    @Override
    public Mono<Void> execute() {
        scheduler.getPlayer().setVolume(Integer.parseInt(arguments.get(0)));
        return Mono.empty();
    }
}
