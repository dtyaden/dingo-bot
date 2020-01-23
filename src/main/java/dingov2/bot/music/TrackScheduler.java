package dingov2.bot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class TrackScheduler implements AudioLoadResultHandler {

    private final AudioPlayer player;

    public TrackScheduler(AudioPlayer player){
        this.player = player;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        player.setPaused(false);
        player.playTrack(track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {

    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException exception) {

    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
