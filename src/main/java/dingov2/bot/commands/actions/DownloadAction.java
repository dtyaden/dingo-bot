package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.services.downloads.DownloadService;
import dingov2.bot.services.downloads.validation.DownloadResults;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.bot.services.downloads.YoutubeDownloaderService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.core.spec.legacy.LegacyMessageCreateSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.TextStringBuilder;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DownloadAction extends AbstractMessageEventAction {

    private final AudioTrackUtil audioTrackUtil;
    private final DownloadService downloadUtil;
    YoutubeDownloaderService downloaderService;
    private String errorMessage = "Dude, you didn't even pass me something to download. Fuck off. jfc.......";
    public DownloadAction(MessageCreateEvent event) {
        super(event);
        audioTrackUtil = new AudioTrackUtil();
        downloaderService = new YoutubeDownloaderService(audioTrackUtil);
        downloadUtil = new DownloadService(downloaderService);
    }

    @Override
    public String getInfo() {
        return "Tell dingo bot to download a youtube link so we can play it back later.";
    }

    @Override
    public Mono<Void> execute(List<String> args) {

        // move the arguments and attachment urls into the same list
        event.getMessage().getAttachments().stream().map(Attachment::getUrl).forEach(args::add);

        // tell em off if they don't call the command correctly.
        if(args.isEmpty()){
            event.getMessage().getChannel().subscribe(channel -> {
                channel.createMessage(errorMessage);
            });
        }

        // Attempt to download each one.
        DownloadResults results = downloadUtil.download(args);
        TextStringBuilder message = new TextStringBuilder();
        event.getMessage().getAuthor().ifPresent(author -> {
            message.append("ay, ");
            message.append(author.getMention());
        });
        message.append(results);
        event.getMessage().getChannel().subscribe(channel -> {
           channel.createMessage(message.build()).subscribe();
        });

        // Send out each downloaded file cause why not. Bandwidth? psh
        event.getMessage().getChannel().subscribe(channel -> {
           results.results.stream().filter(result -> result.isSuccessful()).map(result -> result.downloadedFile).forEach(file -> {
               try {
                   MessageCreateSpec.Builder specBuilder = MessageCreateSpec.builder().addFile(file.getName(), FileUtils.openInputStream(file));
                   channel.createMessage(specBuilder.build()).subscribe();
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           });
        });
        return Mono.empty();
    }
}
