package interactions.actions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.lang3.StringUtils;

import engine.DingoBotUtil;
import engine.DingoEngine;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.audio.IAudioProcessor;
import sx.blah.discord.handle.audio.impl.DefaultProcessor;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.providers.AudioInputStreamProvider;

public class DingoSoundByte extends AbstractOperation{
	
	public DingoSoundByte(IMessage message) {
		super(message);
	}
	
	@Override
	public void run() {
		IDiscordClient dingo = DingoEngine.getBot();
		IUser author = message.getAuthor();
		
		IVoiceChannel channel = DingoBotUtil.findUserVoiceChannel(author, dingo);
		if(channel == null){
			System.out.println("user channel not found");
			return;
		}
		String [] messageContent = message.getContent().split(" ");
		int startingPosition;
		if(messageContent.length <= 2){
			startingPosition = 1;
		}
		else{
			startingPosition = 2;
		}
		// Fix checking if it's a url and finding the file name if it's not
		if(messageContent[startingPosition].startsWith("http")){
			playURL(message, channel);
		}
		else{
			playAudioFile(message, channel, startingPosition);
		}
	}
	
	public void incrementPlayCount(String file){
		try {
			File f = new File(DingoEngine.FILE_PLAY_COUNT_lOCATION);
			f.createNewFile();
			FileInputStream fis = new FileInputStream(f);
			HashMap<String,Integer> counts;
			try{
				ObjectInputStream ois = new ObjectInputStream(fis);
				counts = (HashMap<String,Integer>) ois.readObject();
			}
			catch(EOFException e){
				counts = new HashMap<String, Integer>();
			}
			Integer playCount = counts.get(file);
			if(playCount != null){
				counts.put(file, ++playCount);
			}
			else{
				counts.put(file, 1);
			}
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(counts);
			oos.flush();
			oos.close();
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void playURL(IMessage message, IVoiceChannel channel){
		channel.sendMessage("I don't understand what a fucking url is");
		return;
	}
	
	public static AudioPlayer getAudioPlayer(IGuild guild){
		AudioPlayer p = AudioPlayer.getAudioPlayerForGuild(guild);
		p.setVolume(Volume.getClampedVolume());
		return p;
	}
	
	public void playAudioFile(IMessage message, IVoiceChannel channel, int startingPosition){
		AudioPlayer p = DingoSoundByte.getAudioPlayer(message.getGuild());
		p.clear();
		String trackName = getTrackName(message, startingPosition).toLowerCase();
		File soundDir = new File(DingoEngine.AUDIO_DIRECTORY);
		
		if(!soundDir.exists()){
			try {
				soundDir.createNewFile();
			} catch (IOException e) {
				System.out.println("SoundDir creation failed!");
				e.printStackTrace();
			}
		}
		try{
			System.out.println("joining channel");
			channel.join();
		}
		catch(Exception e){
			System.out.println("failed to join channel");
			e.printStackTrace();
		}
		File[] tracks = new File(DingoEngine.AUDIO_DIRECTORY).listFiles((file) -> file.getName().toLowerCase().contains(trackName));
		if(tracks!= null && tracks.length > 0){
			try {
				System.out.println(tracks[0].getName() + " playing");
				p.queue(tracks[0]);
				incrementPlayCount(tracks[0].getName());
				if(StringUtils.equals(tracks[0].getName(), "memelord.wav")){
					message.reply("me");
				}
			} catch (DiscordException e){
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			}
			
		}
		else{
			message.getChannel().sendMessage("I don't see a file containing '"+ trackName + "' here...");
		}
	}
	
	@Override
	public String getInfo() {
		return "<FILE NAME OR URL>";
	}
	
	public String getTrackName(IMessage message, int startingPosition){
		String messageString = message.getContent().trim();
		
		String[] messageContent = messageString.split("[' ']+");
		List<String> contentList = Arrays.asList(messageContent);
		
		if(contentList.size() < 2){
			return "";
		}
		
		int startPosition;
		if(contentList.size() < 3){
			startPosition = 1;
		}
		else{
			startPosition = 2;
		}
		
		
		StringBuilder fileName = new StringBuilder();
		for(int i = startPosition ; i < messageContent.length; i++){
			fileName.append(messageContent[i] + " ");
		}
		return fileName.toString().trim();
	}
	
	public void playSound(IVoiceChannel channel, String sound){
		
	}
	
}
