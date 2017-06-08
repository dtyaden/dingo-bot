package engine;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MessageListener implements IListener<MessageReceivedEvent> {
	String previousID;
	@Override
	public void handle(MessageReceivedEvent event) {
		String content = event.getMessage().getContent();
		System.out.println("DIRECT MESSAGE CONTENT: " + content);
		String ID = event.getMessage().getStringID();
		if(StringUtils.equals(ID, previousID)){
			return;
		}
		if(!event.getChannel().isPrivate()){
			return;
		}
		previousID = ID;
		List<IMessage.Attachment> attachments = event.getMessage().getAttachments();
		if(attachments.size() > 0){
			System.out.println("RECEIVING ATTACHMENTS: " + attachments);
			attachments.forEach(attachment -> {
				attachment.getLongID();
				String url = attachment.getUrl();
				boolean downloaded = new DownloadAction().download(url);
				if(!downloaded){
					new SendMessageAction().sendMessage(DingoEngine.getBot(), event.getAuthor(), "Your upload failed, probably because David is bad at programming.");
				}
			});
		}
		else{
			LanguageEngine parser = new LanguageEngine();
			parser.parse(event.getMessage());
		}
		
	}

}
