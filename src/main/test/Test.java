import com.alibaba.fastjson.JSON;
import com.villa.util.encrypt.EncryptionUtil;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class Test {
    public static void main(String[] args) {
        System.out.println(JSON.toJSONString(EncryptionUtil.createDHKey("262074f1e0e19618f0d2af786779d6ad9e814b","02")));
    }
}