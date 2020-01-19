package dingov2.bot.commands.actions;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.MessageBuilder;

public class SendMessageAction {
	
	public boolean sendMessage(IDiscordClient dingo, IUser user, String content){
		MessageBuilder message = new MessageBuilder(dingo);
		message.withContent(content);
		IPrivateChannel channel = user.getOrCreatePMChannel();
		message.withChannel(channel);
		message.send();
		return true;
	}
	
}
