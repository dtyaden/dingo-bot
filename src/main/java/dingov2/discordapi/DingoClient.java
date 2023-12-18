package dingov2.discordapi;

import com.theokanning.openai.service.OpenAiService;
import dingov2.bot.commands.Commands;
import dingov2.bot.services.DingoOpenAIQueryService;
import dingov2.bot.services.music.DingoPlayer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.voice.VoiceConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public class DingoClient {

    private Logger logger = LoggerFactory.getLogger(DingoClient.class);
    private DiscordClient discordClient;
    private DingoPlayer dingoPlayer;
    private VoiceConnectionRegistry registry;

    public DingoClient(String discordToken, String openAIAPIKey) {
        dingoPlayer = new DingoPlayer();
        AtomicReference<GatewayDiscordClient> gatewayClient = new AtomicReference<>();
        DiscordClient discordClient = DiscordClient.create(discordToken);
        DingoOpenAIQueryService service = new DingoOpenAIQueryService(openAIAPIKey);
        Commands commandHandler = new Commands(this, service);
        discordClient.withGateway((GatewayDiscordClient gateway) -> {
                    Mono<Void> ready = gateway.on(ReadyEvent.class, readyEvent ->
                            Mono.fromRunnable(() -> {
                                System.out.println("we ready i guess");
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
