package dingov2.bot.services.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import discord4j.voice.AudioProvider;


import java.nio.ByteBuffer;

public class DingoPlayer extends AudioProvider {

    private AudioPlayerManager audioplayerManager;
    private TrackScheduler scheduler;
    AudioPlayer player;
    private MutableAudioFrame frame;

    public DingoPlayer(){

//        super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
        audioplayerManager = new DingoAudioPlayerManager();
        audioplayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(audioplayerManager);
        AudioSourceManagers.registerLocalSource(audioplayerManager);
        player = audioplayerManager.createPlayer();
        frame = new MutableAudioFrame();
        frame.setBuffer(getBuffer());
        scheduler = new TrackScheduler(player, audioplayerManager);
    }

    @Override
    public boolean provide() {
        boolean didProvide = player.provide(frame);

        if(didProvide) {
            getBuffer().flip();
        }
        return didProvide;
    }

    public AudioPlayerManager getAudioplayerManager() {
        return audioplayerManager;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }
}
