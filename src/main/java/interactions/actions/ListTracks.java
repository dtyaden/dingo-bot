package interactions.actions;

import java.io.File;
import java.util.*;

import engine.DingoBotUtil;
import org.apache.commons.lang3.StringUtils;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;

public class ListTracks extends AbstractOperation{

	public ListTracks(IMessage message) {
		super(message);
	}

	DingoBotUtil util = new DingoBotUtil();

    public static int maxMessageLength = 50;
	@Override
	public void run() {
        StringBuilder trackList = new StringBuilder();
        trackList.append("Here are my current meme clips:\n");
        IChannel messageChannel = message.getAuthor().getOrCreatePMChannel();
        List<String> searchArgs = util.getMessageArguments(message);
        List<File> tracks = util.searchSoundFiles(searchArgs);
        Collections.sort(tracks, new Comparator<File>() {
            @Override
            public int compare(File arg0, File arg1) {
                String a = arg0.getName();
                String b = arg1.getName();
                return StringUtils.compareIgnoreCase(a, b);
            }
        });
        List<File> mutableTrackList = new LinkedList<>();
        mutableTrackList.addAll(tracks);
        SendMessageThread thread = new SendMessageThread(mutableTrackList, messageChannel);
        System.out.println("STARTING LIST THREAD~~~~");
        Thread messageSendingThread = new Thread(thread);
        messageSendingThread.start();
    }

	@Override
	public String getInfo() {
		return "The bot will message you a list of all available audio shitposts";
	}

}
