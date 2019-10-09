package interactions.actions.Downloads;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameArgumentParser extends AbstractRegexArgumentParser{

    public static final String NAME_REGEX = "(-name)[\\s]+(([\"]([^\"])*[\"]{1})|([\\w])+)";
    public NameArgumentParser(String command){
        super(command, NAME_REGEX);
    }


}
