package dingo.tests;

import org.junit.Test;

import engine.DingoEngine;

public class TestAudio {

	public DingoEngine setupEngine(){
		DingoEngine engine = new DingoEngine();
		engine.run(DingoEngine.dingoID);
		return engine;
	}
	
	@Test
	public void testAudioPlayer(){
		DingoEngine engine = setupEngine();
	}
	
}
