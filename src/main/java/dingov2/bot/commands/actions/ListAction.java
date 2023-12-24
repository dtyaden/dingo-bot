package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

public class ListAction extends AbstractAction {

    AudioTrackUtil util;

    int maxFileCount = 25;

    public ListAction(DingoEventWrapper event, List<String> arguments) {
        super(event, arguments);
        util = new AudioTrackUtil();
    }

    @Override
    public Mono<Void> execute() {
        List<String> files = util.searchForFileAllTerms(arguments);
        if (files.isEmpty()) {
            return event.reply("Could not find files matching " + StringUtils.join(arguments, " "));
        }
        StringBuilder fullSearchText = new StringBuilder();
        TextStringBuilder fileList = new TextStringBuilder();
        arguments.forEach(arg -> fullSearchText.append(arg).append(" "));
        sendInitialMessage(fullSearchText.toString());
        int fileCount = 0;
        for (String file : files) {
            if (fileCount >= maxFileCount) {
                String fileListBuilt = fileList.toString();
                event.reply(fileListBuilt).subscribe();
                fileList.clear();
                fileCount = 0;
            }
            fileList.appendln(file);
            fileCount++;
        }
        return event.reply(fileList.toString());
    }

    public void sendInitialMessage(String fullSearchText) {
        String message = "";
        if (fullSearchText.isBlank()) {
            message = "Here are the files I have... sorry for the spam but you asked for it...";
        } else {
            message = "searching for files containing " + fullSearchText;
        }
        event.reply(message).subscribe();
    }
}
