package util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by wangsong on 16-9-16.
 */
public class HousingCrawler {

    public static void main(String[] args) {
        String s = sendRequest("http://sh.58.com/pinpaigongyu/pn/118/?minprice=1500_2000");
        _58Parser parser = new _58Parser();
        ArrayList<HouseInfo> list = parser.parse(s);
        try {
            File file = new File("/home/wangsong/myRent.csv");
            if (!file.exists()) {
                System.out.println(file.createNewFile());
            }

            PrintWriter pw = new PrintWriter(new FileWriter(file, true), true);
            for (HouseInfo info : list) {
                pw.printf("%s,%s,%s,%s\n", info.getName(), info.getAddress(), info.getRent(), info.getUrl());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("end...");

    }

    private static String sendRequest(String link) {
        HttpGet hg = new HttpGet(link);
        HttpClientBuilder hcb = HttpClients.custom();
        CloseableHttpClient httpclient = hcb.build();
        String s = null;
        try {
            CloseableHttpResponse response = httpclient.execute(hg);
            HttpEntity entity = response.getEntity();
            s = EntityUtils.toString(entity);

//            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

}
