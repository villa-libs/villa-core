# 1. 核心功能概述

1. springboot2.2.6.RELEASE框架基础
2. 核心工具集
3. 动态拦截器
4. 动态参数防篡改
5. 动态参数加密
6. 用户授权token验证
7. 动态白名单
8. 动态黑名单
9. 动态redis
10. 动态国际化
11. 验证包
12. 缓存包
13. 事件机制包
14. 多数据源
15. SSH链接数据库
# 2. 功能模块简述
## 1. springboot2.2.6.RELEASE框架基础
### 使用
```
1. 单模块项目则直接在pom.xml中配置
2. 多模块项目在跟pom.xml中配置即可
```
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.6.RELEASE</version>
    <relativePath/>
</parent>
<dependencies>
    <dependency>
        <groupId>com.villa</groupId>
        <artifactId>core</artifactId>
        <version>${core-version}</version>
    </dependency>
</dependencies>
```
## 2. 核心工具集
```
此核心包提供了很多便于开发的工具类,如果有下列相关操作,可以使用这些工具类代替
```

### 1. ClassUtil:类/实例/请求参数相关工具类
1. 从src对象将属性赋值到desc的属性中
2. 获取某类的方法实例
3. 获取请求参数
4. 判断当前字节码是否运行在jar包中
### 2. EncryptionUtil:加解密相关工具类
1. SHA1加密
2. SHA256加密
3. MD5加密
4. AES加解密
5. DES加解密
6. RSA获取公私钥/公钥加密/私钥加密/公钥解密/私钥解密
### 3. FileUtil:文件相关工具类
1. 获取文件后缀
2. 获取文件MD5码
3. 获取文件类型
4. 下载文件
5. 写文件
6. 将在线文件转换为base64
### 4. HttpUtil:请求相关工具类
1. 发起form-data格式的get/post请求
2. 发起json格式的get/post请求
### 5. SpringContextUtil:spring实例工具类
1. 获取bean对象
### 6. SystemUtil:授权配套相关的工具类
1. 获取登录者信息
2. 获取登录者v-token
### 7. ThreadManager:线程池相关的工具类
1. 通过线程池执行异步任务
### 8. TimeUtil:时间相关的工具类
1. 指定日期加指定天数
2. 给定一个Date实例  得到这个时间中对应月的最大天数
3. 取两个时间的小时差  得到的差值保留一位小数
4. 时间格式转换为yyyy-MM-dd
5. 时间格式转换为yyyy-MM-dd HH:mm:ss
### 9. Util:验证/断言/随机数/ip...相关工具类
1. 各种规则相关验证
2. 各种规则相关断言
3. 获取随机数(数字格式)
4. 通过身份证获取生日
5. MD5加密
6. 获取随机数
7. 将传入的字符串首字母大写
8. 隐藏字符串中间的几位数
9. 将byte转为16进制
### 10. ZIPUtil:解压缩相关工具类
1. 数据压缩传输
2. gzip数据压缩
3. gzip数据解压
4. deflater 解压
5. deflater压缩
## 3. 动态拦截器
```text
动态拦截器的意思是可以对拦截器进行开关,因为拦截器中做了很多的事情,比如白名单、黑名单、登录授权验证、签名等等  
但是关闭拦截器的限制较大,需要先关闭参数防篡改、参数加密、redis、白名单访问、黑名单限制后才对对拦截器进行关闭  
具体看下面代码就明白了
```
```java
@Value("${villa.param.distort:false}")
private boolean paramDistort;
@Value("${villa.interceptor.flag:false}")
private boolean interceptorFlag;
@Value("${villa.encrypt.flag:false}")
private boolean encryptFlag;
@Value("${jedis.pool.host:}")
private String redisHost;
@Value("${villa.whitelist:}")
private String whiteList;
@Value("${villa.blacklist.flag:true}")
private boolean blacklistFlag;
@Value("${villa.encrypt.uri:}")
private String encryptURI;
//下述为拦截器开启条件
if(interceptorFlag||paramDistort||encryptFlag||blacklistFlag|| Util.isNotNullOrEmpty(whiteList)|| Util.isNotNullOrEmpty(redisHost)||Util.isNotNullOrEmpty(encryptURI))
```
## 4. 动态参数防篡改
```java
@Value("${villa.param.distort:false}")
private boolean paramDistort;
```
```text
如果上述配置为true则开启参数防篡改验证,会自动启动拦截器,并在拦截器中进行签名判断  此配置仅作为参数防篡改校验，不启用参数加密。而前端请求时需携带签名参数,签名规则如下:
```
```java
private static int[] dealKeys;

/**
 * 提供一个毫秒级时间戳 得到一个签名
 */
public static String getSign(long timestamp){
    return Util.MD5(timestamp+"_"+dealKeys[(int)timestamp%dealKeys.length]).toUpperCase();
}
/**
    控制器参数:最好要json格式接收
    签名除上述签名外,还需要对参数进行签名 最终签名为： sign = MD5(sign+paramJSONStr)  <=== 对参数签名的作用是防数据串改
    普通请求:paramJSONStr 就是参数的json字符串
    上传请求:
    参数:
    
    name:diqhwdqihu1dnqqw.png
    aa:"测试属性"
    
    参数中必须传文件的唯一md5码,若存在多个参数,则需要将参数进行自然排序后形成这样的字符串==> name1value1name2value2
    ==>aa测试属性namediqhwdqihu1dnqqw.png==> 最终签名为：MD5(sign+namediqhwdqihu1dnqqw.png)
 */
```
## 5. 动态参数加密
```java
@Value("${villa.encrypt.flag:false}")
private boolean encryptFlag;
@Value("${villa.encrypt.uri:}")
private String encryptURI;
```
```text
如果上述配置为true则开启参数加密,会自动启动拦截器,会在拦截器中启动参数防篡改验证。同时会启动过滤器,包装请求对象,并处理参数加解密  
如果encryptURI有值 仅代表启用包含此值的uri进行加密接收和加密响应
```
## 6. 用户授权token验证
```text
采用类似JWT实现机制,在JWT基础上增加了单点登录/多点登录动态调控  
```
## 7. 动态白名单
## 8. 动态黑名单
## 9. 动态redis
## 10. 动态国际化
## 11. 验证包
## 12. 缓存包
## 13. 事件机制包
## 14. 多数据源
```
核心包天生支持多数据源,具体配置参考下述yml。若仅是单库,只保留master配置即可
```
```yml
spring:
  datasource:
    # 采用动态选取 使用@DS在Service上指定非主数据源 事务配置需@Transactional(propagation = Propagation.REQUIRES_NEW)
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #严格匹配数据源,默认false. false使用默认数据源 true未匹配到指定数据源时抛异常
      datasource:
        # 主数据库 名字自定义 但是需要primary指定 若非必要 不修改即可
        master:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://xxx:3306/xxx?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false
          username: xxx
          password: xxx
        # nft数据库 名字自定义
        nft:
          driver-class-name: com.mysql.cj.jdbc.Driver
          url: jdbc:mysql://xxx:3306/xxx?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&serverTimezone=GMT%2B8&useSSL=false
          username: xxx
          password: xxx
```
## 15. SSH链接数据库


















