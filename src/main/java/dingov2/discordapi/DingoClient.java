package dingov2.discordapi;

import dingov2.bot.commands.Commands;
import dingov2.bot.music.DingoPlayer;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public class DingoClient {

    private Logger logger = LoggerFactory.getLogger(DingoClient.class);
    private DiscordClient discordClient;
    private DingoPlayer dingoPlayer;

    public DingoClient(String token) {
        dingoPlayer = new DingoPlayer();
        AtomicReference<GatewayDiscordClient> gatewayClient = new AtomicReference<>();
        DiscordClient discordClient = DiscordClient.create(token);
        Commands commandHandler = new Commands(this);

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

                    return ready.and(parseCommand);
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
}
