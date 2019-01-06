package web.controller.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import services.DingoFileService;
import web.controller.AbstractController;

import java.io.File;
import java.util.List;

public class ActionsController extends AbstractController {
    @Autowired
    DingoFileService dingoFileService;

    @GetMapping("/files")
    public List<File> getFiles(@RequestParam(required = false) String searchString) {
        return dingoFileService.searchSoundFiles(searchString);
    }
}
