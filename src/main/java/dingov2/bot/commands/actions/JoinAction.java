package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.spec.VoiceChannelJoinSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.List;

public class JoinAction extends AbstractAction {

    private final DingoClient dingoClient;

    Logger logger = LoggerFactory.getLogger(JoinAction.class);

    public JoinAction(DingoEventWrapper event, List<String> arguments, DingoClient dingoClient) {
        super(event, arguments);
        this.dingoClient = dingoClient;
    }

    @Override
    public Mono<Void> execute() {

        logger.debug("executing join");
        VoiceChannelJoinSpec.builder().provider(dingoClient.getDingoPlayer()).build();
        return Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                .flatMap(channel -> channel.join(spec -> spec.setProvider(dingoClient.getDingoPlayer())))
                .then();
    }
}
