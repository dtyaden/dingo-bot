package dingov2.bot.music;

import org.apache.commons.io.FileUtils;
import org.apache.commons.text.TextStringBuilder;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AudioTrackUtil {

    public static final String AUDIO_DIRECTORY = "src/resources/sounds/";
    public static Logger logger;

    public AudioTrackUtil(){
        logger = LoggerFactory.getLogger(AudioTrackUtil.class);
    }

    public String getTrack(List<String> args){
        if (args.isEmpty()){
            return "";
        }
        if (args.get(0).contains("youtube") || args.get(0).contains("youtu.be")){
            return args.get(0);
        }
        logger.debug("didn't match youtube. making a file path instead");
        File[] fileArray = FileUtils.getFile(AUDIO_DIRECTORY).listFiles();
        if (fileArray == null || fileArray.length == 0){
            logger.error("no files found!");
            return "";
        }
        return searchForFile(args);
    }
    public List<File> matchFilenames(List<File> files, List<String> keywords){
        if(keywords.isEmpty()){
            return files;
        }
        return files.stream().filter( file -> {
                    for(String keyword : keywords){
                        if(file.getName().toLowerCase().contains(keyword.toLowerCase())){
                            return true;
                        }
                    }
                    return false;
                }
        ).collect(Collectors.toList());
    }

    public List<File> findFilesMultipleWords(String directory, List<String> keywords){
        List<File> files = Arrays.asList(new File(directory).listFiles());
        return matchFilenames(files, keywords);
    }

    public List<String> searchForFileAllTerms(List<String> args){
        TextStringBuilder combinedArgs = new TextStringBuilder();
        return findFilesMultipleWords(AUDIO_DIRECTORY, args).stream().map(file -> file.getName()).collect(Collectors.toList());
    }

    public List<String> searchSoundFiles(List<String> args) {
        return findFilesMultipleWords(AUDIO_DIRECTORY, args).stream().map(file -> file.getAbsolutePath()).collect(Collectors.toList());
    }

    // use levenshtein distance to search for files on disk and determine the closest file name to the given args
    private String searchForFile(List<String> args) {
        List<File> tracks = findFilesMultipleWords(AUDIO_DIRECTORY, args);
        String closestTrack = "";
        int closestDistance = 999;
        if(tracks.size() != 0) {
            System.out.println("found some tracks: " + Arrays.asList(tracks));
            for(File file : tracks) {
                int distance = LevenshteinDistance.getDefaultInstance().apply(file.getName(), closestTrack);
                if(distance < closestDistance) {
                    closestDistance = distance;
                    closestTrack = file.getAbsolutePath();
                }
            }
        }
        else {
            return null;
        }
        return closestTrack;
    }
}
