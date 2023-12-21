package dingov2.bot.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OpenAIQueryService {
    default Flux<String> sendChatMessage(String chatMessage){
        return Flux.fromArray(new String[] {"OpenAIQueryService not congigured"});
    }
}
