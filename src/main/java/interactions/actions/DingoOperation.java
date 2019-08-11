package interactions.actions;

import sx.blah.discord.handle.impl.obj.Message;

public interface DingoOperation extends Runnable{
	public void run();
	public String getInfo();
}
