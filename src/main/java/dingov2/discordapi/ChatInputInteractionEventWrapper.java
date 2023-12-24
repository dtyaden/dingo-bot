package dingov2.discordapi;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChatInputInteractionEventWrapper implements  DingoEventWrapper {

    private final ChatInputInteractionEvent event;

    public ChatInputInteractionEventWrapper(ChatInputInteractionEvent event) {
        this.event = event;
    }

    @Override
    public Mono<Void> reply(Object content) {
        return event.createFollowup(content.toString()).then();
    }

    @Override
    public List<Attachment> getAttachments() {
        List<Attachment> attachments = new ArrayList<>();
        event.getInteraction().getMessage().ifPresent(message -> {
            attachments.addAll(message.getAttachments());
        });
        return attachments;
    }

    @Override
    public Mono<MessageChannel> getChannel() {
        return event.getInteraction().getChannel();
    }

    @Override
    public Optional<Member> getMember() {
        return event.getInteraction().getMember();
    }

    @Override
    public Instant getTimestamp() {
        return event.getCommandId().getTimestamp();
    }
}
