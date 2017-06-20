package engine;

import java.io.File;
import java.util.HashMap;

import listeners.MentionListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

public class DingoEngine {
	
	private static final String dingoId = "MzIwNjc5NjI4ODExNTM0MzM5.DBT3gg.DMrzy5mGHz6iWyacwOyb7JuS140";
	private static final String dingoTestId = "MzI1ODc2NzE1NjgxMjE4NTcw.DCeqVQ.B2CNHu8IWbvxCcTquK_JF6UwN6k";
	public static LanguageEngine languageEngine = new LanguageEngine();
	public static boolean debug = false;
	public static String imageLocation = "src/resources/profile.jpg";
	private static IDiscordClient dingo;
	private static long timeout = 30000;
	public static final String FILE_PLAY_COUNT_lOCATION = "src/resources/PLAYCOUNT";
	public static final String AUDIO_DIRECTORY = "src/resources/sounds/";
	
	public static String getDingoTestId(){
		return dingoTestId;
	}
	
	public static String getDingoId(){
		return dingoId;
	}
	
	public void run(String clientID){
		IDiscordClient dingo = createClient(clientID,false);
		DingoEngine.dingo = dingo;
		
		dingo.getDispatcher().registerListener(new MentionListener(Long.parseLong(dingo.getApplicationClientID())));
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
	
    public static IDiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder
        
        try {
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            e.printStackTrace();
            return null;
        }
    }
    
    public static IDiscordClient getBot(){
    	return dingo;
    }
    
    public static long getTimeout(){
    	return timeout;
    }
    
    public static void stopDingo(){
    	SpamThread.stopRunning();
    	dingo.logout();
    	System.exit(1);
    }
}
