package dingo.interactions.actions.Downloads;

public class NameArgumentParser extends AbstractRegexArgumentParser{

    public static final String NAME_REGEX = "(-name)[\\s]+(([\"]([^\"])*[\"]{1})|([\\w])+)";
    public NameArgumentParser(String command){
        super(command, NAME_REGEX);
    }


}
