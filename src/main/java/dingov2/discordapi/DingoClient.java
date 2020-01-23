package dingov2.discordapi;

import dingov2.bot.commands.Commands;
import dingov2.bot.music.DingoPlayer;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DingoClient {

    private Logger logger = LoggerFactory.getLogger(DingoClient.class);
    private DiscordClient discordClient;
    private DingoPlayer dingoPlayer;

    public DingoClient(String token) {
        dingoPlayer = new DingoPlayer();

        DiscordClientBuilder clientBuilder = new DiscordClientBuilder(token);
        discordClient = clientBuilder.build();
        Commands commandHandler = new Commands(this);
        // delete this shit later cause it shouldn't be here...
        discordClient.getEventDispatcher().on(MessageCreateEvent.class)
                .flatMap(event -> Mono.justOrEmpty(event.getMessage().getContent())
                        .flatMap(content -> Flux.fromIterable(commandHandler.entrySet())
                                .filter(entry -> content.startsWith("$" + entry.getKey()))
                                .flatMap(entry -> entry.getValue().getAction(event).execute())
                                .next()))
                .subscribe(null, error -> logger.error(error.getStackTrace().toString()));
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
