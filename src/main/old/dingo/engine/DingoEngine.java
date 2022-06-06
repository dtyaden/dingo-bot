package dingo.engine;

import java.io.File;

import discord4j.core.DiscordClient;
import dingo.interactions.actions.SpamThread;
import discord4j.core.DiscordClientBuilder;
import listeners.MentionListener;
import listeners.TrackFinishListener;
import listeners.TrackStartListener;

public class DingoEngine {

    public static LanguageEngine languageEngine = new LanguageEngine();
    public static boolean debug = false;
    public static String imageLocation = "src/resources/profile.jpg";
    private static DiscordClient dingo;
    private static long timeout = 30000;
    public static final String FILE_PLAY_COUNT_lOCATION = "src/resources/PLAYCOUNT";

    private static String runningID;

    public void run(String clientID){
        runningID=clientID;
        DiscordClient dingo = createClient(clientID,false);
        DingoEngine.dingo = dingo;
        dingo.getEventDispatcher().on()

        dingo.getDispatcher().registerListener(new MentionListener(Long.parseLong(dingo.getApplicationClientID())));
        dingo.getDispatcher().registerListener(new TrackStartListener(Long.parseLong(dingo.getApplicationClientID())));
        dingo.getDispatcher().registerListener(new TrackFinishListener(Long.parseLong(dingo.getApplicationClientID())));
        dingo.getDispatcher().registerListener(new MessageListener());

        if(dingo.isLoggedIn()){
            dingo.logout();
        }
        dingo.login();

        while(!dingo.isLoggedIn()){
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("waiting for login");
        }
        setProfileImage(imageLocation);
    }

    public static void setProfileImage(String image){
        File avatar = new File(image);
        Image profileImage = Image.forFile(avatar);
        dingo.changeAvatar(profileImage);
    }

    public static DiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord client
        DiscordClientBuilder clientBuilder = new DiscordClientBuilder(token); // Creates the ClientBuilder instance

        DiscordClient client = clientBuilder.build();
        if (login) {
            client.login();// Creates the client instance and logs the client in
        }
        return client;
    }

    public static IDiscordClient getBot(){
        return dingo;
    }

    public static long getTimeout(){
        return timeout;
    }

    public static void restart(){
        SpamThread.stopRunning();
        dingo.logout();
        new DingoEngine().run(DingoEngine.runningID);
    }
}
