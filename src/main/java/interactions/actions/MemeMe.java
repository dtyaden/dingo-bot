package interactions.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import sx.blah.discord.handle.obj.IMessage;

public class MemeMe extends AbstractOperation{
	
	public MemeMe(IMessage message) {
		super(message);
	}

	public static List<String> xD = new ArrayList<String>();
	
	static{
		xD.add("{navy seal pasta}");
		xD.add("investigate 311");
		xD.add("covfefe");
		xD.add("i vape i spin i fidget and u have the audacity to say i dont have a life...");
	}
	
	@Override
	public void run() {
		message.getChannel().sendMessage(xD.get(new Random().nextInt(xD.size())));
	}

	@Override
	public String getInfo() {
		return "get a random meme";
	}
	
}
