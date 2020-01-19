package listeners;

import discord4j.core.object.entity.Message;
import dingo.engine.DingoEngine;
import dingo.interactions.actions.SpamThread;
import dingo.interactions.actions.responses.DeleteMessageResponse;

public class MentionListener extends AbstractDingoListener {

	public MentionListener(long clientID){
		super(clientID);
	}

	private boolean mentionsHereOrEveryone(Message message){
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
