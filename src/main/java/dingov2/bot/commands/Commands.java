package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.DingoAction;
import dingov2.bot.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Parses input and returns an appropriate command to execute based on the parameters given.
 */
public class Commands extends HashMap<String, DingoOperation> {


    private final DingoClient dingoClient;
    private void registerCommand(DingoOperation action, String...commandKeys){
        for (String command : commandKeys){
            this.put(command, action);
        }
    }


    public void loadCommands(){

        DefaultAudioPlayerManager manager = dingoClient.getDingoPlayer().getAudioplayerManager();
        TrackScheduler scheduler = dingoClient.getDingoPlayer().getScheduler();
        this.put("ping", event -> event.getMessage().getChannel().flatMap(channel -> channel.createMessage("test!"))
                .then());

        this.put("join", event -> Mono.justOrEmpty(event.getMember())
        .flatMap(Member::getVoiceState)
        .flatMap(VoiceState::getChannel)
        .flatMap(channel -> channel.join(spec -> spec.setProvider(dingoClient.getDingoPlayer())))
        .then());

        this.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
        .map(content -> Arrays.asList(content.split(" ")))
        .doOnNext(command ->manager.loadItem(command.get(1), scheduler))
        .then());

        registerCommand(event -> new PlayMusicCommand(event, scheduler, manager), "play");

        this.put("logout", event -> {
            dingoClient.getClient().logout();
            System.exit(0);
            return null;
        });
    }

    public Commands(DingoClient dingoClient) {
        super();
        this.dingoClient = dingoClient;
        loadCommands();
    }


}
