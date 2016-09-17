package util;

import lombok.Data;

/**
 * Created by wangsong on 16-9-16.
 */

@Data
public class HouseInfo {

//    房源名称，地址，月租，房源url地址
    private String name;
    private String address;
    private String rent;
    private String url;

    public HouseInfo(String name, String address, String rent, String url) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.url = url;
    }

}
