package dingov2.discordapi;

import com.google.api.services.youtube.YouTube;
import com.theokanning.openai.service.OpenAiService;
import dingov2.bot.commands.Commands;
import dingov2.bot.services.*;
import dingov2.bot.services.music.DingoPlayer;
import dingov2.config.DingoSecrets;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.command.ApplicationCommand;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.voice.VoiceConnectionRegistry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public class DingoClient {

    private Logger logger = LoggerFactory.getLogger(DingoClient.class);
    private DiscordClient discordClient;
    private DingoPlayer dingoPlayer;
    private VoiceConnectionRegistry registry;

    public DingoClient(DingoSecrets secrets) {
        dingoPlayer = new DingoPlayer();
        AtomicReference<GatewayDiscordClient> gatewayClient = new AtomicReference<>();
        DiscordClient discordClient = DiscordClient.create(secrets.dingoApiKey);
        OpenAIQueryService service = StringUtils.isBlank(secrets.openAiApiKey) ? new NullOpenAIQueryService() : new DingoOpenAIQueryService(secrets.openAiApiKey);
        YouTubeService youTubeService = StringUtils.isBlank(secrets.youTubeApiKey) ? new NullYouTubeService() : new DingoYouTubeService(secrets.youTubeApiKey);
        Commands commandHandler = new Commands(this, service, youTubeService);
        discordClient.withGateway((GatewayDiscordClient gateway) -> {
                    Mono<Void> ready = gateway.on(ReadyEvent.class, readyEvent ->
                            Mono.fromRunnable(() -> {
                                logger.info("we ready i guess");
                            })).then();
                    Mono<Void> parseCommand = gateway.on(MessageCreateEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                commandHandler.handleMessage(event);
                            })
                    ).then();
                    Mono<Void> emojiReaction = gateway.on(ReactionAddEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                commandHandler.handleReaction(event);
                            })
                    ).then();
                    return ready.and(parseCommand).and(emojiReaction);
                }
        ).block();
    }

    public DiscordClient getClient() {
        return discordClient;
    }

    public void login() {
        discordClient.login().block();
    }

    public DingoPlayer getDingoPlayer() {
        return dingoPlayer;
    }

    public VoiceConnectionRegistry getRegistry() {
        return registry;
    }

}
