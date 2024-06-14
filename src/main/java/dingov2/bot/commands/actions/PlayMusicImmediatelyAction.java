package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import reactor.core.publisher.Mono;

import java.util.List;

public class PlayMusicImmediatelyAction extends AbstractPlayMusicCommand{
    public PlayMusicImmediatelyAction(DingoEventWrapper event, List<String> arguments, TrackScheduler scheduler, AudioPlayerManager manager, DingoClient dingoClient) {
        super(event, arguments, scheduler, dingoClient);
    }
    @Override
    public void playCommand(String trackPath) {
        scheduler.rudelyInterrupt(trackPath);
    }

    @Override
    public Mono<Void> execute(){
        if(arguments.isEmpty()){
            scheduler.resumePlayback();
            return Mono.empty();
        }
        return super.execute();
    }
}
