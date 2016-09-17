package util;

/**
 * Created by wangsong on 16-9-16.
 */
public class HouseInfo {

//    房源名称，地址，月租，房源url地址
    String name;
    String address;
    String rent;
    String url;

    public HouseInfo(String name, String address, String rent, String url) {
        this.name = name;
        this.address = address;
        this.rent = rent;
        this.url = url;
    }

    @Override
    public String toString() {
        return "HouseInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rent='" + rent + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
