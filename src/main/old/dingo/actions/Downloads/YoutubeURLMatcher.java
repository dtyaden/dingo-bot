package dingov2.bot.commands.actions.Downloads;

public class YoutubeURLMatcher extends AbstractRegexArgumentParser{
    public static final String YOUTUBE_REGEX = "[^\\s]*((youtu.be)|(youtube.com)[^\\s]*)";
    public YoutubeURLMatcher(String command){
        super(command, YOUTUBE_REGEX);
    }
}
