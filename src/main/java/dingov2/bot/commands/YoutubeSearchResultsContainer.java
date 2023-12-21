package dingov2.bot.commands;

import java.util.concurrent.ConcurrentHashMap;

public class YoutubeSearchResultsContainer {
    private ConcurrentHashMap<Integer, String> results = new ConcurrentHashMap<>();
    public void add(int number, String url){
        results.put(number, url);
    }

    public String getUrl(int number){
        if(!results.containsKey(number)){
            throw new IllegalArgumentException();
        }
        return results.get(number);
    }

    public YoutubeSearchResultsContainer clear(){
        results.clear();
        return this;
    }

    public int size(){
        return results.size();
    }
}
