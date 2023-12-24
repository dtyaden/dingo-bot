package dingov2.discordapi;

import discord4j.core.object.entity.Attachment;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.MessageChannel;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface DingoEventWrapper {
    Mono<Void> reply(Object content);
    List<Attachment> getAttachments();
    Mono<MessageChannel> getChannel();
    Optional<Member> getMember();
    Instant getTimestamp();

}
