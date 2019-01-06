package services;

import sx.blah.discord.handle.obj.IMessage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DingoFileService {
    public static final String AUDIO_DIRECTORY = "src/resources/sounds/";
    public static final String SPLIT_REGEX = "[ _-]";

    public List<String> splitString(List<String> searchStrings){
        List<String> allSplit = new ArrayList<>();
        for(String input : searchStrings){
            if(input != null){
                String[] split = input.split(SPLIT_REGEX);
                allSplit.addAll(Arrays.asList(split));
            }
        }
        return allSplit;
    }

    public List<File> searchSoundFiles(String... searchStrings) {
        return searchSoundFiles(splitString(Arrays.asList(searchStrings)));
    }

    public List<File> searchSoundFiles(List<String> searchStrings) {
        return findFilesMultipleWords(AUDIO_DIRECTORY, searchStrings);
    }

    public List<String> getMessageArguments(IMessage message) {
        String[] messageContent = message.getContent().split("[ ]+");
        List<String> messageArguments = new ArrayList<>();
        int i = 2;
        while(i < messageContent.length){
            messageArguments.add(messageContent[i]);
            i++;
        }
        return messageArguments;
    }

    /**
     * matches all files if no search strings passed
     * @param files
     * @param keywords
     * @return
     */
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

}
