package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.music.DingoPlayer;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.VoiceChannelCreateSpec;
import discord4j.core.spec.VoiceChannelJoinSpec;
import discord4j.core.spec.legacy.LegacyVoiceChannelCreateSpec;
import discord4j.voice.AudioReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class JoinAction extends AbstractMessageEventAction {

    private final DingoClient dingoClient;

    Logger logger = LoggerFactory.getLogger(JoinAction.class);

    public JoinAction(MessageCreateEvent event, DingoClient dingoClient) {
        super(event);
        this.dingoClient = dingoClient;
    }

    @Override
    public Mono<Void> execute(List<String> args) {

        logger.debug("executing join");
        VoiceChannelJoinSpec.builder().provider(dingoClient.getDingoPlayer()).build();
        return Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(channel -> channel.join(spec -> spec.setProvider(dingoClient.getDingoPlayer())))
                .then();
    }
}
