package dingo.interactions.actions;

import dingo.api.base.entity.DingoMessage;

public interface DingoAction {
	public DingoOperation getOperation(DingoMessage message);
}
