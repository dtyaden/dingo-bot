package services;

import engine.DingoBotUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;

public class DingoFileService {
    @Autowired
    DingoBotUtil dingoBotUtil;

    public List<File> getFiles(String searchString){
        return dingoBotUtil.searchSoundFiles(searchString);
    }

}
