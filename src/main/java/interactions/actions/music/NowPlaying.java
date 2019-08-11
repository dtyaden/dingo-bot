package interactions.actions.music;

import engine.DingoBotUtil;
import engine.DingoEngine;
import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

import java.util.Collection;

public class NowPlaying extends AbstractOperation {

    private IGuild guild;
    private AudioUtils audioUtils;
    private IChannel channel;
    private DingoBotUtil util;
    public NowPlaying(IMessage message) {
        super(message);
        this.guild = message.getGuild();
        audioUtils = new AudioUtils();
        channel = message.getAuthor().getOrCreatePMChannel();
        util = new DingoBotUtil();
    }

    @Override
    public void run() {
        Collection<String> trackMetadata = audioUtils.getTrackMetadata(guild);
        String formattedTrackNames = formatTrackNames(trackMetadata);
        sendMessage(formattedTrackNames);
    }

    private IMessage sendMessage(String formattedTrackNames){
        IMessage sentMessage = util.requestMessageSend(getMessage(formattedTrackNames));
        return sentMessage;
    }

    private MessageBuilder getMessage(String formattedTrackNames){
        MessageBuilder builder = util.getMessageBuilder();
        builder.appendContent(formattedTrackNames);
        return builder;
    }

    public String formatTrackNames(Collection<String> trackMetadata){
        StringBuilder formattedTrackNames = new StringBuilder();
        for(String metadata : trackMetadata){
            formattedTrackNames.append(formatTrackMetadata(metadata));
            formattedTrackNames.append("\n");
        }
        return formattedTrackNames.toString().trim();
    }

    private String formatTrackMetadata(String metadata){
        //here in case stuff gets more complicated or needs to be... idk
        return metadata.trim();
    }

    @Override
    public String getInfo() {
        return "Gets the currently queued up tracks";
    }

}
