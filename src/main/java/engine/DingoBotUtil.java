package engine;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.RequestBuffer;

public class DingoBotUtil {


    public static final String AUDIO_DIRECTORY = "src/resources/sounds/";

    // dont think this urlregex works...
    public static final String URLREGEX = "(?:(?:https?|ftp):\\/\\/|\\b(?:[a-z\\d]+\\.))(?:(?:[^\\s()<>]+|\\((?:[^\\s()<>]+|(?:\\([^\\s()<>]+\\)))?\\))+(?:\\((?:[^\\s()<>]+|(?:\\(?:[^\\s()<>]+\\)))?\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))?";


    public static IVoiceChannel findUserVoiceChannel(IUser user, IDiscordClient bot) {
        long timeout = DingoEngine.getTimeout();
        List<IVoiceChannel> voiceChannels = bot.getVoiceChannels();
        long startTime = System.currentTimeMillis();
        while (voiceChannels.isEmpty() && System.currentTimeMillis() - startTime < timeout) {
            voiceChannels = bot.getVoiceChannels();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (IVoiceChannel channel : voiceChannels) {
            if (channel.getConnectedUsers().contains(user)) {
                return channel;
            }
        }
        if (voiceChannels.isEmpty()) {
            throw new RuntimeException("Unable to retrieve voice channels");
        }
        return null;
    }

    public List<File> searchSoundFiles(String... searchString) {
        return findFilesMultipleWords(AUDIO_DIRECTORY, Arrays.asList(searchString));
    }

    public List<File> searchSoundFiles(List<String> searchString) {
        return findFilesMultipleWords(AUDIO_DIRECTORY, searchString);
    }

    public List<String> getMessageArguments(IMessage message) {
        String[] messageContent = message.getContent().split("[ ]+");
        List<String> messageArguments = new ArrayList<>();
        int i = 2;
        while(i < messageContent.length){
            messageArguments.add(messageContent[i]);
            i++;
        }
        return messageArguments;
    }

    /**
     * matches all files if no search strings passed
     * @param files
     * @param keywords
     * @return
     */
    public List<File> matchFilenames(List<File> files, List<String> keywords){
        if(keywords.isEmpty()){
            return files;
        }
        return files.stream().filter( file -> {
            for(String keyword : keywords){
                if(file.getName().toLowerCase().contains(keyword.toLowerCase())){
                    return true;
                }
            }
            return false;
        }
        ).collect(Collectors.toList());
    }

    public List<File> findFilesMultipleWords(String directory, List<String> keywords){
        List<File> files = Arrays.asList(new File(directory).listFiles());
        return matchFilenames(files, keywords);
    }

    public MessageBuilder getMessageBuilder(){
        return new MessageBuilder(DingoEngine.getBot());
    }

    public IMessage requestMessageSend(MessageBuilder message){
        List<IMessage> sentMessageContainer = new ArrayList<>();
        RequestBuffer.request(() -> {
            sentMessageContainer.add(message.send());
        });
        return sentMessageContainer.get(0);
    }

    public boolean isMessageContentUrl(String messageContent){
        return messageContent.matches(URLREGEX);
    }

    public URL getUrl(String urlString){
        try {
            URL url = new URL(urlString);
            return url;
        } catch (MalformedURLException e) {
        }
        return null;
    }

    public void sendErrorMessage(MessageReceivedEvent event) {
        sendErrorMessage(event.getMessage());
    }

    public void sendErrorMessage(IMessage message){
        message.reply("format for sending me a link is: \"{link}\" \"-name\" (name is optional btw) \"{the name of the file you want otherwise it will default to the youtube title}\""
                + "\nYou can also switch the position of the name and link around and it should work. You just need to specify the name with '-name' that's the important part really.\n"
                + "You can also send as many links as you want this way too. Also, don't abuse this power please...");
        throw new IllegalArgumentException();
    }
}
