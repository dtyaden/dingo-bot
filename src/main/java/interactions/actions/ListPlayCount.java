package interactions.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import engine.DingoEngine;
import interactions.actions.AbstractOperation;
import sx.blah.discord.handle.obj.IMessage;

public class ListPlayCount extends AbstractOperation{

	public ListPlayCount(IMessage message) {
		super(message);
	}

	@Override
	public void run() {
		HashMap<String, Integer> filePlayCounts = new HashMap<String, Integer>();
		try {
			FileInputStream fis = new FileInputStream(new File(DingoEngine.FILE_PLAY_COUNT_lOCATION));
			ObjectInputStream ois = new ObjectInputStream(fis);
			filePlayCounts = (HashMap<String,Integer>) ois.readObject();
			
			Set<Entry<String, Integer>> sortedEntries = filePlayCounts.entrySet();
			List<Entry<String, Integer>> entries = new ArrayList<>();
			entries.addAll(filePlayCounts.entrySet());
			Collections.sort(entries,new Comparator<Entry<String,Integer>>(){
				@Override
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			message.reply(formatPlayCount(entries));
			fis.close();
			ois.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static String formatPlayCount(Collection<Entry<String, Integer>> counts){
		StringBuilder builder = new StringBuilder();
		
		counts.forEach(entry -> builder.append(entry.getKey() + " --- " + entry.getValue()+"\n"));
		return builder.toString().trim();
	}
	
	@Override
	public String getInfo() {
		return "Get a list of how many times each audio meme has been played in descending order.";
	}

}
