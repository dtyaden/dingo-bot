package interactions.actions;

import engine.DingoBotUtil;
import engine.MessageListener;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class YoutubeStreamAction extends AbstractOperation{

    private URL youtubeURL;

    public YoutubeStreamAction(IMessage message) {
        super(message);
    }

    public YoutubeStreamAction(IMessage message, URL url){
        super(message);
        this.youtubeURL = url;
    }

    public AudioInputStream getStreamFromYoutube(URL youtubeURL) throws IOException, UnsupportedAudioFileException {
        ProcessBuilder processBuilder;
        String fullCommand = "youtube-dl -f 251 " + youtubeURL.toString() + "-o - ";
        processBuilder = new ProcessBuilder(fullCommand);
        Process youtubeDl = processBuilder.start();
        InputStream youtubeDLOutput = youtubeDl.getInputStream();
        return AudioSystem.getAudioInputStream(youtubeDLOutput);
    }

    @Override
    public void run() {
        if(youtubeURL != null){
            try {
                AudioInputStream youtubeDLOutput = getStreamFromYoutube(youtubeURL);
                if(youtubeDLOutput == null){
                    return;
                }

                AudioPlayer.getAudioPlayerForGuild(message.getGuild()).queue(youtubeDLOutput);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnsupportedAudioFileException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getInfo() {
        return null;
    }
}
