package listeners;

import java.util.HashMap;

import engine.DingoEngine;
import engine.SpamThread;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class MentionListener implements IListener<MentionEvent>{
	private long clientID;
	
	
	public MentionListener(long clientID){
		this.clientID = clientID;
	}
	
	@Override
	public void handle(MentionEvent event) {
		IMessage message = event.getMessage();
		System.out.println("RECEIVED MENTION OF: "+message);
		message.getAuthor();
		boolean spamming = SpamThread.checkUser(event.getAuthor());
		if(spamming){
			return;
		}
		long authorID = message.getAuthor().getLongID();
		if(authorID != clientID){
			if(DingoEngine.debug){
				event.getChannel().sendMessage("I saw the message: '" + message.getContent() + "' from a fuckboy named: " + event.getAuthor());
			}
			DingoEngine.languageEngine.parse(event.getMessage());
		}
	}
	
}
