package dingov2.bot.services.downloads.validation;

import io.netty.util.internal.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadResults {
    public List<DownloadResult> results = new ArrayList<DownloadResult>();

    @Override
    public String toString() {
        TextStringBuilder builder = new TextStringBuilder();
        List<DownloadResult> successes = results.stream().filter(result -> result.isSuccessful()).collect(Collectors.toList());
        List<DownloadResult> failures = results.stream().filter(result -> !result.isSuccessful()).collect(Collectors.toList());
        if (!successes.isEmpty()) {
            builder.append("Successfully downloaded: ");
            for(DownloadResult result : successes){
                builder.append(result.downloadedFile.getName());
            }
            String successMessage = successes.stream().map(result -> {
                String message = result.downloadedFile.getName();
                if(!StringUtils.isWhitespace(result.warningMessage)){
                    message += " (Warning: " + result.warningMessage + ")";
                }
                return message;
            }).collect(Collectors.joining(","));
            builder.append(successMessage);
        }
        if(!failures.isEmpty()){
            if(!builder.isEmpty()){
                builder.appendNewLine();
            }
            builder.append("Failed to download: ");
            String failureMessage = failures.stream().map(result -> result.url + ", reason: " + result.failureReason).collect(Collectors.joining(". "));
            builder.append(failureMessage);
        }
        return builder.toString();
    }
}
