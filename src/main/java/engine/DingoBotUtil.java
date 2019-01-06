package engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;

public class DingoBotUtil {

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


}
