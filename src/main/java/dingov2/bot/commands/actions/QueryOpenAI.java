package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.DingoOpenAIQueryService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import java.util.List;

public class QueryOpenAI extends AbstractMessageEventAction {

    private Logger logger;
    private DingoOpenAIQueryService dingoOpenAIQueryService;

    public QueryOpenAI(MessageCreateEvent event, DingoOpenAIQueryService service) {
        super(event);
        logger = LoggerFactory.getLogger(QueryOpenAI.class);
        this.dingoOpenAIQueryService = service;
    }

    @Override
    public Mono<Void> execute(List<String> args) {
        logger.info("query command received");
        dingoOpenAIQueryService.sendChatMessage(StringUtils.join(args))
                .subscribe(message -> event
                        .getMessage()
                        .getChannel()
                        .subscribe(messageChannel -> messageChannel
                                .createMessage(message)
                                .subscribe()));
        return Mono.empty();
    }

}
