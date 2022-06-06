package dingo.api.base.entity;

public interface DingoMessage {

    String getMessageContent();

    DingoChannel getChannel();

    DingoUser getAuthor();
}