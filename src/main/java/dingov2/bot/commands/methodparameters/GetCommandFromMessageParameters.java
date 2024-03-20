package dingov2.bot.commands.methodparameters;

import dingov2.bot.commands.DefaultDingoOperationRetrieval;
import dingov2.bot.commands.methodparameters.YoutubeSearchResponseParameters;
import discord4j.core.event.domain.message.MessageCreateEvent;

public record GetCommandFromMessageParameters(String message, MessageCreateEvent event,
                                              DefaultDingoOperationRetrieval defaultDingoOperationRetrieval,
                                              YoutubeSearchResponseParameters youtubeSearchResponseParameters) {
}