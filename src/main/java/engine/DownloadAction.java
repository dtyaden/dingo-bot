package engine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class DownloadAction {
	
	public static HashMap<String, String> extensions = new HashMap<>();
	public static final String SOUND_DIRECTORY = "src/sounds";
	
	static{
		extensions.put("mp4", SOUND_DIRECTORY);
		extensions.put("mp3", SOUND_DIRECTORY);
		extensions.put("wav", SOUND_DIRECTORY);
		extensions.put("flac", SOUND_DIRECTORY);
	}
	
	public boolean download(String url){
		URL attachment;
		String targetFolder;
		try {
			attachment = new URL(url);
			String fileName = attachment.getFile();
			File attachmentFile = new File(fileName);
			String extension = FilenameUtils.getExtension(fileName).toLowerCase();
			targetFolder = extensions.get(extension);
			DingoEngine.getBot().getToken();
			String[] splitFileName = fileName.split("/");
			if(splitFileName.length > 0){
				fileName = splitFileName[splitFileName.length-1];
			}
			if(targetFolder != null){
				URLConnection conn = attachment.openConnection();
				conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
				conn.connect();
				FileUtils.copyInputStreamToFile(conn.getInputStream(), new File(DingoEngine.AUDIO_DIRECTORY + fileName));
				return true;
			}
			else{
				return false;
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
