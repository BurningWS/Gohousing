package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangsong on 16-9-16.
 */
public class _58Parser {

    Pattern p = Pattern.compile("(?<=】).*", Pattern.UNICODE_CHARACTER_CLASS);

    public ArrayList<HouseInfo> parse(String s) {
        ArrayList<HouseInfo> list = new ArrayList<HouseInfo>();
        Document d = Jsoup.parse(s);
        Elements es = d.select(".list > li");
        for (Element e : es) {
            String title = e.select("h2").first().text();
            Matcher m = p.matcher(title);
            while (m.find()) {
                title = m.group();
            }

            String url = "http://sh.58.com/" + e.select("a").first().attr("href");
            String[] split = title.split(" ");
            String address = null;
            if (split[1].contains("公寓") || split[1].contains("青年社区")) {
                address = split[0];
            } else {
                address = split[1];
            }
            String rent = e.select(".money").first().select("b").first().text();
            HouseInfo houseInfo = new HouseInfo(title, address, rent, url);
            list.add(houseInfo);
        }

        return list;
    }
}
