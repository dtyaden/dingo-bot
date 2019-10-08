package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import interactions.actions.DownloadAction;
import interactions.actions.DownloadThread;
import interactions.actions.SendMessageAction;
import org.apache.commons.lang3.StringUtils;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;

public class MessageListener implements IListener<MessageReceivedEvent> {
	String previousID;

	public void downloadAttachments(IMessage message){
		List<IMessage.Attachment> attachments = message.getAttachments();
		if(attachments.size() > 0){
			System.out.println("RECEIVING ATTACHMENTS: " + attachments);
			attachments.forEach(attachment -> {
				attachment.getLongID();
				String url = attachment.getUrl();
				boolean downloaded = new DownloadAction().download(url);
				if(!downloaded){
					new SendMessageAction().sendMessage(DingoEngine.getBot(), message.getAuthor(), "Your upload failed, probably because David is bad at programming.");
				}
				else{

				}
			});
			return;
		}
	}

	public HashMap<String, String> downloadURL(IMessage message){
		// map of names -> URLs
		HashMap<String, String> downloads = new HashMap<>();
		List<String> messageContent = Arrays.asList(message.toString().split(" "));
		for(int i = 0; i < messageContent.size(); i++) {
			String name = "";
			String link = "";
			String currentPart = messageContent.get(i);
			if(currentPart.contains("youtube.com") || currentPart.contains("youtu.be")) {
				link = currentPart;
				try {
					if(messageContent.get(i + 1).equals("-name")) {
						try {
							// name found, jump the iterator forward two positions so we don't try to parse it again.
							name = messageContent.get(i + 2);
							i +=2;
						}
						catch (IndexOutOfBoundsException e) {
							// -name was given but no actual name was given.
							sendErrorMessage(message);
						}
					}
				}
				catch(IndexOutOfBoundsException e) {
					// no name supplied :shrug: we don't need to increase the index here
				}
			}
			else if(currentPart.contains("-name")) {
				try {
					name = messageContent.get(i + 1);
					try {
						link = messageContent.get(i+2);
						if(!link.contains("youtube.com") || !link.contains("youtu.be")) {
							sendErrorMessage(message);
							break;
						}
						// name and link found increase i + 2
						i += 2;
					}
					catch(IndexOutOfBoundsException e) {
						sendErrorMessage(message);
					}
				}
				catch(IndexOutOfBoundsException e) {
					sendErrorMessage(message);
				}
			}
			if(StringUtils.isNotBlank(link)) {
				// adding link to downloads
				System.out.println("prepping link: " + link + " to be downloaded");
				downloads.put(link, name);
			}
		}
		return downloads;
	}

	@Override
	public void handle(MessageReceivedEvent event) {
		IMessage message = event.getMessage();
		String content = message.getContent();
		System.out.println("DIRECT MESSAGE CONTENT: " + content);
		String ID = message.getStringID();
		if(StringUtils.equals(ID, previousID)){
			return;
		}
		if(!event.getChannel().isPrivate()){
			return;
		}
		previousID = ID;

		downloadAttachments(message);

		System.out.println("checking if it's a link...");
		HashMap<String, String> urls = downloadURL(message);
		System.out.println("it looks like I found a/some link(s): " + urls);
		Set<Entry<String, String>> entries = urls.entrySet();
		for(Entry<String, String> entry : entries) {
			System.out.println("1");
			List<String> fullCommandList = new ArrayList<>();
			System.out.println("2");
			fullCommandList.addAll(YOUTUBE_DL_STRING);
			String scriptWithParameters = YOUTUBE_DL_COMMAND + " " + entry.getKey() + " " + entry.getValue();
			fullCommandList.add(scriptWithParameters);
			System.out.println("about to try processing command: " + fullCommandList);

			System.out.println("running command: " + fullCommandList);
			Thread thread = new DownloadThread(fullCommandList,event, entry.getKey());
			thread.start();
		}
	}

}
