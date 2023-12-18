package dingov2.bot.services.downloads;

import dingov2.bot.services.downloads.validation.DownloadResult;
import dingov2.bot.services.downloads.validation.DownloadResults;
import dingov2.bot.services.music.AudioTrackUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DownloadService {

    Logger logger = LoggerFactory.getLogger(DownloadService.class);
    private YoutubeDownloaderService youtubeDownloaderService;

    public DownloadService(YoutubeDownloaderService service) {

        this.youtubeDownloaderService = service;
    }

    public long getDownloadSize(URL url) throws IOException {
        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("HEAD");
        return conn.getContentLengthLong();
    }

    public DownloadResult downloadUrl(String url) {
        try {
            File downloadedFile;
            URL download = new URL(url);
            long downloadSize = getDownloadSize(download);
            if(downloadSize > 1000000){
                throw new IOException("File way too big");
            }
            String fileName = download.getFile();
            String mimeType = URLConnection.guessContentTypeFromName(fileName);
            if (mimeType.contains("audio")) {
                downloadedFile = downloadUrlToPath(download, AudioTrackUtil.AUDIO_DIRECTORY);
                return new DownloadResult(url, downloadedFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new DownloadResult(url, e.getMessage());
        }
        return null;
    }

    public File downloadUrlToPath(URL url, String path) throws IOException {
        String fileName = FilenameUtils.getName(url.getFile());
        File targetFile = new File(path + fileName);
        FileUtils.copyURLToFile(url, targetFile, 2000, 2000);
        return targetFile;
    }

    public DownloadResults download(List<String> args) {
        DownloadResults results = new DownloadResults();
        args.parallelStream().forEach(url -> {
            DownloadResult downloadResult;
            if (YoutubeDownloaderService.IsYoutubeVideo(url)) {
                downloadResult = youtubeDownloaderService.downloadVideo(url);
            } else {
                downloadResult = downloadUrl(url);
            }

            results.results.add(downloadResult);
        });
        return results;
    }
}
