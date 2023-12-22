package dingov2.bot.commands.actions;

import dingov2.bot.commands.DingoAction;
import reactor.core.publisher.Mono;

import java.util.List;

public class NullAction implements DingoAction {
    @Override
    public Mono<Void> execute() {
        return Mono.empty();
    }
}
