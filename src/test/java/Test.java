import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangsong on 16-9-17.
 */
public class Test {
    public static void main(String[] args) {
        Pattern p = Pattern.compile("(?<=】).*", Pattern.UNICODE_CHARACTER_CLASS);
        Matcher m = p.matcher("【合租】垡头 翠城馨园 3室次卧");
        while (m.find()) {
            System.out.println(m.group());
        }
    }

}
