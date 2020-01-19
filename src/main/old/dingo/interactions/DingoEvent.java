package dingo.interactions;

public interface DingoEvent {
	public DingoReaction react(String...args);
	public String getUsageInfo();
}
