package dingov2;

        import dingov2.bot.music.AudioTrackUtil;
        import dingov2.discordapi.DingoClient;
        import discord4j.core.GatewayDiscordClient;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;
        import reactor.core.publisher.Mono;

public class Main {

    public static void main(String args[]){

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("test");

        if(args.length < 1){
            throw new RuntimeException("No token supplied for the bot. gg.");
        }

        if(args.length > 1 ){
            AudioTrackUtil.AUDIO_DIRECTORY = args[1];
        }
        DingoClient dingoClient = new DingoClient(args[0]);
    }
}
