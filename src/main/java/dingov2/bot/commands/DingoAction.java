package dingov2.bot.commands;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public interface DingoAction {

    default Mono<Void> execute(){
        return execute();
    }
    default String getInfo(){
        return "David or whoever is currently maintaining it in case of David's death did not bother creating any " +
                "helpful information for this command.";
    }
}
