package util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangsong on 16-9-16.
 */
public class HousingCrawler {

    static PrintWriter pw;
    static RandomAccessFile rw;

    static boolean status[]; //检测未访问到或失败的页面

    static CountDownLatch cd;

    static HouseParser parser = new HizhuParser(); //_58Parser()

    static {
        try {
            String dir = System.getProperty("user.dir");
            File file = new File(dir + "/src/main/webapp/rent.csv");
            if (!file.exists()) {
                System.out.println(file.createNewFile());
            }

            pw = new PrintWriter(new FileWriter(file, true), true);
            rw = new RandomAccessFile(file, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    final static String _58BASE = "http://sh.58.com/pinpaigongyu/pn/%d/?minprice=0_1000";

    final static String HiZhuBASE = "http://m.hizhu.com/Home/House/scrollinfo.html?num=%d&where=";

    final static String HiZhuCondition = URLEncoder.encode("{\"limit\":1000,\"sort\":1,\"rentmoney\":\"1500以下\",\"line_id\":0,\"stand_id\":0}"); //1500以下,1500-2500

    static class MultiThread implements Runnable {
        int index;

        public MultiThread(int i) {
            this.index = i;
        }

        @Override
        public void run() {
            long l = System.currentTimeMillis();
            String url = String.format(HiZhuBASE, index);

            url = url + HiZhuCondition;
            System.out.println(url);

            String s = sendRequest(url);
            ParseInfo info = new ParseInfo(s, index);
            try {
                int t = parse(info);
                System.out.printf("page%d：%.3fs\n", index, (System.currentTimeMillis() - l) * 1.0 / 1000);
                status[index] = true;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cd.countDown();
            }
        }
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        int num = 80;
        cd = new CountDownLatch(num);
        status = new boolean[num + 1];
        for (int i = 1; i <= num; i++) {
            pool.execute(new MultiThread(i));

            try {
                Thread.sleep(500); //阻塞，防止太快锁ip
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            cd.await(5, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("--------------");
        for (int i = 0; i < status.length; i++) {
            if (!status[i])
                System.out.printf("%d,", i);
        }

        System.out.println("\nend...");
        try {
            pw.close();
            rw.write(new byte[]{0}); //修改首行回车
            rw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static int parse(ParseInfo parseInfo) {

        List<HouseInfo> list = parser.parse(parseInfo);

        return insertStatics(parseInfo, list);
    }

    private static int insertStatics(ParseInfo parseInfo, List<HouseInfo> list) {
        for (HouseInfo info : list) {
            pw.printf("\n%s,%s,%s,%s,%s,%s", info.getName(), info.getAddress(), info.getRent(), info.getUrl(), info.getLng(), info.getLat());
        }

        System.out.printf("page%d：insert %d statics\n", parseInfo.getIndex(), list.size());
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
            s = EntityUtils.toString(entity, "utf-8");

//            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }

}
