package dingov2.bot.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.List;

public interface DingoOperation {
    DingoAction getAction(MessageCreateEvent event, List<String> arguments);
}
