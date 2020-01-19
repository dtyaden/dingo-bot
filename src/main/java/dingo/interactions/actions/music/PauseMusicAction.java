package dingo.interactions.actions.music;

import dingo.api.base.entity.DingoMessage;
import dingo.interactions.actions.AbstractOperation;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

public class PauseMusicAction extends AbstractOperation {

    public PauseMusicAction(DingoMessage message) {
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
