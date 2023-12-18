package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.text.TextStringBuilder;

import java.util.List;

public class QueueAction extends AbstractPlayMusicCommand {

    public QueueAction(MessageCreateEvent event, TrackScheduler scheduler,
                       DefaultAudioPlayerManager manager, DingoClient dingoClient) {
        super(event, scheduler, manager, dingoClient);
    }

    @Override
    public void playCommand(String trackPath) {
        if (trackPath.isBlank()){
            event.getMessage().getChannel().subscribe(channel ->{
                String message = "";
                List<String> queuedTracks = scheduler.getQueuedTracks();
                if(queuedTracks.isEmpty()){
                    message = "Playlist is empty, brother.";
                }
                else{
                    TextStringBuilder builder = new TextStringBuilder();
                    for (String queuedTrack : queuedTracks) {
                        builder.appendln(queuedTrack);
                    }
                    message = builder.toString();
                }
                channel.createMessage(message).subscribe();
            });
        }
        else{
            scheduler.queueTrack(trackPath);
        }
    }
}
