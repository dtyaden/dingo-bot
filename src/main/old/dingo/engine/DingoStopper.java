package dingo.engine;

import sx.blah.discord.api.IDiscordClient;

public class DingoStopper {
	public static void main(String[] args){
		IDiscordClient dingo = DingoEngine.createClient("MzIwNjc5NjI4ODExNTM0MzM5.DBT3gg.DMrzy5mGHz6iWyacwOyb7JuS140",false);
		dingo.logout();
	}
}
