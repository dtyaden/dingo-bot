package dingo.interactions.actions.music;

import dingo.api.base.entity.DingoMessage;
import sx.blah.discord.handle.obj.IMessage;

public class QueueTrackAction extends AbstractPlayAudioClip {

    public QueueTrackAction(DingoMessage message) {
        super(message);
    }
}
