package engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import interactions.actions.AbstractOperation;
import interactions.actions.DingoAction;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;

public class AdminPowers extends AbstractOperation{

	public static final String SUPREME_RULER = "giantdad";
	public static List<String> LACKEYS = Arrays.asList(new String[] {"lil' mustad", "connor"});
	public static final String COMMAND = "please";
	
	private static HashMap<String, DingoAction> secretActions = new HashMap<>();
	static{
		secretActions.put(SpamOdouls.COMMAND, (message) -> new SpamOdouls(message));
	}
	public AdminPowers(IMessage message) {
		super(message);
	}
	
	private static HashMap<String, DingoAction> superSecretActions = new HashMap<>();
	
	
	public boolean assertPowerIsValid(String authorDisplayName){
		return LACKEYS.contains(authorDisplayName) || StringUtils.equals(authorDisplayName, SUPREME_RULER);
	}
	
	@Override
	public void run() {
		String authorName = message.getAuthor().getDisplayName(message.getGuild()).toLowerCase();
		boolean validLackey = this.assertPowerIsValid(authorName);
		
		if(!validLackey){
			message.reply("YOUR POWER IS NOT LEGITIMATE, DO NOT DARE SPEAK TO ME AGAIN!");
			return;
		}
		
		List<String> content = Arrays.asList(message.getContent().trim().split("[ ]+"));
		
		for(String part : content){
			DingoAction action = secretActions.get(part.toLowerCase());
			if(action != null){
				action.getOperation(message).run();
				return;
			}
		}
		MessageBuilder builder = new MessageBuilder(DingoEngine.getBot());
		builder.withChannel(message.getAuthor().getOrCreatePMChannel());
		builder.withContent("That was not a valid secret command. Are you sure you deserve your power?!");
		builder.send();
	}

	@Override
	public String getInfo() {
		return "secret powers";
	}
	
	
	
}
