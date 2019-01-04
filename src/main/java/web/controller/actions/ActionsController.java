package web.controller.actions;

import engine.DingoBotUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import services.DingoFileService;
import web.controller.AbstractController;

import java.io.File;
import java.util.List;

public class ActionsController extends AbstractController {
    @Autowired
    DingoBotUtil dingoUtil;

    @Autowired
    DingoFileService dingoFileService;


    @GetMapping("/files")
    public List<File> getFiles(@RequestBody String searchString){
        return dingoFileService.getFiles(searchString);
    }
}
