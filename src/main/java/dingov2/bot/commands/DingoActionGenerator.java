package dingov2.bot.commands;

import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.object.command.ApplicationCommandOption;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;

import java.util.List;

public interface DingoActionGenerator {
    DingoAction getAction(DingoEventWrapper event, List<String> arguments);
    default ApplicationCommandRequest getOperation(String name, String description, String...options){
        return ApplicationCommandRequest.builder()
                .name(name)
                .description(description)
                .addOption(ApplicationCommandOptionData.builder()
                        .name("text")
                        .description("Text necessary for the command to run, eg. a youtube link, an audio file to play, what to search for. Example: type /yt and enter 'the boys are back in town' here to search for the boys are back in town.")
                        .type(ApplicationCommandOption.Type.STRING.getValue())
                        .required(false)
                        .build())
                .addOption(ApplicationCommandOptionData.builder()
                        .name("attachment")
                        .description("Upload a file as part of this command. Most commands don't support this.")
                        .type(ApplicationCommandOption.Type.ATTACHMENT.getValue())
                        .required(false)
                        .build())
                .build();
    }
}
