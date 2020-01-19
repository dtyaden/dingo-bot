package dingov2.discordapi;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;

public class DingoClient {

    private DiscordClient dingoClient;

    public DingoClient(String token){
        DiscordClientBuilder clientBuilder = new DiscordClientBuilder(token);
        dingoClient = clientBuilder.build();
    }

    public DiscordClient getClient(){
        return dingoClient;
    }

    public void login(){
        dingoClient.login().block();
    }
}
