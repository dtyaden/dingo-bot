package dingo.tests;

import org.junit.Test;

import dingo.engine.DingoEngine;

public class TestAudio {

	public DingoEngine setupEngine(){
		DingoEngine engine = new DingoEngine();
		return engine;
	}
	
	@Test
	public void testAudioPlayer(){
		DingoEngine engine = setupEngine();
	}
	
}
