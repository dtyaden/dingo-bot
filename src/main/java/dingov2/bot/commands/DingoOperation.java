package dingov2.bot.commands;

import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.List;

public interface DingoOperation {
    DingoAction getAction(DingoEventWrapper event, List<String> arguments);
    default ApplicationCommandRequest getOperation(String name, String description, String...options){
        return ApplicationCommandRequest.builder()
                .name(name)
                .description(description)
                .addOption(ApplicationCommandOptionData.builder()
                        .name("args")
                        .description("Put additional shit here if the command requires it.")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("file")
                        .description("file")
                        .type(ApplicationCommandOption.Type.ATTACHMENT.getValue())
                        .required(false)
                        .build())
                .build();
    }
}
