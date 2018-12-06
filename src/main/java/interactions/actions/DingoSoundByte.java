package interactions.actions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import engine.DingoBotUtil;
import engine.DingoEngine;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.audio.IAudioManager;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.audio.AudioPlayer;


public class DingoSoundByte extends AbstractOperation{
	
	public DingoSoundByte(IMessage message) {
		super(message);
	}

	private DingoBotUtil util = new DingoBotUtil();

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
	
	public class DingoAudioPlayer extends AudioPlayer {

		public DingoAudioPlayer(IAudioManager manager) {
			super(manager);
		}
		
		
	}
	
	public AudioPlayer getAudioPlayer(IGuild guild){
		return AudioPlayer.getAudioPlayerForGuild(guild);
	}
	
	public void playAudioFile(IMessage message, IVoiceChannel channel, int startingPosition){
		String trackName = getTrackName(message, startingPosition).toLowerCase();
		File soundDir = new File(DingoBotUtil.AUDIO_DIRECTORY);
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
			// Maybe we need to check if we're connected before stopping trying to join
			while(!channel.isConnected()) {
				channel.join();
			}
		}
		catch(Exception e){
			System.out.println("failed to join channel");
			e.printStackTrace();
		}
		
		File track = searchForFile(trackName);
		if(track!= null){
			try {
				System.out.println(track.getName() + " playing");
				AudioPlayer p = getAudioPlayer(message.getGuild());
				if (p.getCurrentTrack() != null) {
					System.out.println("track playing queueing pan");
					p.clear();
					File pan = searchForFile("pan");
					if(!pan.equals(track)) {
						p.queue(pan);
					}
				}
				p.setVolume(Volume.getClampedVolume());
				p.queue(track);
				incrementPlayCount(track.getName());
				if(StringUtils.equals(track.getName(), "memelord.wav")){
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
	
	public File searchForFile(String trackName) {
		Character[] characters = ArrayUtils.toObject(trackName.toCharArray());
		List<Character> charList = Arrays.asList(characters);
		String[] arrayOfStrings = {"this", "is", "an", "array", "of", "strings"};
		List<String> list = Arrays.asList(arrayOfStrings);
		HashSet<Character> trackCharacters = new HashSet<>(charList);
		List<File> tracks = util.searchSoundFiles(trackName);
		File closestTrack = new File("");
		int closestDistance = 999;
		if(tracks.size() != 0) {
			System.out.println("found some tracks: " + Arrays.asList(tracks));
			for(File f : tracks) {
				int distance = LevenshteinDistance.getDefaultInstance().apply(f.getName(), closestTrack.getName());
				if(distance < closestDistance) {
					System.out.println("found closeer track: " + f.getName()  + " distance from  " + trackName + " is "+ distance);
					closestDistance = distance;
					closestTrack = f;
				}
			}
		}
		else {
			return null;
		}
		return closestTrack;
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
}
