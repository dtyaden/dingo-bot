package dingov2.bot.commands.actions;

import dingo.api.base.entity.DingoMessage;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

public class Stop extends AbstractOperation{

	public Stop(DingoMessage message) {
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
