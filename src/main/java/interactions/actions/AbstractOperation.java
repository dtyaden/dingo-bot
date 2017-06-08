package interactions.actions;

import sx.blah.discord.handle.obj.IMessage;

public abstract class AbstractOperation implements DingoOperation{
	protected IMessage message;
	public AbstractOperation(IMessage message){
		this.message = message;
	}
}
