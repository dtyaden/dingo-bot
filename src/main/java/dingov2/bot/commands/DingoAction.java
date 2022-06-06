package dingov2.bot.commands;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public interface DingoAction {
    Mono<Void> execute(List<String> args);

    default Mono<Void> execute(){
        return execute(new ArrayList<>());
    }
    default String getInfo(){
        return "David or whoever is currently maintaining it in case of David's death did not bother creating any " +
                "helpful information for this command.";
    }
}
