package dingo.api.discord4j;

import dingo.api.base.entity.AbstractDingoMessage;
import dingo.api.base.entity.DingoChannel;
import dingo.api.base.entity.DingoUser;
import discord4j.core.object.entity.Channel;
import discord4j.core.object.entity.Message;

public class Discord4JDingoMessage extends AbstractDingoMessage {
    private Message message;

    public Discord4JDingoMessage(Message message){
        this.message = message;
    }

    public String getMessageContent(){
        return message.getContent().orElse("");
    }

    @Override
    public DingoChannel getChannel() {
        Discord4JDingoChannel channel = new Discord4JDingoChannel();
        message.getChannel()
        return channel;
    }

    @Override
    public DingoUser getAuthor() {
        // I don't think this should ever cause a problem but in case it does, fail fast.
        return new Discord4JDingoUser(message.getAuthor().orElseThrow());
    }

}
