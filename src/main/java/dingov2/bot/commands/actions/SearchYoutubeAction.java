package dingov2.bot.commands.actions;

import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import dingov2.bot.commands.AbstractMessageEventAction;
import dingov2.bot.commands.YoutubeSearchResultsContainer;
import dingov2.bot.services.YouTubeService;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.apache.commons.lang3.StringUtils;
import reactor.core.publisher.Mono;

import java.util.Iterator;
import java.util.List;

public class SearchYoutubeAction extends AbstractMessageEventAction {

    public static final String youTubeUrlPrefix = "https://www.youtube.com/watch?v=";
    private YouTubeService dingoYouTubeService;
    private YoutubeSearchResultsContainer container;

    public SearchYoutubeAction(MessageCreateEvent event, YouTubeService dingoYouTubeService, YoutubeSearchResultsContainer container){
        super(event);

        this.dingoYouTubeService = dingoYouTubeService;
        this.container = container;
    }

    private String prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {
        StringBuilder messageBuilder = new StringBuilder();
        StringBuilder urlBuilder = new StringBuilder();

        if (!iteratorSearchResults.hasNext()) {
            return "No results found for query '" + query + "'... jfc.";
        }
        int resultNumber = 1;
        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Confirm that the result represents a video. Otherwise, the
            // item will not contain a video ID.
            if (rId.getKind().equals("youtube#video")) {
                urlBuilder.append(youTubeUrlPrefix).append(rId.getVideoId());
                String url = urlBuilder.toString();
                Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                messageBuilder.append("### ").append(resultNumber).append(" - ").append(singleVideo.getSnippet().getTitle()).append("\n");
                messageBuilder.append("<").append(url).append(">\n");
                container.add(resultNumber, url);
                resultNumber++;
            }
        }
        return messageBuilder.toString();
    }

    @Override
    public Mono<Void> execute(List<String> args) {
        String query = StringUtils.join(args);
        return Mono.fromRunnable(() ->{
           List<SearchResult> results = dingoYouTubeService.queryYouTube(query);

           event.getMessage().getChannel().subscribe((messageChannel -> {
               messageChannel.createMessage(prettyPrint(results.iterator(), query)).subscribe();
           }));
        });
    }

}
