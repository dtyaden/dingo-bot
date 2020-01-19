package dingo.interactions.actions.Downloads;

public class YoutubeDownloadThread implements Runnable{

    String name = "", url = "";

    public YoutubeDownloadThread(String name, String url){
        this.name = name;
        this.url = url;
    }


    @Override
    public void run() {
        
    }
}
