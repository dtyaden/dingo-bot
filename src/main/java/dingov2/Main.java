package dingov2;

        import dingov2.bot.services.music.AudioTrackUtil;
        import dingov2.discordapi.DingoClient;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String args[]){

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.info("starting");
        if(args.length < 2){
            throw new RuntimeException("No token supplied for the bot. gg.");
        }

        if(args.length > 2 ){
            AudioTrackUtil.AUDIO_DIRECTORY = args[2];
        }
        DingoClient dingoClient = new DingoClient(args[0], args[1]);
    }
}
