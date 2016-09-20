package util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by wangsong on 16-9-19.
 */
public class HizhuParser implements HouseParser {

    ExecutorService pool = Executors.newFixedThreadPool(20);

    @Override
    public List<HouseInfo> parse(final ParseInfo info) {
        Document d = Jsoup.parse(info.getContent());
        Elements li = d.select("li");

        final List<HouseInfo> list = new CopyOnWriteArrayList<>();
        final CountDownLatch cd = new CountDownLatch(li.size());

        int index = 1, size = li.size();
        for (Element e : li) {
            try {
                final String url = e.select("a").first().attr("href");

                Elements div = e.select("div");
                Element d1 = div.get(0);
                String rentType = d1.select("p").first().text();

                d1 = div.get(1);
                String address = d1.select("h3").first().text();

                String money = d1.select("em").first().text();

                String payType = d1.select("span").first().text();

                String detail = d1.select(".houseRight_address").first().text();

                detail = String.format("【%s】%s %s %s", rentType, detail, address, payType);

                final HouseInfo houseInfo = new HouseInfo(detail, address, money, url);
                final ParseInfo parseInfo = new ParseInfo(index++, size);

                class MultiThread implements Runnable {

                    @Override
                    public void run() {
                        try {
                            parsePosition(url, houseInfo, parseInfo, info.getIndex());
                            if (houseInfo.getLat() != null && houseInfo.getLng() != null) {
                                list.add(houseInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            cd.countDown();
                        }
                    }
                }

                pool.execute(new MultiThread());
                Thread.sleep(1000); //阻塞，防止太快锁ip

            } catch (Exception e1) {
                e1.printStackTrace();
                cd.countDown();
                continue;
            }
        }

        try {
            cd.await(4, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(list);
        return list;
    }

    //爬取经纬度
    private void parsePosition(String url, HouseInfo houseInfo, ParseInfo info, int index) {
        String ds = HousingCrawler.sendRequest(url);
        Document d = Jsoup.parse(ds);
        String href = d.select(".address_img").first().attr("href");

        //解析url参数
        String paramsStr = href.substring(href.indexOf('?') + 1);
        HashMap<String, String> map = new HashMap<String, String>();
        String[] params = paramsStr.split("&");
        for (String param : params) {
            String[] KV = param.split("=");
            if (KV.length <= 1)
                continue;
            map.put(KV[0], KV[1]);
        }

        houseInfo.setLng(map.get("lpt_x"));
        houseInfo.setLat(map.get("lpt_y"));
//        houseInfo.setAddress(map.get("estate_address"));

        System.out.printf("page%d：get x,y %d/%d\n", index, info.getIndex(), info.getTotal());
    }
}
