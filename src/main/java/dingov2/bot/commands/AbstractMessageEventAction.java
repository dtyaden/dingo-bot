package dingov2.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.MessageChannel;
import reactor.core.publisher.Mono;

public abstract class AbstractMessageEventAction implements DingoAction{

    protected MessageCreateEvent event;

    public AbstractMessageEventAction(MessageCreateEvent event){
        this.event = event;
    }

    public Mono<MessageChannel> getMessageChannel(){
        return Mono.justOrEmpty(event.getMessage())
                .flatMap(Message::getChannel);
    }

    public Mono<?> getMessageContent(){
        return Mono.justOrEmpty(event.getMessage().getContent());
    }
}

