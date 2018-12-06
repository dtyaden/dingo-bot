package listeners;

import engine.DingoEngine;
import interactions.actions.SpamThread;
import interactions.actions.responses.DeleteMessageResponse;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MentionEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MentionListener implements IListener<MentionEvent>{
	private long clientID;
	
	
	public MentionListener(long clientID){
		this.clientID = clientID;
	}

	private boolean mentionsHereOrEveryone(IMessage message){
	    return message.mentionsEveryone() || message.mentionsHere();
    }

    private boolean mentionsMoreThanOnePerson(IMessage message){
	    return message.getMentions().size() > 1;
    }

    private boolean mentionsOnlyDingoBot(IMessage message){
        return !mentionsHereOrEveryone(message) && !mentionsMoreThanOnePerson(message);
    }

	@Override
	public void handle(MentionEvent event) {
		IMessage message = event.getMessage();
        if(mentionsOnlyDingoBot(message)){
            new Thread(new DeleteMessageResponse(message)).start();
        }
        else{
            // Don't handle mentions that include other users than dingo bot.
            return;
        }
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
