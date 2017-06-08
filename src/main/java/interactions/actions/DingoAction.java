package interactions.actions;

import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IMessage;

public interface DingoAction {
	public DingoOperation getOperation(IMessage message);
}
