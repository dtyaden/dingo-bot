package dingov2.bot.commands;

import dingov2.bot.commands.actions.NullAction;
import dingov2.discordapi.DingoEventWrapper;

import java.util.List;

public class NullOperation implements DingoActionGenerator {
    @Override
    public DingoAction getAction(DingoEventWrapper event, List<String> arguments) {
        return new NullAction();
    }
}
