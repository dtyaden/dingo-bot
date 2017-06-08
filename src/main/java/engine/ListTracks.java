package engine;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import interactions.actions.AbstractOperation;
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
		tracks.forEach(file -> trackList.append(file.getName()+ '\n'));
		MessageBuilder messageBuilder = new MessageBuilder(DingoEngine.getBot());
		messageBuilder.withChannel(message.getAuthor().getOrCreatePMChannel());
		messageBuilder.withContent(trackList.toString().trim());
		messageBuilder.send();
	}

	@Override
	public String getInfo() {
		return null;
	}

}
