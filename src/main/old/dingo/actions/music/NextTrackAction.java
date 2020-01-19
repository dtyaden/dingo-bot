package dingov2.bot.commands.actions.music;

import dingo.api.base.entity.DingoMessage;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

public class NextTrackAction extends AbstractOperation {

    public NextTrackAction(DingoMessage message) {
        super(message);
    }

    @Override
    public void run() {
        AudioPlayer.getAudioPlayerForGuild(message.getGuild()).skip();
    }

    @Override
    public String getInfo() {
        return "play the next track in the queue";
    }
}
