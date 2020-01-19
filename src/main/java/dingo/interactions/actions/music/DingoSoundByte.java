package dingo.interactions.actions.music;

import dingo.api.base.entity.DingoMessage;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;


public class DingoSoundByte extends AbstractPlayAudioClip {

	public DingoSoundByte(DingoMessage message) {
		super(message);
	}

    protected void preQueueTrack(AudioPlayer p, File track) throws IOException, UnsupportedAudioFileException {
        // Before track plays, interrupt current song playing and play pan noise
        if (p.getCurrentTrack() != null) {
            System.out.println("track playing queueing pan");
            p.clear();
            File pan = searchForFile("pan");
            if(!pan.equals(track)) {
                p.queue(pan);
            }
        }
    }
}
