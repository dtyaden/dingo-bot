package dingov2.bot.services;

import dingov2.discordapi.DingoEventWrapper;
import dingov2.util.DingoStringUtils;

public class SendMessageService {

    private final DingoEventWrapper event;
    private final String message;
    private static final int maxMessageLength = 1500;

    public SendMessageService(DingoEventWrapper event, String message){
        this.event = event;
        this.message = message;
    }

    public void chopAndSendMessage(){
        for (String messagePart : DingoStringUtils.SplitStringsByLength(message, maxMessageLength)){
            event.reply(messagePart).subscribe();
        }
    }
}
