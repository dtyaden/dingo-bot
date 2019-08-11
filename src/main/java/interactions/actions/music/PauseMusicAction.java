package interactions.actions.music;

import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

public class PauseMusicAction extends AbstractOperation {

    public PauseMusicAction(IMessage message){
        super(message);
    }

    @Override
    public void run() {
        AudioPlayer.getAudioPlayerForGuild(message.getGuild()).togglePause();
    }

    @Override
    public String getInfo() {
        return "pause/play the music bro.";
    }
}
