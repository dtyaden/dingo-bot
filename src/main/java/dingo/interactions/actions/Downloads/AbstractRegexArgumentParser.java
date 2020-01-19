package dingo.interactions.actions.Downloads;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractRegexArgumentParser {

    private final String command;
    private final String regex;

    public AbstractRegexArgumentParser(String command, String regex){
        this.command = command;
        this.regex = regex;
    }

    public List<String> getMatches(){
        List<String> matches = new ArrayList<>();
        Matcher nameMatcher = Pattern.compile(regex).matcher(command);
        while(nameMatcher.find()){
            matches.add(nameMatcher.group());
        }
        return matches;
    }
}
