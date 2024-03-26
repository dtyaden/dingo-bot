package dingov2.bot.services;

import dingov2.discordapi.DingoEventWrapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OpenAIQueryService {
    default Flux<String> sendChatMessage(String chatMessage, DingoEventWrapper event){
        return Flux.fromArray(new String[] {"OpenAIQueryService not congigured"});
    }
}
