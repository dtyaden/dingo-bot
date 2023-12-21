package dingov2.bot.services;

import com.google.api.services.youtube.model.SearchResult;
import org.apache.commons.lang3.NotImplementedException;

import java.util.List;

public interface YouTubeService {
    default List<SearchResult> queryYouTube(String query){
        throw new NotImplementedException("Youtube service not setup...");
    }
}
