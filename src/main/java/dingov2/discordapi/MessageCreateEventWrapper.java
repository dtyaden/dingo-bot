package dingov2.discordapi;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class MessageCreateEventWrapper implements DingoEventWrapper {

    private final MessageCreateEvent event;

    public MessageCreateEventWrapper(MessageCreateEvent event) {
        this.event = event;
    }

    @Override
    public Mono<Void> reply(Object content) {
        return getChannel().flatMap(channel -> channel.createMessage(content.toString())).then();
    }

    @Override
    public List<Attachment> getAttachments() {
        return event.getMessage().getAttachments();
    }

    @Override
    public Mono<MessageChannel> getChannel() {
        return event.getMessage().getChannel();
    }

    @Override
    public Optional<Member> getMember() {
        return event.getMember();
    }

    @Override
    public Instant getTimestamp() {
        return event.getMessage().getTimestamp();
    }
}
