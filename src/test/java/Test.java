import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by wangsong on 16-9-17.
 */
public class Test {
    public static void main(String[] args) throws Exception {
        String dir = System.getProperty("user.dir");
        File file = new File(dir + "/src/main/webapp/rent.csv");
        RandomAccessFile rw = new RandomAccessFile(file, "rw");
        rw.write("he1234嬲".getBytes());
    }
}