package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public Mono<Void> execute(List<String> args) {
        scheduler.getPlayer().setVolume(Integer.parseInt(args.get(0)));
        return Mono.empty();
    }
}
