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
import interactions.actions.ODouls;
import interactions.actions.Stop;
import interactions.actions.Volume;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;

public class  LanguageEngine{
	public static final HashMap<String, DingoAction> commands = new HashMap<>();
	InputStream input;
	public static final String[] BAD_WORDS = {"fuckboi","fidgetspinner", "fidget spinner", "fidget-spinner",
			"marco rubio"};
	
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
	}
	
	List<DingoOperation> actionQueue = new ArrayList<>();
	
	public void parse(IMessage message){
		String content = message.getContent().trim();
		String[]  input = content.split(" ");
		
		if(message.getContent().split("[ ]+")[1].toLowerCase().equals("please")){
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
				command.getOperation(message).run();
				return;
			}
		}
		commands.get("play").getOperation(message).run();
	}
	
	public static void displayHelpMessage(IChannel channel, IUser fuckboi){
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
