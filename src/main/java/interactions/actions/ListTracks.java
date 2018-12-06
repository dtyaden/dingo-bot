package interactions.actions;

import java.io.File;
import java.util.*;

import engine.DingoBotUtil;
import engine.DingoEngine;
import org.apache.commons.lang3.StringUtils;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

public class ListTracks extends AbstractOperation{

	public ListTracks(IMessage message) {
		super(message);
	}

	DingoBotUtil util = new DingoBotUtil();

    public static int maxMessageLength = 50;
	@Override
	public void run() {
        StringBuilder trackList = new StringBuilder();
        trackList.append("Here are my current meme clips:\n");
        IChannel messageChannel = message.getAuthor().getOrCreatePMChannel();
        List<String> searchArgs = util.getMessageArguments(message);
        List<File> tracks = util.searchSoundFiles(searchArgs);
        Collections.sort(tracks, new Comparator<File>() {
            @Override
            public int compare(File arg0, File arg1) {
                String a = arg0.getName();
                String b = arg1.getName();
                return StringUtils.compareIgnoreCase(a, b);
            }
        });
        List<File> mutableTrackList = new LinkedList<>();
        mutableTrackList.addAll(tracks);
        ListThread thread = new ListThread(mutableTrackList, messageChannel);
        System.out.println("STARTING LIST THREAD~~~~");
        Thread messageSendingThread = new Thread(thread);
        messageSendingThread.start();
    }

	@Override
	public String getInfo() {
		return "The bot will message you a list of all available audio shitposts";
	}

    public class ListThread implements Runnable{
        private List<File> tracks;
        private IChannel channel;
        private long timeBetweenMessages = 500;
        public ListThread(List<File> tracks, IChannel channel){
            this.tracks = tracks;
            this.channel = channel;
        }

        private String getFileNames(List<File> tracks, int maxMessageLength){
            StringBuilder trackList = new StringBuilder();
            int messageLength = 0;
            File currentFile;
            while(!tracks.isEmpty()) {
                currentFile = tracks.remove(0);
                if (messageLength > maxMessageLength) {
                    break;
                }
                trackList.append(currentFile.getName() + '\n');
                messageLength++;
            }
            return trackList.toString();
        }

        private List<MessageBuilder> buildMessages(List<File> tracks, int maxMessageLength){
            List<MessageBuilder> messages = new ArrayList<MessageBuilder>();
            while(!tracks.isEmpty()){
                String trackList = getFileNames(tracks, maxMessageLength);
                MessageBuilder message = buildMessage(trackList);
                messages.add(message);
            }
            return messages;
        }

	    public void run(){
            System.out.println("inside of run method");
            List<MessageBuilder> builtMessages = buildMessages(tracks, ListTracks.maxMessageLength);
            for(MessageBuilder message : builtMessages){
                try {
                    Thread.sleep(getTimeBetweenMessages());
                    System.out.println("sending message?");
                    RequestBuffer.request(() -> {
                        message.send();
                    });
                } catch (InterruptedException e) {
//                    channel.sendMessage("SendListMessage interrupted for some reason?");
                }
            }
        }

        public long getTimeBetweenMessages(){
            return timeBetweenMessages;
        }

        public MessageBuilder buildMessage(String trackList) {
            MessageBuilder messageBuilder = new MessageBuilder(DingoEngine.getBot());
            messageBuilder.withChannel(channel);
            messageBuilder.withContent(trackList.trim());
            return messageBuilder;
        }
    }
}
