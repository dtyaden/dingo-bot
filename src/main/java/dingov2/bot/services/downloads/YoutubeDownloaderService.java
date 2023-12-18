package dingov2.bot.services.downloads;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.Format;
import dingov2.bot.services.downloads.validation.DownloadResult;
import dingov2.bot.services.music.AudioTrackUtil;
import org.apache.commons.text.TextStringBuilder;
import reactor.core.publisher.Mono;

import java.io.File;

public class YoutubeDownloaderService {

    YoutubeDownloader downloader;
    private AudioTrackUtil audioTrackUtil;

    public YoutubeDownloaderService(AudioTrackUtil audioTrackUtil){
        this.audioTrackUtil = audioTrackUtil;
        downloader = new YoutubeDownloader();
    }
    public static boolean IsYoutubeVideo(String url){
        return url.contains("youtube") || url.contains("youtu.be");
    }
    public DownloadResult downloadVideo(String url){
        url = url.replaceFirst(".*\\?.*=", "");
        RequestVideoInfo request = new RequestVideoInfo(url);
        Response<VideoInfo> infoResponse = downloader.getVideoInfo(request);
        VideoInfo videoInfo = infoResponse.data();
        String warningMessage = "";

        if(videoInfo.formats().isEmpty()){
            return new DownloadResult(url, "No formats for the video...");
        }

        Format bestFormat;
        if(videoInfo.audioFormats().isEmpty()){
            bestFormat = videoInfo.bestVideoFormat();
            if(bestFormat == null){
                return new DownloadResult(url, "No best audio or video formats somehow...");
            }
            warningMessage = "There weren't any audio formats available so we used a video format. sorry.";
        }
        else {
            bestFormat = videoInfo.bestAudioFormat();
        }

        if(bestFormat == null){
            bestFormat = videoInfo.audioFormats().get(0);
            if(bestFormat == null){
                bestFormat = videoInfo.bestVideoFormat();
            }
        }
        RequestVideoFileDownload downloadRequest = new RequestVideoFileDownload(bestFormat)
                .saveTo(new File(AudioTrackUtil.AUDIO_DIRECTORY)).overwriteIfExists(true)
                .renameTo(videoInfo.details().title());
        TextStringBuilder errorMessage = new TextStringBuilder();
        File downloadedFile = Mono.fromSupplier(()->{
            Response<File> response = downloader.downloadVideoFile(downloadRequest);
            File video = response.data();
            return video;
        }).doOnError(e -> errorMessage.append(e.getMessage())).block();

        if(downloadedFile == null){
            return new DownloadResult(url, errorMessage.build());
        }

        return new DownloadResult(url, downloadedFile, warningMessage);
    }
}
