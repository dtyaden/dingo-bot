package engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import interactions.actions.DingoAction;
import interactions.actions.DingoOperation;
import interactions.actions.DingoSoundByte;
import interactions.actions.MemeMe;
import interactions.actions.Stop;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class LanguageEngine {
	public static final HashMap<String, DingoAction> commands = new HashMap<>();
	InputStream input;
	
	static{
		commands.put("mememe", (message)-> new MemeMe(message));
		commands.put("play", (message) -> new DingoSoundByte(message));
		commands.put("stop", (message) -> new Stop(message));
		commands.put("list", (message) -> new ListTracks(message));
		commands.put("pasta", (message) -> new NavySeal(message));
		commands.put("playcount", (message) -> new ListPlayCount(message));
	}
	
	List<DingoOperation> actionQueue = new ArrayList<>();
	
	public void parse(IMessage message){
		String[] validUsers = {"Giantdad", "giantdad"};
		List<String> admin = Arrays.asList(validUsers);
		String content = message.getContent().trim();
		String[]  input = content.split(" ");
		String authorName = message.getAuthor().getName();
		if(StringUtils.equals(message.getContent(), "shutdown") && admin.contains(authorName)){
			DingoEngine.stopDingo();
			return;
		}
		for(int i = 0; i < input.length; i++){
			String word = input[i].toLowerCase();
			DingoAction command = commands.get(word);
			if(command != null){
				command.getOperation(message).run();
				return;
			}
		}
		displayHelpMessage(message.getChannel(), message.getAuthor());
	}
	
	public static void displayHelpMessage(IChannel channel, IUser fuckboi){
		StringBuilder builder = new StringBuilder();
		builder.append("Usage:\n");
		commands.entrySet().forEach(entry -> builder.append(entry.getKey().toString() + "\n"));
		channel.sendMessage(builder.toString().trim());
	}
}
