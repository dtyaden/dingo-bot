package test.utilities;

import dingov2.bot.services.downloads.DownloadService;
import dingov2.bot.services.downloads.validation.DownloadResult;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.bot.services.downloads.YoutubeDownloaderService;
import org.junit.Before;
import org.junit.Test;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloaderTests {

    private String testVid = "https://www.youtube.com/watch?v=VoNGrW8aMRs";
    private String reeeaudio = "https://cdn.discordapp.com/attachments/326614452659027968/983613258323873812/REEEEEEEE.m4a";
    YoutubeDownloaderService youtubeDownloadService;
    AudioTrackUtil audioTrackUtil;
    DownloadService downloadService;

    @Before
    public void init(){
        audioTrackUtil = new AudioTrackUtil();
        youtubeDownloadService = new YoutubeDownloaderService(audioTrackUtil);
        downloadService = new DownloadService(youtubeDownloadService);
    }

    @Test
    public void testAudioURLDownloadsCorrectly(){
        DownloadResult result = downloadService.download(Arrays.asList(reeeaudio)).results.get(0);
        assert result.isSuccessful();
    }
    @Test
    public void testVideoDownloadsCorrectly(){
         DownloadResult result = downloadService.download(Arrays.asList(testVid)).results.get(0);
         assert result.isSuccessful();
    }
}
