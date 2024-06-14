package dingov2.bot.commands.methodparameters;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.UserData;

public class YoutubeSearchResponseParameters {
    private String message;
    private UserData author;
    private MessageCreateEvent event;

    public YoutubeSearchResponseParameters(String message, UserData author, MessageCreateEvent event) {
        this.message = message;
        this.author = author;
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public UserData getAuthor() {
        return author;
    }

    public MessageCreateEvent getEvent() {
        return event;
    }
}
