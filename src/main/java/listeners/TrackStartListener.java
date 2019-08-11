package listeners;

import engine.DingoEngine;
import interactions.actions.music.AudioUtils;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.audio.events.TrackStartEvent;

public class TrackStartListener extends AbstractDingoListener<TrackStartEvent> {

    AudioUtils utils = new AudioUtils();

    public TrackStartListener(long clientID) {
        super(clientID);
    }

    @Override
    public void handle(TrackStartEvent event) {
        String trackName = utils.getTrackName(event.getTrack());
        DingoEngine.getBot().changePresence(StatusType.ONLINE, ActivityType.PLAYING, trackName);

    }

}
