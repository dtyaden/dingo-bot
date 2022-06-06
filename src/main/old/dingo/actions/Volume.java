package dingov2.bot.commands.actions;

import java.util.Arrays;
import java.util.List;

import dingo.api.base.entity.DingoMessage;
import dingo.engine.DingoEngine;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.audio.AudioPlayer;

public class Volume extends AbstractOperation{
	
	public static final float MAX_VOLUME = 100;
	public static final float MIN_VOLUME = 0;
	public static float AUDIOVOLUME = 1;

	public Volume(DingoMessage message) {
		super(message);
	}

	@Override
	public void run() {
		parseVolume(message);
	}

	@Override
	public String getInfo() {
		return "Set the volume of the audio player.\nExample: @dingo-bot-9000 volume 69";
	}
	
	public String getVolumeMessage(){
		return "The volume is at: " + getVolumeHuman();
	}
	
	public void parseVolume(IMessage message){
		List<String> content = Arrays.asList(message.getContent().split("[ ]+"));
		if(content.size() < 3){
			message.reply(getVolumeMessage());
			return;
		}
		
		setVolume(Float.parseFloat(content.get(2)));
		
		AudioPlayer p = AudioPlayer.getAudioPlayerForGuild(message.getGuild());
		p.setVolume(getClampedVolume());
		MessageBuilder builder = new MessageBuilder(DingoEngine.getBot());
		builder.withChannel(message.getChannel());
		builder = builder.appendContent("Volume has been set to: " + getVolumeHuman());
		builder.send();
	}
	
	/**
	 * Restricts volume to a range of 0-1
	 * @param value
	 * @return
	 */
	private static float clampVolume(float value){
		if(value >= MAX_VOLUME){
			value = MAX_VOLUME;
		}
		else if(value < MIN_VOLUME){
			return MIN_VOLUME;
		}
		return value/100;
	}
	
	public static void setVolume(float volume){
		AUDIOVOLUME = volume;
	}
	
	/**
	 * returns 0-100 value for volume
	 * @return
	 */
	public static float getVolumeHuman(){
		return AUDIOVOLUME;
	}
	
	public static float getClampedVolume(){
		return clampVolume(AUDIOVOLUME);
	}
}
