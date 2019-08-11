package interactions.actions.music;

import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

public class NextTrackAction extends AbstractOperation {

    public NextTrackAction(IMessage message){
        super(message);
    }

    @Override
    public void run() {
        AudioPlayer.getAudioPlayerForGuild(message.getGuild()).skip();
    }

    @Override
    public String getInfo() {
        return "play the next track in the queue";
    }
}
