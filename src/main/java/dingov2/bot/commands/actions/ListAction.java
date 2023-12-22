package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.music.AudioTrackUtil;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.TextStringBuilder;
import reactor.core.publisher.Mono;

import java.util.List;

public class ListAction extends AbstractMessageEventAction {

    AudioTrackUtil util;

    int maxFileCount = 25;

    public ListAction(MessageCreateEvent event, List<String> arguments) {
        super(event, arguments);
        util = new AudioTrackUtil();
    }

    @Override
    public Mono<Void> execute() {
        List<String> files = util.searchForFileAllTerms(arguments);
        if (files.isEmpty()) {
            event.getMessage().getChannel().subscribe(channel -> channel.createMessage("Could not find files matching "
                    + StringUtils.join(arguments, " ")).subscribe());
            return Mono.empty();
        }
        StringBuilder fullSearchText = new StringBuilder();
        TextStringBuilder fileList = new TextStringBuilder();
        arguments.forEach(arg -> fullSearchText.append(arg).append(" "));
        event.getMessage().getChannel().subscribe(channel -> {
            sendInitialMessage(channel, fullSearchText.toString());
            int fileCount = 0;
            for (String file : files) {
                if (fileCount >= maxFileCount) {
                    String fileListBuilt = fileList.toString();
                    channel.createMessage(fileListBuilt).subscribe();
                    fileList.clear();
                    fileCount = 0;
                }
                fileList.appendln(file);
                fileCount++;
            }
            channel.createMessage(fileList.toString()).subscribe();
        });
        return Mono.empty();
    }

    public void sendInitialMessage(MessageChannel channel, String fullSearchText) {
        String message = "";
        if (fullSearchText.isBlank()) {
            message = "Here are the files I have... sorry for the spam but you asked for it...";
        } else {
            message = "searching for files containing " + fullSearchText;
        }
        channel.createMessage(message).subscribe();
    }
}
