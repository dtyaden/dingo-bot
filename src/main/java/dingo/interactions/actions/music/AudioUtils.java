package dingo.interactions.actions.music;

import sx.blah.discord.handle.impl.obj.Guild;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.AudioPlayer;

import java.util.*;

import static dingo.engine.DingoBotUtil.AUDIO_DIRECTORY;

public class AudioUtils {

    public Optional<AudioPlayer> getAudioPlayer(IGuild guild){
        AudioPlayer p = AudioPlayer.getAudioPlayerForGuild(guild);
        return Optional.of(p);
    }

    public List<AudioPlayer.Track> getTrackList(IGuild guild){
            List<AudioPlayer.Track> tracks = new ArrayList<>();
           Optional<AudioPlayer> player = getAudioPlayer(guild);
           player.ifPresent(audioPlayer -> tracks.addAll(audioPlayer.getPlaylist()));
           return tracks;
    }

    public Collection<String> getTrackMetadata(IGuild guild){
        List<String> trackList = new ArrayList<>();
        for (AudioPlayer.Track track : getTrackList(guild)){
            String metadata = extractMetadata(track);
            if (metadata.length() > 0){
                trackList.add(metadata);
            }
        }
        return trackList;
    }

    public String extractMetadata(AudioPlayer.Track track){
        Map<String, Object> trackMetadata = track.getMetadata();
        if (trackMetadata == null){
            return "";
        }
        Object fileMetadata = trackMetadata.get("file");
        Object urlMetadata = trackMetadata.get("url");
        Object validMetadata = fileMetadata == null ? urlMetadata : fileMetadata;
        if (validMetadata == null){
            return "";
        }
        return validMetadata.toString();
    }

    public String removeResourcesDirPath(String fullPath){
        String removedSoundDirHeading = fullPath.replaceFirst(AUDIO_DIRECTORY, "");
        return removedSoundDirHeading;
    }

    public String getTrackName(AudioPlayer.Track track){
        String metadata = extractMetadata(track);
        if (metadata.isEmpty()){
            return "";
        }
        return removeResourcesDirPath(metadata);
    }
}
