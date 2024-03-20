package dingov2.bot.commands;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.PrivateChannel;

public class DefaultDingoOperationRetrieval {

    private final Channel channel;
    private final boolean dingoMentioned;
    private final DingoOperation queryOpenAi;
    private final DingoOperation playMusic;

    public DefaultDingoOperationRetrieval(Channel channel, boolean dingoMentioned, DingoOperation queryOpenAi,
                                          DingoOperation playMusic) {
        this.channel = channel;
        this.dingoMentioned = dingoMentioned;
        this.queryOpenAi = queryOpenAi;
        this.playMusic = playMusic;
    }

    public DingoOperation retrieveDefaultCommand() {
        if (channel instanceof PrivateChannel) {
            return queryOpenAi;
        }
        if (dingoMentioned) {
            return playMusic;
        }
        return null;
    }

    public Channel getChannel() {
        return channel;
    }
}
