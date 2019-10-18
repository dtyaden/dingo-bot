package engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import interactions.actions.DownloadAction;
import interactions.actions.DownloadThread;
import interactions.actions.Downloads.YoutubeDownloader;
import interactions.actions.SendMessageAction;
import org.apache.commons.lang3.StringUtils;

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

	/**
	 * Fix this shit david.
	 * @return
	 */
	public HashMap<String, String> downloadURL(String message){
		return new HashMap<>();
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
		HashMap<String, String> urls = downloadURL(message.getContent());
		System.out.println("it looks like I found a/some link(s): " + urls);
		Set<Entry<String, String>> entries = urls.entrySet();
		for(Entry<String, String> entry : entries) {
			System.out.println("1");
			List<String> fullCommandList = new ArrayList<>();
			System.out.println("2");
			fullCommandList.addAll(YoutubeDownloader.YOUTUBE_DL_STRING);
			String scriptWithParameters = YoutubeDownloader.YOUTUBE_DL_COMMAND + " " + entry.getKey() + " " + entry.getValue();
			fullCommandList.add(scriptWithParameters);
			System.out.println("about to try processing command: " + fullCommandList);

			System.out.println("running command: " + fullCommandList);
			Thread thread = new DownloadThread(fullCommandList,event, entry.getKey());
			thread.start();
		}
	}

}
