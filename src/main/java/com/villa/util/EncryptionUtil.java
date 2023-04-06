package com.villa.util;

import com.villa.dto.RSAEncryptKeyDTO;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionUtil {
    /**
     * 可以在项目中修改这个属性值,每个项目可以使用不同的值进行签名计算
     */
    private static int[] dealKeys = new int[]{
            0x07, 0xB6, 0x79, 0x56, 0x7A, 0x5C, 0x4A, 0xBE, 0x1D, 0xF1, 0xB2, 0x10, 0x3C, 0x5E, 0xDC, 0xA6,
            0x56, 0xE7, 0x88, 0x25, 0x87, 0x95, 0xD5, 0x85, 0x76, 0x7D, 0xEA, 0x66, 0xF5, 0x0A, 0xC3, 0xA8,
            0x55, 0x28, 0x67, 0x14, 0x06, 0xE7, 0xCB, 0x68, 0xAC, 0x2E, 0x00, 0x36, 0x57, 0x2F, 0xD2, 0xE2,
            0x54, 0xE9, 0xC6, 0xA3, 0x03, 0xC6, 0x07, 0x33, 0xBD, 0xF1, 0x6D, 0x46, 0x62, 0xFD, 0x82, 0xCF,
            0xA3, 0x50, 0x15, 0xB2, 0x53, 0xA4, 0x9C, 0x93, 0x98, 0x55, 0x8E, 0xF8, 0xC1, 0x0C, 0x15, 0x71,
            0x42, 0x6A, 0xA4, 0xF1, 0x5D, 0x72, 0xB1, 0xC4, 0xF6, 0xF0, 0x56, 0xAE, 0xCA, 0x77, 0x44, 0x45,
            0x21, 0x1B, 0x93, 0x40, 0x49, 0x89, 0x52, 0x76, 0x2C, 0x64, 0xB8, 0x3B, 0xF9, 0x8D, 0x51, 0xA5,
            0x80, 0x2C, 0x92, 0x39, 0xF7, 0xAD, 0xAF, 0x59, 0x1F, 0x06, 0xDE, 0x5A, 0x1D, 0x91, 0x1C, 0xDB,
            0x6F, 0xAD, 0xC1, 0xE8, 0xE5, 0xD4, 0xB4, 0x7C, 0x3E, 0x61, 0x73, 0x2D, 0xCE, 0xCD, 0x01, 0xDF,
            0x5E, 0xCE, 0x60, 0xB7, 0x83, 0xD1, 0x39, 0xA9, 0xF3, 0x35, 0x05, 0xBA, 0x88, 0x78, 0x97, 0xFC,
            0x3D, 0x2F, 0xF9, 0x36, 0x2A, 0x38, 0xB0, 0x25, 0x16, 0xA7, 0x08, 0x8C, 0xF6, 0x21, 0xC8, 0x22,
            0xBC, 0x90, 0x48, 0x35, 0x9A, 0x0D, 0x1A, 0xD9, 0xFA, 0xCC, 0x70, 0xAA, 0x42, 0x3F, 0xB6, 0xE1,
            0xBB, 0x41, 0x17, 0x74, 0xC2, 0x48, 0x7E, 0x80, 0xD6, 0x09, 0xC5, 0x24, 0x60, 0x30, 0x0E, 0xE3,
            0xFA, 0x92, 0x66, 0x43, 0xE1, 0x8A, 0x4D, 0xD7, 0x1B, 0x6B, 0x23, 0x65, 0xA0, 0x12, 0x9D, 0x9B,
            0xE0, 0x93, 0xE5, 0xD2, 0xE3, 0xF4, 0xDC, 0x41, 0xA4, 0x3A, 0x10, 0x2B, 0x96, 0xED, 0x1B, 0x1E,
            0xA9, 0xB4, 0x34, 0x11, 0x94, 0xA6, 0x75, 0x34, 0xD8, 0x89, 0xFC, 0x4F, 0x3B, 0x22, 0xB1, 0xA7
    };

    /**
     * 覆盖加密算法规则集
     */
    public static void setDealKeys(int[] dealKeys) {
        EncryptionUtil.dealKeys = dealKeys;
    }

    /**
     * 提供一个毫秒级时间戳 得到一个签名
     */
    public static String getSign(long timestamp) {
        return Util.MD5(timestamp + "_" + dealKeys[(int) (timestamp % dealKeys.length)]).toUpperCase();
    }
    //--------------------------------------HMAC-SHA256 加密开始-------------------------------------------------------------

    /**
     * HMAC-SHA256常用于JWT签名的算法
     *
     * @param secret  密钥
     * @param message 加密内容
     */
    public static String encrypt_HMAC_SHA256(String secret, String message) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmacSha256.init(secret_key);
            byte[] bytes = hmacSha256.doFinal(message.getBytes());
            StringBuilder hs = new StringBuilder();
            String tmp;
            for (int n = 0; bytes != null && n < bytes.length; n++) {
                tmp = Integer.toHexString(bytes[n] & 0XFF);
                if (tmp.length() == 1)
                    hs.append('0');
                hs.append(tmp);
            }
            return hs.toString().toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //--------------------------------------SHA1 加密开始-------------------------------------------------------------

    /**
     * SHA1加密
     * 使用SHA1算法对字符串进行加密
     *
     * @param data 要加密的数据
     * @return
     */
    public static String encrypt_SHA1(String data) {
        if (Util.isNullOrEmpty(data)) {
            return null;
        }
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(data.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;

            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //--------------------------------------SHA256 加密-------------------------------------------------------------

    /**
     * SHA256加密
     *
     * @param data 需要加密的数据
     */
    public static String encrypt_SHA256(String data) {
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(data.getBytes("UTF-8"));
            return Util.byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //--------------------------------------MD5 加密-------------------------------------------------------------

    /**
     * MD5加密
     */
    public static String encrypt_MD5(String data) {
        //如果为空 原路返回
        if (Util.isNullOrEmpty(data)) {
            return null;
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(data.getBytes("utf-8"));
            StringBuilder hex = new StringBuilder(bs.length * 2);
            for (byte b : bs) {
                if ((b & 0xFF) < 0x10) {
                    hex.append("0");
                }
                hex.append(Integer.toHexString(b & 0xFF));
            }
            return hex.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //--------------------------------------AES 对称加解密相关-------------------------------------------------------------

    /**
     * AES加密
     *
     * @param data 需要加密的数据
     * @param key  加解密的对称密钥
     * @return 加密字符串 加密失败返回null
     */
    public static String encrypt_AES(String data, String key) {
        if (Util.isNullOrEmpty(data) || Util.isNullOrEmpty(key)) {
            return null;
        }
        try {
            Util.assertionIsFalse(key.length() != 32, "加密失败,密钥长度须32位.");
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec keySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes("utf-8")));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * AES解密
     *
     * @param data 需要解密的数据
     * @param key  加解密的对称密钥
     * @return 解密字符串 解密失败返回null
     */
    public static String decrypt_AES(String data, String key) {
        if (Util.isNullOrEmpty(data) || Util.isNullOrEmpty(key)) {
            return null;
        }
        try {
            Util.assertionIsFalse(key.length() != 32, "加密失败,密钥长度须32位.");
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.getDecoder().decode(data);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "utf-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //--------------------------------------DES 对称加解密相关-------------------------------------------------------------

    /**
     * DES加密字符串
     * key 对称密钥
     * data 需要加密的数据
     */
    public static String encrypt_DES(String data, String key) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(key.getBytes("UTF-8"));
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * DES解密字符串
     *
     * @param key  对称密钥
     * @param data 需要解密的数据
     */
    public static String decrypt_DES(String data, String key) {
        try {
            // DES算法要求有一个可信任的随机数源
            SecureRandom random = new SecureRandom();
            // 创建一个DESKeySpec对象
            DESKeySpec desKey = new DESKeySpec(key.getBytes("UTF-8"));
            // 创建一个密匙工厂
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            // 将DESKeySpec对象转换成SecretKey对象
            SecretKey securekey = keyFactory.generateSecret(desKey);
            // Cipher对象实际完成解密操作
            Cipher cipher = Cipher.getInstance("DES");
            // 用密匙初始化Cipher对象
            cipher.init(Cipher.DECRYPT_MODE, securekey, random);
            // 真正开始解密操作
            return new String(cipher.doFinal(Base64.getDecoder().decode(data)), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //--------------------------------------DES 对称加解密结束-------------------------------------------------------------
    //--------------------------------------RSA 非对称加解密开始-----------------------------------------------------------

    /**
     * 生成随机的base64格式RSA公钥和私钥
     * 如果报错 则返回null
     */
    public static RSAEncryptKeyDTO createRSAKey() {
        try {
            // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 初始化密钥对生成器，密钥长度 512位
            keyPairGen.initialize(512, new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            return new RSAEncryptKeyDTO(Base64.getEncoder().encodeToString(publicKey.getEncoded()), Base64.getEncoder().encodeToString(privateKey.getEncoded()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA公钥加密
     *
     * @param data      加密字符串
     * @param publicKey base64格式的公钥
     * @return 密文 加密失败返回null
     */
    public static String encrypt_public_key(String data, String publicKey) {
        try {
            //base64编码的公钥
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").
                    generatePublic(new X509EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return new String(Base64.getEncoder().encode(cipher.doFinal(data.getBytes("UTF-8"))), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA私钥解密
     *
     * @param data       加密字符串
     * @param privateKey base64格式的私钥
     * @return 明文 解密失败返回null
     */
    public static String decrypt_private_key(String data, String privateKey) {
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.getDecoder().decode(data);
            //base64编码的私钥
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * RSA私钥加密
     *
     * @param data       加密字符串
     * @param privateKey base64格式的私钥
     * @return 加密字符串 加密失败返回null
     */
    public static String encrypt_private_key(String data, String privateKey) {
        try {
            //base64编码的公钥
            byte[] decoded = Base64.getDecoder().decode(privateKey);
            PrivateKey priKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA加密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, priKey);
            return new String(Base64.getEncoder().encode(cipher.doFinal(data.getBytes())), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * RSA公钥解密
     *
     * @param data      加密字符串
     * @param publicKey base64格式的公钥
     * @return 明文 解密失败返回null
     */
    public static String decrypt_public_key(String data, String publicKey) {
        try {
            //64位解码加密后的字符串
            byte[] inputByte = Base64.getDecoder().decode(data);
            //base64编码的私钥
            byte[] decoded = Base64.getDecoder().decode(publicKey);
            PublicKey pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, pubKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    //--------------------------------------RSA 非对称加解密结束-----------------------------------------------------------
}
