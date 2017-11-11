package engine;

import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.obj.IMessage;

public class Restart extends AbstractOperation{

	public Restart(IMessage message) {
		super(message);
	}

	@Override
	public void run() {
		DingoEngine.restart();
	}

	@Override
	public String getInfo() {
		return "Logs the client out and creates a new engine that runs. THIS PROBABLY DOESNT ACTUALLY DESTROY THE OLD ENGINE SO IT ISNT GARBAGE COLLECTED AND EVERY TIME IT'S RESTARTED, IT SITS IN MEMORY SO USAGE BUILDS UP OVER TIME. PLEASE DONT RESTART DINGO BOT A MILLION TIMES DUDES. TODO: Actually make sure the engine is destroyed.";
	}

}
