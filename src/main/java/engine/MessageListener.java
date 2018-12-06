package engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import interactions.actions.DownloadAction;
import interactions.actions.SendMessageAction;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;

public class MessageListener implements IListener<MessageReceivedEvent> {
	String previousID;
	private static final List<String> YOUTUBE_DL_STRING = new ArrayList<String>();
	static{
		YOUTUBE_DL_STRING.add("/bin/bash");
		YOUTUBE_DL_STRING.add("-i");
		YOUTUBE_DL_STRING.add("-c");
//		YOUTUBE_DL_STRING.add("~/youtube-dl.sh");
	}
	private static final String YOUTUBE_DL_COMMAND = "source ~/.bashrc; ~/youtube-dl.sh $dingoSoundDir";
	private static final String PLEASE_NO_REACTION = "<a:pleaseno:441795866404323338>";
	private static void sendErrorMessage(MessageReceivedEvent event) {
		ReactionEmoji.of(PLEASE_NO_REACTION);
		event.getMessage().reply("format for sending me a link is: \"{link}\" \"-name\" (name is optional btw) \"{the name of the file you want otherwise it will default to the youtube title}\""
			+ "\nYou can also switch the position of the name and link around and it should work. You just need to specify the name with '-name' that's the important part really.\n"
			+ "You can also send as many links as you want this way too. Also, don't abuse this power please...");
		throw new IllegalArgumentException();
	}
	
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
				else{

				}
			});
		}
		else{
			List<String> message = Arrays.asList(event.getMessage().toString().split(" "));
			// map of names -> URLs
			HashMap<String, String> downloads = new HashMap<>();
			System.out.println("checking if it's a link...");
			for(int i = 0; i < message.size(); i++) {
				String name = "";
				String link = "";
				String currentPart = message.get(i);
				if(currentPart.contains("youtube.com") || currentPart.contains("youtu.be")) {
					link = currentPart;
					try {
						if(message.get(i + 1).equals("-name")) {
							try {
								// name found, jump the iterator forward two positions so we don't try to parse it again.
								name = message.get(i + 2);
								i +=2;
							}
							catch (IndexOutOfBoundsException e) {
								// -name was given but no actual name was given.
								sendErrorMessage(event);
							}
						}
					}
					catch(IndexOutOfBoundsException e) {
						// no name supplied :shrug: we don't need to increase the index here
					}
				}
				else if(currentPart.contains("-name")) {
					try {
						name = message.get(i + 1);
							try {
								link = message.get(i+2);
								if(!link.contains("youtube.com") || !link.contains("youtu.be")) {
									sendErrorMessage(event);
									break;
								}
								// name and link found increase i + 2
								i += 2;
							}
							catch(IndexOutOfBoundsException e) {
								sendErrorMessage(event);
							}
					}
					catch(IndexOutOfBoundsException e) {
						sendErrorMessage(event);
					}
				}
				if(StringUtils.isNotBlank(link)) {
					// adding link to downloads
					System.out.println("prepping link: " + link + " to be downloaded");
					downloads.put(link, name);
				}
			}
			if(downloads.isEmpty()) {
				event.getMessage().reply("what are you trying to do with me?!");
				return;
			}
			System.out.println("it looks like I found a/some link(s): " + downloads);
			Set<Entry<String, String>> entries = downloads.entrySet();
			for(Entry<String, String> entry : entries) {
				System.out.println("1");
				List<String> fullCommandList = new ArrayList<>();
				System.out.println("2");
				fullCommandList.addAll(YOUTUBE_DL_STRING);
				String scriptWithParameters = YOUTUBE_DL_COMMAND + " " + entry.getKey() + " " + entry.getValue();
				fullCommandList.add(scriptWithParameters);
				System.out.println("about to try processing command: " + fullCommandList);
				Process process;
				try {
					System.out.println("running command: " + fullCommandList);
					process = new ProcessBuilder().command(fullCommandList).start();
					System.out.println(process.waitFor());
					System.out.println(IOUtils.toString(process.getErrorStream(), "UTF-8"));
					System.out.println("input stream (output from process): ");
					System.out.println(IOUtils.toString(process.getInputStream(), "UTF-8"));
                    new SendMessageAction().sendMessage(DingoEngine.getBot(), event.getMessage().getAuthor(), "Upload of "
                            + entry.getKey() + "should be done by now?");
				} catch (IOException e) {
					System.out.println("IOEXCEPTION: " + e.getMessage());
					e.printStackTrace();
				} catch (InterruptedException e) {
					System.out.println("INTERRUPTED EXCEPTION: " + e.getMessage());
					e.printStackTrace();
				}
			}
		}
		
	}

}
