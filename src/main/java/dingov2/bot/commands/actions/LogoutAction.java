package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class LogoutAction extends AbstractAction {
    private final DingoClient dingoClient;

    public LogoutAction(DingoEventWrapper event, List<String> arguments, DingoClient dingoClient) {
        super(event, arguments);
        this.dingoClient = dingoClient;
    }

    @Override
    public Mono<Void> execute() {
//        Mono<Void> command = Mono.justOrEmpty(event)
//                .doOnNext(event -> dingoClient.getClient().logout().block(Duration.ofSeconds(5))).then();
//        return command;
        return Mono.empty();
    }
}
