import com.villa.cms.DBHandler;

public class CMS {
    public static void main(String[] args) {
        DBHandler handler = new DBHandler("jdbc:mysql://192.168.110.121/six-lottry?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false", "root", "admin123");
        handler.setPackageName("com.lottery");
//        handler.auto();
        handler.createVue("ticket");
    }
}