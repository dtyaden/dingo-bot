package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.music.TrackScheduler;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class DownloadAction extends AbstractMessageEventAction {
    public DownloadAction(MessageCreateEvent event) {
        super(event);
    }

    @Override
    public String getInfo() {
        return "Tell dingo bot to download a youtube link so we can play it back later.";
    }

    @Override
    public Mono<Void> execute(List<String> args) {
        return null;
    }
}
