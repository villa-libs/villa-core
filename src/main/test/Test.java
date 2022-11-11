import com.villa.event.aop.EventAOP;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @作者 微笑い一刀
 * @bbs_url https://blog.csdn.net/u012169821
 */
public class Test {
    public static void main(String[] args) {
        Method[] methods = new EventAOP().getClass().getDeclaredMethods();
        for (Method method : methods) {
            Class<?>[] types = method.getParameterTypes();
            StringBuilder sb = new StringBuilder();
            for (Class<?> type : types) {
                sb.append(type.getName());
            }
            System.out.println(sb);
        }
    }
}
