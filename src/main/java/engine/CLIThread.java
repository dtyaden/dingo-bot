package engine;

import java.util.Scanner;

public class CLIThread extends Thread{
	
	@Override
	public void run(){
		Scanner in = new Scanner(System.in);
		String input = in.nextLine();
		while(!input.equals("stop")){
			input = in.nextLine();
		}
		DingoEngine.restart();
	}
}
