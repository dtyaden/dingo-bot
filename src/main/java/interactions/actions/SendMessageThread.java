package interactions.actions;

import engine.DingoEngine;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendMessageThread implements Runnable{
    private List<File> tracks;
    private IChannel channel;
    private long timeBetweenMessages = 500;
    public SendMessageThread(List<File> tracks, IChannel channel){
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
