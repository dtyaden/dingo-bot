package engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import sx.blah.discord.handle.obj.IUser;

public class SpamThread extends Thread{
	
	public static HashMap<IUser, Long> spamGuard = new HashMap<>();
	private final static long timeout = 3000;
	public static AtomicBoolean continueRunnning = new AtomicBoolean(true);
	@Override
	public void run() {
		while(continueRunnning.get()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			long time = System.currentTimeMillis();
			spamGuard.entrySet().forEach((entry) -> {
				if(time - entry.getValue() > timeout){
					spamGuard.remove(entry.getKey());
				}
			});
		}
	}
	
	public static boolean checkUser(IUser user){
		long time = System.currentTimeMillis();
		List<Entry> entriesToRemove = new ArrayList<>();
		spamGuard.entrySet().forEach((entry) -> {
			if(time - entry.getValue() > timeout){
				entriesToRemove.add(entry);
			}
		});
		entriesToRemove.forEach(entry -> spamGuard.remove(entry.getKey()));
		if(spamGuard.containsKey(user)){
			return true;
		}
		else{
			spamGuard.put(user, System.currentTimeMillis());
			return false;
		}
	}
	
	public static void stopRunning(){
		if(SpamThread.continueRunnning != null){
			SpamThread.continueRunnning.set(false);
		}
	}
	
}
