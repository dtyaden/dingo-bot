package listeners;

import dingo.engine.DingoEngine;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.audio.events.TrackFinishEvent;

public class TrackFinishListener extends AbstractDingoListener<TrackFinishEvent> {
    public TrackFinishListener(long clientID) {
        super(clientID);
    }

    @Override
    public void handle(TrackFinishEvent event) {
        DingoEngine.getBot().changePresence(StatusType.ONLINE, ActivityType.LISTENING, "fuckboys talk in discord");
    }
}
