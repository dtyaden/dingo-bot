package dingov2.bot.commands.actions;

import dingov2.bot.commands.AbstractAction;
import dingov2.bot.services.downloads.DownloadService;
import dingov2.bot.services.downloads.validation.DownloadResult;
import dingov2.bot.services.downloads.validation.DownloadResults;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.bot.services.downloads.YoutubeDownloaderService;
import dingov2.discordapi.DingoEventWrapper;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Attachment;
import discord4j.core.spec.MessageCreateSpec;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.TextStringBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class DownloadAction extends AbstractAction {

    private final AudioTrackUtil audioTrackUtil;
    private final DownloadService downloadUtil;
    YoutubeDownloaderService downloaderService;
    private String errorMessage = "Dude, you didn't even pass me something to download. Fuck off. jfc.......";
    public DownloadAction(DingoEventWrapper event, List<String> arguments) {
        super(event, arguments);
        audioTrackUtil = new AudioTrackUtil();
        downloaderService = new YoutubeDownloaderService(audioTrackUtil);
        downloadUtil = new DownloadService(downloaderService);
    }

    @Override
    public String getInfo() {
        return "Tell dingo bot to download a youtube link so we can play it back later.";
    }

    @Override
    public Mono<Void> execute() {
        // move the arguments and attachment urls into the same list
        event.getAttachments().stream().map(Attachment::getUrl).forEach(arguments::add);

        // tell em off if they don't call the command correctly.
        if(arguments.isEmpty()){
            event.reply(errorMessage).subscribe();
        }

        // Attempt to download each one.
        DownloadResults results = downloadUtil.download(arguments);
        TextStringBuilder message = new TextStringBuilder();
        event.getMember().ifPresent(author -> {
            message.append("ay, ");
            message.append(author.getMention());
        });
        message.append(results);
        event.reply(message).subscribe();

        // Send out each downloaded file cause why not. Bandwidth? psh
        event.getChannel().subscribe(channel -> {
           results.results.stream().filter(DownloadResult::isSuccessful).map(result -> result.downloadedFile).forEach(file -> {
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
