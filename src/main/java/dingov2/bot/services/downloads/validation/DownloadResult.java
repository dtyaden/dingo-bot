package dingov2.bot.services.downloads.validation;

import org.apache.commons.text.TextStringBuilder;

import java.io.File;

public class DownloadResult {
    public String url;
    public File downloadedFile;
    public String failureReason = "";

    public String warningMessage = "";

    public DownloadResult(String url, String failureReason){
        this.url = url;
        this.failureReason = failureReason;
    }

    public DownloadResult(String url, File downloadedFile){
        this.url = url;
        this.downloadedFile = downloadedFile;
    }

    public DownloadResult(String url, File downloadedFile, String warningMessage){
        this.url = url;
        this.downloadedFile = downloadedFile;
        this.warningMessage = warningMessage;
    }
    public String getResult(){
        TextStringBuilder builder = new TextStringBuilder();
        builder.append("Download of ");

        if (failureReason.isBlank()){
            builder.append("succeeded");
        }
        else{
            builder.append("the file at " + url + " failed because " + failureReason);
        }
        return builder.toString();
    }

    public boolean isSuccessful(){
        return failureReason.isBlank() && downloadedFile != null;
    }
}
