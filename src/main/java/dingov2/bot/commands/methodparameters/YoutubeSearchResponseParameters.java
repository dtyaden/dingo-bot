package dingov2.bot.commands.methodparameters;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;

public class YoutubeSearchResponseParameters {
    private String message;
    private User author;
    private MessageCreateEvent event;

    public YoutubeSearchResponseParameters(String message, User author, MessageCreateEvent event) {
        this.message = message;
        this.author = author;
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public User getAuthor() {
        return author;
    }

    public MessageCreateEvent getEvent() {
        return event;
    }
}
