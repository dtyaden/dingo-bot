package dingov2.bot.commands;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.commands.actions.*;
import dingov2.bot.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import reactor.core.publisher.Mono;

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

        registerCommand(event -> new PlayMusicCommand(event, scheduler, manager, dingoClient), "play");
        registerCommand(event -> new LogoutAction(event, dingoClient), "logout");
        registerCommand(NavySeal::new, "pasta");
        registerCommand(event -> new StopAction(event, scheduler), "stop", "pause");
        registerCommand(event -> new VolumeAction(event, scheduler), "volume");

    }

    public Commands(DingoClient dingoClient) {
        super();
        this.dingoClient = dingoClient;
        loadCommands();
    }



}
