package dingov2.bot.commands;

import dingov2.bot.commands.actions.NullAction;
import discord4j.core.event.domain.message.MessageCreateEvent;

import java.util.List;

public class NullOperation implements DingoOperation {
    @Override
    public DingoAction getAction(MessageCreateEvent event, List<String> arguments) {
        return new NullAction();
    }
}
