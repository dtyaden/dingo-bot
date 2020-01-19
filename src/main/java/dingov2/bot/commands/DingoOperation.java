package dingov2.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

public interface DingoOperation {
    DingoAction getAction(MessageCreateEvent event);
}
