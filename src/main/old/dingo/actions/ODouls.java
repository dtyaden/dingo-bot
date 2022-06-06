package dingov2.bot.commands.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import dingo.api.base.entity.DingoMessage;
import org.apache.commons.io.FileUtils;

import dingo.engine.DingoEngine;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.MessageBuilder;

public class ODouls extends AbstractOperation {

	public static String ODOULS_LOCATION = "src/resources/images/odouls";

	public ODouls(DingoMessage message) {
		super(message);
	}

	@Override
	public void run() {
		File imageDir = FileUtils.getFile(ODOULS_LOCATION);
		if(!imageDir.isDirectory()){
			throw new RuntimeException("Need a directory for images...");
		}
		List<File> imageFiles = Arrays.asList(imageDir.listFiles());
		//pick a random image to send
		Random random = new Random();
		int imagePosition = random.nextInt(imageFiles.size());
		File image = imageFiles.get(imagePosition);
		
		MessageBuilder m = new MessageBuilder(DingoEngine.getBot());
		m.withChannel(message.getChannel());
		
		try {
			m.withFile(new File(image.getAbsolutePath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		m.send();
	}

	@Override
	public String getInfo() {
		return "MARCO RUBIO... marco.... rubio, MARCO...";
				
	}

}
