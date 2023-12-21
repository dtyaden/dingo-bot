package dingov2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.runs.Run;
import dingov2.bot.services.music.AudioTrackUtil;
import dingov2.config.DingoSecrets;
import dingov2.discordapi.DingoClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    private static final String secretsPathRelative = "secrets.json";

    public static void main(String args[]) {
        Logger logger = LoggerFactory.getLogger(Main.class);
        ObjectMapper mapper = new ObjectMapper();
        DingoSecrets secrets;
        try {
            File f = new File(secretsPathRelative);
            logger.info("reading config from "+ f.getAbsolutePath());
            secrets = mapper.readValue(f, DingoSecrets.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(secrets == null){
            String errorMessage = "Failed to read " + secretsPathRelative + ". Please make sure it exists.";
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        if(StringUtils.isBlank(secrets.dingoApiKey)){
            throw new RuntimeException("Couldn't read dingo api key. Make sure secrets.json has an key 'dingoApiKey'");
        }

        logger.info("starting");
        if (args.length > 2) {
            AudioTrackUtil.AUDIO_DIRECTORY = args[2];
        }
        DingoClient dingoClient = new DingoClient(secrets);
    }
}
