package dingo.api.base.entity;

import discord4j.core.object.entity.Channel;

public interface DingoChannel {
    void sendMessage(String messageContent);
}
