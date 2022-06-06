package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public class RudelyPlayMusicAction extends AbstractPlayMusicCommand{
    public RudelyPlayMusicAction(MessageCreateEvent event, TrackScheduler scheduler, DefaultAudioPlayerManager manager, DingoClient dingoClient) {
        super(event, scheduler, manager, dingoClient);
    }

    @Override
    public void playCommand(String trackPath) {
        scheduler.rudelyInterrupt(trackPath);
    }
}
