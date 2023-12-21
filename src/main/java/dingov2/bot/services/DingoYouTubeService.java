package dingov2.bot.services;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class DingoYouTubeService implements YouTubeService {

    private final String youTubeApiKey;
    private final Logger logger;

    public DingoYouTubeService(String youTubeApiKey){
        logger = LoggerFactory.getLogger(DingoYouTubeService.class);
        this.youTubeApiKey = youTubeApiKey;
    }

    @Override
    public List<SearchResult> queryYouTube(String query){
        logger.info("querying youtube: "+ query);
        YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {

            }
        }).setApplicationName("dingo-query").build();

        YouTube.Search.List searchList = null;
        try {
            searchList = youTube.search().list("id, snippet");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        searchList.setKey(youTubeApiKey);
        searchList.setQ(query);
        searchList.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
        searchList.setMaxResults(5L);
        try {
            var response = searchList.execute();
            return response.getItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
