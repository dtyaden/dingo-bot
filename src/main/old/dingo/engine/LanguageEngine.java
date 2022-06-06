package dingo.engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dingo.api.base.entity.DingoChannel;
import dingo.api.base.entity.DingoMessage;
import dingo.api.base.entity.DingoUser;
import dingo.interactions.actions.*;
import dingo.interactions.actions.music.*;
import dingo.api.discord4j.Discord4JDingoMessage;
import discord4j.core.object.entity.Message;
import org.apache.commons.lang3.StringUtils;

public class  LanguageEngine{
	public static final HashMap<String, DingoAction> commands = new HashMap<>();
	InputStream input;
	public static final String[] BAD_WORDS = {"fuckboi","fidgetspinner", "fidget spinner", "fidget-spinner",
			"marco rubio"};

	private static void registerCommand(DingoAction action, String...commandKeys){
		for (String command : commandKeys){
			commands.put(command, action);
		}
	}

	static{
		commands.put("mememe", (message)-> new MemeMe(message));
		commands.put("play", (message) -> new DingoSoundByte(message));
		commands.put("stop", (message) -> new Stop(message));
		commands.put("list", (message) -> new ListTracks(message));
		commands.put("pasta", (message) -> new NavySeal(message));
		commands.put("playcount", (message) -> new ListPlayCount(message));
		commands.put("volume", (message) -> new Volume(message));
		commands.put("odouls", (message) -> new ODouls(message));
		commands.put("restart", (message) -> new Restart(message));
		commands.put("search", (message) -> new ListTracks(message));
		commands.put("queue", (message) -> new QueueTrackAction(message));
		registerCommand((message) -> new NowPlaying(message), "trackinfo", "track", "nowplaying", "shazam", "what this", "what_this");
		registerCommand(((message) -> new NextTrackAction(message)), "skip", "next");
		registerCommand(((message) -> new PauseMusicAction(message)), "pause");
	}
	
	List<DingoOperation> actionQueue = new ArrayList<>();
	
	public void parse(Message discord4jMessage){
		DingoMessage message = new Discord4JDingoMessage(discord4jMessage);
		String content = message.getMessageContent();

		String[]  input = content.split(" ");
		
		if(message.getMessageContent().split("[ ]+")[1].toLowerCase().equals("please")){
			AdminPowers power = new AdminPowers(message);
			power.run();
			return;
		}
		
		if(StringUtils.equals(input[1].toLowerCase(),"help")){
			displayHelpMessage(message.getChannel(), message.getAuthor());
			return;
		}
		
		for(int i = 0; i < input.length; i++){
			String word = input[i].toLowerCase();
			DingoAction command = commands.get(word);
			if(command != null){
				Thread operationThread = new Thread();
				command.getOperation(message).run();
				return;
			}
		}
		commands.get("play").getOperation(message).run();
	}
	
	public static void displayHelpMessage(DingoChannel channel, DingoUser fuckboi){
		StringBuilder builder = new StringBuilder();
		builder.append("Usage:\n");
		commands.entrySet().forEach(entry -> builder.append(entry.getKey().toString()).append(" - " + entry.getValue().
				getOperation(null).getInfo()).append("\n"));
		
		channel.sendMessage(builder.toString().trim());
	}
	
	public static String naughtyWordFilter(String[] messageContent) {
		StringBuilder response = new StringBuilder();
		for(String str : messageContent){
			
		}
		return response.toString();
	}
}
