package dingo.tests;

import dingo.interactions.actions.Downloads.NameArgumentParser;
import org.junit.Test;

public class TestDownloadParsing {

    private static final String nameSingleWord = "-name name";
    private static final String nameMultiWord = "-name multi word";
    private static final String nameQuotations = "-name \"quotes used here\"";

    @Test
    public void testNamePatternMatching(){
        String url = "youtu.be/bullshit ";
        NameArgumentParser nameArgs = new NameArgumentParser( url + nameSingleWord);
        NameArgumentParser multieWord = new NameArgumentParser(url + nameMultiWord);
        NameArgumentParser nameQuotes = new NameArgumentParser(url + nameQuotations);

        System.out.println(nameArgs.getMatches());
    }

}
