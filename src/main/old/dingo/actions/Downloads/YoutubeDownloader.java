package dingov2.bot.commands.actions.Downloads;

import dingo.engine.DingoBotUtil;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;

public class YoutubeDownloader {
    DingoBotUtil util = new DingoBotUtil();
    public static final String YOUTUBE_DL_COMMAND = "source ~/.bashrc; ~/youtube-dl.sh $dingoSoundDir";


    public static final List<String> YOUTUBE_DL_STRING = new ArrayList<String>();
    private Collection<YoutubeDownloadThread> youtubeDownloads;
    static{
        YOUTUBE_DL_STRING.add("/bin/bash");
        YOUTUBE_DL_STRING.add("-i");
        YOUTUBE_DL_STRING.add("-c");
//		YOUTUBE_DL_STRING.add("~/youtube-dl.sh");
    }


    public boolean isYoutubeURL(String url){
        return !new YoutubeURLMatcher(url).getMatches().isEmpty();
    }

    public void saveDownload(String name, String url){
        YoutubeDownloadThread thread = new YoutubeDownloadThread(name, url);
        youtubeDownloads.add(thread);
    }

    public Map<String, String> parseDownloadMessage(String message){
        List<String> url = new ArrayList<>();
        String name = "";
        HashMap<String, String> map = new HashMap<>();
        List<String> names = new ArrayList<>();

        List<String> messageSplit = Arrays.asList(message.toString().split(" "));
        new YoutubeURLMatcher(message).getMatches();

        return map;
    }

    public List<String> extractURLs(String message){
        return new YoutubeURLMatcher(message).getMatches();
    }

    public HashMap<String, String> downloadURL(IMessage message){
        // map of names -> URLs
        HashMap<String, String> downloads = new HashMap<>();
        List<String> messageContent = Arrays.asList(message.toString().split(" "));
        for(int i = 0; i < messageContent.size(); i++) {
            String name = "";
            String link = "";
            String currentPart = messageContent.get(i);
            if(isYoutubeURL(currentPart)) {
                link = currentPart;
                try {
                    if(messageContent.get(i + 1).equals("-name")) {
                        try {
                            // name found, jump the iterator forward two positions so we don't try to parse it again.
                            name = messageContent.get(i + 2);
                            i +=2;
                        }
                        catch (IndexOutOfBoundsException e) {
                            // -name was given but no actual name was given.
                            util.sendErrorMessage(message);
                        }
                    }
                }
                catch(IndexOutOfBoundsException e) {
                    // no name supplied :shrug: we don't need to increase the index here
                }
            }
            else if(currentPart.contains("-name")) {
                try {
                    name = messageContent.get(i + 1);
                    try {
                        link = messageContent.get(i+2);
                        if(!isYoutubeURL(link)) {
                            util.sendErrorMessage(message);
                            break;
                        }
                        // name and link found increase i + 2
                        i += 2;
                    }
                    catch(IndexOutOfBoundsException e) {
                        util.sendErrorMessage(message);
                    }
                }
                catch(IndexOutOfBoundsException e) {
                    util.sendErrorMessage(message);
                }
            }
            if(StringUtils.isNotBlank(link)) {
                // adding link to downloads
                System.out.println("prepping link: " + link + " to be downloaded");
                downloads.put(link, name);
            }
        }
        return downloads;
    }

}
