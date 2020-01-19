package dingov2.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

public abstract class AbstractMessageEventAction implements DingoAction{

    protected MessageCreateEvent event;

    public AbstractMessageEventAction(MessageCreateEvent event){
        this.event = event;
    }
}

