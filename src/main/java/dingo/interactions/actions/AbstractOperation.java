package dingo.interactions.actions;


import dingo.api.base.entity.DingoMessage;

public abstract class AbstractOperation implements DingoOperation{
	protected DingoMessage message;
	public AbstractOperation(DingoMessage message){
		this.message = message;
	}
}
