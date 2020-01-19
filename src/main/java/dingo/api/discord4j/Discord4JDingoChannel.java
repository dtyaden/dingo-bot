package dingo.api.discord4j;

import dingo.api.base.entity.AbstractDingoChannel;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.entity.TextChannel;

public class Discord4JDingoChannel extends AbstractDingoChannel {

    private MessageChannel channel;

    @Override
    public void sendMessage(String messageContent) {
        channel.createMessage(messageContent).subscribe();
    }

    public MessageChannel getChannel() {
        return channel;
    }

    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }
}
