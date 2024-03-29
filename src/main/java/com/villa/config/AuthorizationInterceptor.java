package com.villa.config;

import com.alibaba.fastjson.JSON;
import com.villa.auth.Auth;
import com.villa.auth.AuthModel;
import com.villa.auth.annotation.NoLogin;
import com.villa.auth.annotation.NoSign;
import com.villa.dto.ErrCodeDTO;
import com.villa.dto.ResultDTO;
import com.villa.log.Log;
import com.villa.redis.RedisClient;
import com.villa.util.ClassUtil;
import com.villa.util.IPUtil;
import com.villa.util.Util;
import com.villa.util.encrypt.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor{
    @Autowired
    private Auth auth;
    @Autowired
    private VillaConfig villaConfig;
    @Autowired
    private RedisClient redisClient;

    private List<String> whitelists = new ArrayList<>();
    /** ip请求数在redis中的前缀常量 */
    private static final String IP_REQ_KEY = "auth_ip_req_";
    /** 黑名单ip在redis中的前缀常量 */
    private static final String BLACKLIST_KEY = "auth_blacklist";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = IPUtil.getIP(request);
        //黑白名单验证
        if(!validateIp(ip)){
            return false;
        }
        /**
         * 1. 方法类型是OPTION 严格来说这里仅处理POST/GET
         * 2. public开头的接口  不处理签名和登录 也意味着拿不到登录者
         * 3. 如果这里得到一个ResourceHttpRequestHandler 检查URL是否拼接正确,
         * 一般都是url拼接错误导致不存在此url,spring则会当成静态资源解析
         */
        if(request.getMethod().equals("OPTIONS")||
            request.getRequestURI().startsWith("/public")||
            !(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod= (HandlerMethod)handler;
        //判断是否需要签名
        NoSign noSign = handlerMethod.getMethod().getAnnotation(NoSign.class);
        //是否需要登录才能访问
        NoLogin noLogin = handlerMethod.getMethod().getAnnotation(NoLogin.class);
        //登陆者token 先获取 是因为想要就算有@NoLogin注解也能拿到登录者的效果
        String token = request.getHeader("v-token");
        //不需要签名 也不需要登录
        if(noSign!=null&&noLogin!=null){
            saveLoginInfo(request,token);
            return true;
        }
        //需要sign
        if(noSign==null){
            String timestampStr = request.getHeader("timestamp");
            if(Util.isNullOrEmpty(timestampStr)||!Util.isNumeric(timestampStr)){
                Log.err("【签名失败】timestamp为空或不是数字");
                putErr(response,ResultDTO.put401(ErrCodeDTO.ox00001));
                return false;
            }
            long timestamp = Long.parseLong(timestampStr);
            //验证签名  签名必须存在于header中
            String sign = request.getHeader("sign");
            //获取当前token上次携带的请求时间
            if(!validateSign(sign,timestamp,request)){
                putErr(response,ResultDTO.put401(ErrCodeDTO.ox00001));
                return false;
            }
        }

        //验证token 启用了登录拦截 才去验证
        if(noLogin==null&&!auth.validate(token,request.getRequestURI())){
            putErr(response, ResultDTO.put401(ErrCodeDTO.ox00002));
            return false;
        }
        saveLoginInfo(request,token);
        return true;
    }
    private void saveLoginInfo(HttpServletRequest request,String token){
        if(Util.isNotNullOrEmpty(token)){
            request.setAttribute("v-token",token);
            AuthModel authModel = auth.getAuthModel(token);
            if(authModel!=null){
                request.setAttribute("loginAttrs", authModel.getAttrs());
            }
        }
    }
    /**
     * IP黑白名单验证
     */
    private boolean validateIp(String ip){
        //白名单访问拦截
        if(Util.isNotNullOrEmpty(villaConfig.getWhiteList())){
            if(whitelists.size()==0){
                String[] ips = villaConfig.getWhiteList().split(",");
                whitelists = Arrays.asList(ips);
            }
            if(!whitelists.contains(ip)){
                return false;
            }
        }
        //未开启黑名单 下面的代码就不执行
        if(!villaConfig.isBlacklistFlag()){
            return true;
        }
        //当前ip在黑名单
        if(redisClient.existWithSet(BLACKLIST_KEY,ip)){
            Log.err(String.format("【黑名单】当前IP:[%s]在黑名单中,终止请求",ip));
            return false;
        }
        //判断请求数
        long reqSecond = System.currentTimeMillis() / 1000;
        String reqStr = redisClient.get(IP_REQ_KEY + ip + "_" + reqSecond);
        Integer req = Util.isNotNullOrEmpty(reqStr)?Integer.parseInt(reqStr):null;
        //当前ip这一秒不存在请求
        if(req == null){
            setReqIpTimeAndCount(ip,reqSecond,1);
            return true;
        }
        //当前ip这一秒的请求数 未超出限制
        if(req <= villaConfig.getIpMax()){
            setReqIpTimeAndCount(ip,reqSecond,++req);
            return true;
        }
        //存在数据 并且超限 加入黑名单 并更新黑名单
        redisClient.addSet(BLACKLIST_KEY,ip);
        return false;
    }
    /**
     * 验证签名是否正确
     */
    public boolean validateSign(String sign, long timestamp, HttpServletRequest request){
        if(Util.isNullOrEmpty(sign)){
            Log.err("【签名失败】sign为空");
            return false;
        }
        //请求有效性 1分钟内 如果当前签名是超出了1分钟有效性范围的 则拦截
        long sysTime = System.currentTimeMillis();
        if(Math.abs(sysTime-timestamp)>=1000* villaConfig.getSignDelay()){
            Log.err("【签名失败】timestamp和系统时间超出了[%s]秒，签名使用时间戳：%s,当前系统时间戳：%s",villaConfig.getSignDelay(),timestamp,sysTime);
            return false;
        }
        //如果开启参数加密 则获取一次参数
        if(villaConfig.isEncryptFlag()){
            //当前请求的uri包含排除的
            if(villaConfig.getExcludeUris().contains(request.getRequestURI())){
                return EncryptionUtil.getSign(timestamp).equals(sign.toUpperCase());
            }
            //不指定uri加密 或指定uri加密并且当前访问的uri命中指定uri
            if((Util.isNullOrEmpty(villaConfig.getEncryptURI())||
                Util.isNotNullOrEmpty(villaConfig.getEncryptURI())&&request.getRequestURI().contains(villaConfig.getEncryptURI()))
            ){
                String paramStr = ClassUtil.getParamStr(request);
                //套了一层双引号
                if (Util.isNotNullOrEmpty(paramStr) && paramStr.startsWith("\"")) {
                    paramStr = paramStr.replace("\"","");
                }
                String sysSign = EncryptionUtil.encryptMD5(EncryptionUtil.getSign(timestamp) + (Util.isNotNullOrEmpty(paramStr) ? paramStr : "")).toUpperCase(Locale.ROOT);
                boolean signEq = sysSign.equals(sign.toUpperCase());
                if(!signEq){
                    Log.err("【签名失败】系统签名=>"+sysSign+"\t\t 接收到的签名===>"+sign.toUpperCase()+"，当前系统开启了加密访问，请确认加密规则是否正确");
                }
                return signEq;
            }
        }
        return EncryptionUtil.getSign(timestamp).equals(sign.toUpperCase());
    }
    private void putErr(HttpServletResponse response, ResultDTO dto){
        PrintWriter writer = null;
        try{
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/json;charset=utf-8");
            writer = response.getWriter();
            writer.write(JSON.toJSONString(dto));
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
    /**
     * ip在同一秒的请求数
     * 有效时间仅1秒 所以5秒过期即可
     */
    private void setReqIpTimeAndCount(String ip,Long time,int count){
        redisClient.set(IP_REQ_KEY+ip+"_"+time,count+"",5);
    }
}
