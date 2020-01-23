package dingov2.bot.music;

import com.sedmelluq.discord.lavaplayer.format.StandardAudioDataFormats;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.voice.AudioProvider;

import java.nio.ByteBuffer;

public class DingoPlayer extends AudioProvider {

    private DefaultAudioPlayerManager audioplayerManager;
    private TrackScheduler scheduler;
    AudioPlayer player;
    private MutableAudioFrame frame;

    public DingoPlayer(){
        super(ByteBuffer.allocate(StandardAudioDataFormats.DISCORD_OPUS.maximumChunkSize()));
        audioplayerManager = new DefaultAudioPlayerManager();
        audioplayerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(audioplayerManager);
        AudioSourceManagers.registerLocalSource(audioplayerManager);
        player = audioplayerManager.createPlayer();
        frame = new MutableAudioFrame();
        frame.setBuffer(getBuffer());
        this.player = player;

        scheduler = new TrackScheduler(player);
    }

    @Override
    public boolean provide() {
        boolean didProvide = player.provide(frame);

        if(didProvide) {
            getBuffer().flip();
        }
        return didProvide;
    }

    public DefaultAudioPlayerManager getAudioplayerManager() {
        return audioplayerManager;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }
}
