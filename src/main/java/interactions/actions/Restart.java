package interactions.actions;

import java.util.EnumSet;

import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;

public class Restart extends AbstractOperation{
	
	private final static String COMMANDS = "source ~/.bashrc; bash ~/dingo-start-stop.sh";
	private final static String[] fullBashCommand = {"/bin/bash", "-i", "-c", COMMANDS};
	public Restart(IMessage message) {
		super(message);
	}
	
	@Override
	public void run() {
		EnumSet<Permissions> permissions = message.getAuthor().getPermissionsForGuild(message.getGuild());
		if(permissions.contains(Permissions.KICK)) {
			// run restart
			attemptRestart();
		}
		else {
			message.reply("You aren't ranked high enough in the dingo squad to do that.");
		}
	}

	private void attemptRestart() {
		Process process;
		try {
			message.reply("Alright, fine. I'll try to restart myself...");
			message.reply("this is the command: ");
			String command = "";
			for(String str : fullBashCommand) {
				command += str + " ";
			}
			message.reply(command);
			process = new ProcessBuilder().command(fullBashCommand).start();
		}
		catch(Exception e) {
			message.reply("restart failed :pleaseno:");
			System.out.println("RESTART FAILED!");
			System.out.println(message.reply(e.getMessage()));
		}
	}
	
	@Override
	public String getInfo() {
		return "Logs the client out and creates a new engine that runs. THIS PROBABLY DOESNT ACTUALLY DESTROY THE OLD ENGINE SO IT ISNT GARBAGE COLLECTED AND EVERY TIME IT'S RESTARTED, IT SITS IN MEMORY SO USAGE BUILDS UP OVER TIME. PLEASE DONT RESTART DINGO BOT A MILLION TIMES DUDES. TODO: Actually make sure the engine is destroyed.";
	}

}
