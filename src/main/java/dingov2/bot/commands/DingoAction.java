package dingov2.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface DingoAction {
    Mono<Void> execute();

    default String getInfo(){
        return "David or whoever is currently maintaining it in case of David's death did not bother creating any " +
                "helpful information for this command.";
    }
}
