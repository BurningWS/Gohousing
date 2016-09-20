package util;

import lombok.Data;

/**
 * Created by wangsong on 16-9-20.
 */
@Data
public class ParseInfo {
    private String content;
    private int index;
    private int total;

    public ParseInfo(String content, int index) {
        this.content = content;
        this.index = index;
    }

    public ParseInfo(int index, int total) {
        this.index = index;
        this.total = total;
    }
}
