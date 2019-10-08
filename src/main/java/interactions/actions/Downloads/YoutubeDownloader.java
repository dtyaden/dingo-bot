package interactions.actions.Downloads;

import engine.DingoBotUtil;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IMessage;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YoutubeDownloader {
    DingoBotUtil util = new DingoBotUtil();
    public static final String YOUTUBE_DL_COMMAND = "source ~/.bashrc; ~/youtube-dl.sh $dingoSoundDir";
    public static final String YOUTUBE_REGEX = "[^\\s]*((youtu.be)|(youtube.com)[^\\s]*)";
    public static final String NAME_REGEX = "(-name)[\\s]+(([\"]([^\"])*[\"]{1})|([\\w])+)";
    private static final List<String> YOUTUBE_DL_STRING = new ArrayList<String>();
    private Collection<YoutubeDownloadThread> youtubeDownloads;
    static{
        YOUTUBE_DL_STRING.add("/bin/bash");
        YOUTUBE_DL_STRING.add("-i");
        YOUTUBE_DL_STRING.add("-c");
//		YOUTUBE_DL_STRING.add("~/youtube-dl.sh");
    }


    public boolean isYoutubeURL(String url){
        return StringUtils.contains(url, YOUTUBE_REGEX);
    }

    public boolean isName(){

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
        url = extractURLs(messageSplit);

        return map;
    }

    public List<String> extractURLs(String message){
        Matcher youtubeMatcher = Pattern.compile(YOUTUBE_REGEX).matcher(message);
        youtubeMatcher.find();
        youtubeMatcher.match(1)
    }

    /**
     * Grabs a string containing one of the youtube url formats
     * @param messageSplit
     * @return
     */
    public Collection<String> extractURLs(List<String> messageSplit){
        String url = "";
        messageSplit.iterator();
        List<String> URLs = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for(String messagePart : messageSplit){
            if (isYoutubeURL(messagePart)){
                url = messagePart;
                URLs.add(url);
            }
            if()
        }
        return url;
    }

    /**
     * finds -name and grabs everything after it. If an entire message is passed in this way, it's advised to remove
     * the url first or else it'll be included in the name
     * @param messageSplit
     * @return
     */
    public String findName(List<String> messageSplit) {
        String name = "";
        for(String messagePart : messageSplit){
            if()
        }
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
