package engine;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;

public class ListTracks extends AbstractOperation{

	public ListTracks(IMessage message) {
		super(message);
	}

	@Override
	public void run() {
		StringBuilder trackList = new StringBuilder();
		trackList.append("Here are my current meme clips:\n");
		List<File> tracks = Arrays.asList(new File(DingoEngine.AUDIO_DIRECTORY).listFiles());
		Collections.sort(tracks, new Comparator<File>(){

			@Override
			public int compare(File arg0, File arg1) {
				String a = arg0.getName();
				String b = arg1.getName();
				return StringUtils.compareIgnoreCase(a, b);
			}
			
		});
		int messageLength = 0;
		for(File track : tracks) {
			if(messageLength > 50) {
				messageLength = 0;
				sendMessage(message, trackList.toString());
				trackList.setLength(0);
			}
			trackList.append(track.getName() + '\n');
			messageLength++;
		}
		sendMessage(message, trackList.toString());
	}
	
	public void sendMessage(IMessage message, String trackList) {
		MessageBuilder messageBuilder = new MessageBuilder(DingoEngine.getBot());
		messageBuilder.withChannel(message.getAuthor().getOrCreatePMChannel());
		messageBuilder.withContent(trackList.toString().trim());
		messageBuilder.send();
	}
	
	@Override
	public String getInfo() {
		return "The bot will message you a list of all available audio shitposts";
	}

}
