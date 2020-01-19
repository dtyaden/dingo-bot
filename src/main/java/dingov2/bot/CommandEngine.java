package dingov2.bot;

import dingo.interactions.actions.DingoAction;
import dingov2.discordapi.DingoClient;

import java.util.HashMap;

/**
 * Parses input and returns an appropriate command to execute based on the parameters given.
 */
public class CommandEngine extends HashMap<String, DingoAction> {


    private final DingoClient dingoClient;

    public void loadCommands(){

    }

    public CommandEngine(DingoClient dingoClient) {
        super();
        this.dingoClient = dingoClient;


    }


}
