package dingov2.bot.commands.actions;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import dingov2.bot.services.music.TrackScheduler;
import dingov2.discordapi.DingoClient;
import dingov2.discordapi.DingoEventWrapper;
import org.apache.commons.text.TextStringBuilder;

import java.util.List;

public class QueueAction extends AbstractPlayMusicCommand {

    public QueueAction(DingoEventWrapper event, List<String> arguments, TrackScheduler scheduler,
                       AudioPlayerManager manager, DingoClient dingoClient) {
        super(event, arguments, scheduler, dingoClient);
    }

    @Override
    public void playCommand(String trackPath) {
        if (trackPath.isBlank()){
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
            event.reply(message).subscribe();
        }
        else{
            scheduler.queueTrack(trackPath);
        }
    }
}
