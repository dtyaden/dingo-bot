package dingov2.bot.commands.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dingo.engine.DingoEngine;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;

public class SpamOdouls extends AbstractOperation{

	public static final String COMMAND = "spamodouls";
	
	public SpamOdouls(IMessage message) {
		super(message);
	}

	@Override
	public void run() {
		List<String> content = Arrays.asList(message.getContent().trim().toLowerCase().split("[ ]+"));
		Set<String> commandArguments = new HashSet<String>();
		int commandStart = -1;
		for(int i = 0; i < content.size(); i++ ){
			if(content.get(i).equals(COMMAND)){
				commandStart = ++i;
				break;
			}
		}
		
		for(int i = commandStart; i < content.size(); i++){
			commandArguments.add(content.get(i).toLowerCase());
		}
		
		IGuild guild = message.getGuild();
		// Get a set lowercase names of users in channel with whitespace trimmed
		Map<String, IUser> displayNamesToUsers = new HashMap<>();
		guild.getUsers().forEach(user -> displayNamesToUsers.put(user.getDisplayName(guild).toLowerCase().trim(), user));
		
		// Get intersection of users in server and targeted by commands.
		Set<String> usersInChannel = displayNamesToUsers.keySet();
		usersInChannel.retainAll(commandArguments);
		
		for(String user : usersInChannel){
			IUser target = displayNamesToUsers.get(user);
			IChannel privateMessage = target.getOrCreatePMChannel();
			
			MessageBuilder builder = new MessageBuilder(DingoEngine.getBot());
			builder.withChannel(privateMessage);
			builder.withContent("Have some fresh O'Douls™!");
			IMessage message = builder.build();
			
			//send 5 random images of odouls to the victim.
			for(int i = 0; i < 5; i++){
				ODouls spam = new ODouls(message);
				spam.run();
			}
		}
	}

	@Override
	public String getInfo() {
		return "cannot tell you...";
	}

}
