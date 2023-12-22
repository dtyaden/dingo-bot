package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

import java.util.List;

public class PlayMusicImmediatelyAction extends AbstractPlayMusicCommand{
    public PlayMusicImmediatelyAction(MessageCreateEvent event, List<String> arguments, TrackScheduler scheduler, DefaultAudioPlayerManager manager, DingoClient dingoClient) {
        super(event, arguments, scheduler, manager, dingoClient);
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
