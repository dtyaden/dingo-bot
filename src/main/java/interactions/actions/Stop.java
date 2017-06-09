package interactions.actions;

import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

public class Stop extends AbstractOperation{

	public Stop(IMessage message) {
		super(message);
	}

	@Override
	public void run() {
		AudioPlayer.getAudioPlayerForGuild(message.getGuild()).skip();
	}

	@Override
	public String getInfo() {
		return "Stop whatever the bot is playing";
	}
	
}
