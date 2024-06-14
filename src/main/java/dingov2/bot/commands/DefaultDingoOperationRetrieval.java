package dingov2.bot.commands;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.PrivateChannel;

public class DefaultDingoOperationRetrieval {

    private final Channel channel;
    private final boolean dingoMentioned;
    private final DingoActionGenerator queryOpenAi;
    private final DingoActionGenerator playMusic;

    public DefaultDingoOperationRetrieval(Channel channel, boolean dingoMentioned, DingoActionGenerator queryOpenAi,
                                          DingoActionGenerator playMusic) {
        this.channel = channel;
        this.dingoMentioned = dingoMentioned;
        this.queryOpenAi = queryOpenAi;
        this.playMusic = playMusic;
    }

    public DingoActionGenerator retrieveDefaultCommand() {
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
