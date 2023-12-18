package dingov2.bot.services.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TrackScheduler extends AudioEventAdapter implements AudioLoadResultHandler {

    private final AudioPlayer player;
    private DefaultAudioPlayerManager manager;
    private BlockingDeque<AudioTrack> queue;
    private AudioTrackUtil util;
    private Logger logger;

    public TrackScheduler(AudioPlayer player, DefaultAudioPlayerManager manager) {
        this.player = player;
        this.manager = manager;
        this.queue = new LinkedBlockingDeque<>();
        util = new AudioTrackUtil();
        logger = LoggerFactory.getLogger(TrackScheduler.class);
        player.addListener(this);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        queue.add(track);
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

    public void nextTrack() {
        AudioTrack nextTrack = queue.poll();
        if (nextTrack == null) {
            return;
        }

        player.setPaused(false);
        player.playTrack(nextTrack);
    }

    public void queueTrack(String trackInfo) {
        try {
            manager.loadItem(trackInfo, this).get();
            if (player.getPlayingTrack() == null || player.isPaused()) {
                nextTrack();
            }
        } catch (Exception e) {

        }

    }

    // Force a track to the front of the queue and start playing it... rudely.
    public void rudelyInterrupt(String trackInfo) {
        LinkedBlockingDeque<AudioTrack> backupQueue = new LinkedBlockingDeque<>(queue);
        queue.clear();
        String panTrack = util.getTrack(Arrays.asList("pan"));
        try {
            if (player.getPlayingTrack() != null) {
                manager.loadItem(panTrack, this).get();
            }
            manager.loadItem(trackInfo, this).get();
            queue.addAll(backupQueue);
            nextTrack();
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public List<String> getQueuedTracks() {
        AtomicInteger playListOrder = new AtomicInteger(1);
        return queue.stream().map(track -> {
            AudioTrackInfo info = track.getInfo();
            String displayInfo = "";
            if (!info.title.equals("Unknown title")) {
                displayInfo = info.title;
            } else {
                displayInfo = Paths.get(info.identifier).getFileName().toString();
            }
            displayInfo = playListOrder.get() + ". " + displayInfo;
            playListOrder.set(playListOrder.get() + 1);
            return displayInfo;
        }).collect(Collectors.toList());
    }

    public void clearQueue(){
        queue.clear();
    }

    public void resumePlayback(){
        player.setPaused(false);
    }
}
