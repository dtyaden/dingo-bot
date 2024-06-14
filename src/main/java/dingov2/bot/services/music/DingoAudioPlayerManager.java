package dingov2.bot.services.music;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;

public class DingoAudioPlayerManager extends DefaultAudioPlayerManager {

    public DingoAudioPlayerManager(){
        registerSourceManager(new YoutubeAudioSourceManager());
        registerSourceManager(new LocalAudioSourceManager());
    }

}
