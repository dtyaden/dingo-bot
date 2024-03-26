package dingov2.util;

import java.util.ArrayList;
import java.util.List;

public class DingoStringUtils {

    public static List<String> SplitStringsByLength(String str, int maxLength){
        List<String> stringParts = new ArrayList<>();
        for(int i = 0; i < str.length(); i += maxLength){
            stringParts.add(str.substring(i, Math.min(str.length(), i + maxLength)));
        }
        return stringParts;
    }
}
