import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangsong on 16-9-17.
 */
public class Test {
    public static void main(String[] args) {
//        String dir = System.getProperty("user.dir");
//        String url = "http://sh.58.com/pinpaigongyu/26949109616685x.shtml";
//        String s = HousingCrawler.sendRequest(url);


        Pattern p = Pattern.compile("(?<=lon = ').*(?=')"), p1 = Pattern.compile("(?<=lat = ').*(?=')");
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(m.group());
            m.usePattern(p1);

        }

    }


    static String s = "";
}