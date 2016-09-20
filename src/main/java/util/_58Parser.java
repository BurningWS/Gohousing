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
public class _58Parser implements HouseParser {

//    Pattern p = Pattern.compile("(?<=】).*", Pattern.UNICODE_CHARACTER_CLASS);

    Pattern p1 = Pattern.compile("(?<=lat = ').*(?=')"), p2 = Pattern.compile("(?<=lon = ').*(?=')");

    public ArrayList<HouseInfo> parse(ParseInfo info) {
        ArrayList<HouseInfo> list = new ArrayList<HouseInfo>();
        Document d = Jsoup.parse(info.getContent());
        Elements es = d.select(".list > li");
        for (Element e : es) {
            String title = e.select("h2").first().text();

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

            parsePosition(url, houseInfo);

            list.add(houseInfo);
        }

        return list;
    }

    //爬取经纬度
    private void parsePosition(String url, HouseInfo houseInfo) {
        Matcher m;
        String ds = HousingCrawler.sendRequest(url);
        m = p2.matcher(ds);

        for (int i = 0; m.find(); i++, m.usePattern(p1)) {
            if (i == 0) {
                houseInfo.setLng(m.group());
            } else {
                houseInfo.setLat(m.group());
                break;
            }
        }
    }
}
