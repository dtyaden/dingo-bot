package engine;

public class MainBot {
	public static void main(String[] args) {
		System.out.println("starting dingo engine...");
		DingoEngine engine = new DingoEngine();
		CLIThread cli = new CLIThread();
		String dingoID = args[0];
		cli.start();
		engine.run(dingoID);
	}
}
