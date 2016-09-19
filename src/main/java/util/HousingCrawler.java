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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangsong on 16-9-16.
 */
public class HousingCrawler {

    static PrintWriter pw;

    static boolean status[];

    static CountDownLatch cd;

    static {
        try {
            String dir = System.getProperty("user.dir");
            File file = new File(dir + "/src/main/webapp/rent.csv");
            if (!file.exists()) {
                System.out.println(file.createNewFile());
            }

            pw = new PrintWriter(new FileWriter(file, true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class MultiThread implements Runnable {
        int index;

        public MultiThread(int i) {
            this.index = i;
        }

        @Override
        public void run() {
            long l = System.currentTimeMillis();
            String url = String.format("http://sh.58.com/pinpaigongyu/pn/%d/?minprice=1000_2000", index);
            String s = sendRequest(url);
            int t = parse(s);
            System.out.printf("page%d:%.3fs\n", index, (System.currentTimeMillis() - l) * 1.0 / 1000);
            status[index] = true;
            cd.countDown();
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        int num = 175;
        cd = new CountDownLatch(num + 1);
        status = new boolean[num + 1];
        for (int i = 0; i <= num; i++) {
            pool.execute(new MultiThread(i));

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            cd.await(2, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--------------");
        for (int i = 0; i < status.length; i++) {
            if (!status[i])
                System.out.printf("%d,", i);
        }

        System.out.println("\nend...");
    }

    private static int parse(String s) {
        _58Parser parser = new _58Parser();
        ArrayList<HouseInfo> list = parser.parse(s);

        for (HouseInfo info : list) {
            pw.printf("\n%s,%s,%s,%s,%s,%s", info.getName(), info.getAddress(), info.getRent(), info.getUrl(), info.getLng(), info.getLat());
        }

        System.out.printf("insert %d statics\n", list.size());
        return list.size();
    }

    public static String sendRequest(String link) {
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
