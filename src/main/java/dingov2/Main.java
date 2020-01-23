package dingov2;

        import dingov2.discordapi.DingoClient;
        import org.slf4j.Logger;
        import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String args[]){

        Logger logger = LoggerFactory.getLogger(Main.class);
        logger.debug("test");

        if(args.length < 1){
            throw new RuntimeException("No token supplied for the bot. gg.");
        }

        DingoClient dingoClient = new DingoClient(args[0]);
        dingoClient.login();
    }
}
