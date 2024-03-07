package com.villa.util;

import com.villa.dto.ResultDTO;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.*;

public class FileUtil {
    //文件常见类型
    private final static Map<String, String> SIMPLE_FILE_TYPE_MAP = new HashMap<String, String>();

    static {
        //----------------------图片格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("image/gif", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/jpeg", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/jp2", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/png", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/tiff", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/bmp", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/svg+xml", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/webp", "img");
        SIMPLE_FILE_TYPE_MAP.put("image/x-icon", "img");
        //----------------------音频格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("audio/mpeg", "audio");
        SIMPLE_FILE_TYPE_MAP.put("audio/midi", "audio");
        SIMPLE_FILE_TYPE_MAP.put("audio/x-wav", "audio");
        SIMPLE_FILE_TYPE_MAP.put("audio/x-mpegurl", "audio");
        SIMPLE_FILE_TYPE_MAP.put("audio/x-m4a", "audio");
        SIMPLE_FILE_TYPE_MAP.put("audio/ogg", "audio");
        SIMPLE_FILE_TYPE_MAP.put("audio/x-realaudio", "audio");
        //----------------------视频格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("video/mp4", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/mpeg", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/quicktime", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/x-m4v", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/x-ms-wmv", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/x-msvideo", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/webm", "video");
        SIMPLE_FILE_TYPE_MAP.put("video/x-flv", "video");
        //----------------------压缩文件格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("application/x-gzip", "zip");
        SIMPLE_FILE_TYPE_MAP.put("application/zip", "zip");
        SIMPLE_FILE_TYPE_MAP.put("application/rar", "zip");
        SIMPLE_FILE_TYPE_MAP.put("application/x-tar", "zip");
        //----------------------文档文件格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("application/msword", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/vnd.ms-excel", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/vnd.ms-powerpoint", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/pdf", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/rtf", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/kswps", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/kset", "doc");
        SIMPLE_FILE_TYPE_MAP.put("application/ksdps", "doc");
        //----------------------文本格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("text/plain", "txt");
        SIMPLE_FILE_TYPE_MAP.put("application/x-javascript", "js");
        SIMPLE_FILE_TYPE_MAP.put("text/javascript", "js");
        SIMPLE_FILE_TYPE_MAP.put("text/css", "css");
        SIMPLE_FILE_TYPE_MAP.put("text/html", "html");
        SIMPLE_FILE_TYPE_MAP.put("application/xhtml+xml", "html");
        SIMPLE_FILE_TYPE_MAP.put("text/xml", "xml");
        //----------------------其他格式------------------------------
        SIMPLE_FILE_TYPE_MAP.put("application/x-photoshop", "psd");
        SIMPLE_FILE_TYPE_MAP.put("application/x-coreldraw", "cdr");
        SIMPLE_FILE_TYPE_MAP.put("application/x-shockwave-flash", "swf");
        SIMPLE_FILE_TYPE_MAP.put("text/x-vcard", "vcf");
        SIMPLE_FILE_TYPE_MAP.put("application/x-httpd-php", "php");
        SIMPLE_FILE_TYPE_MAP.put("application/java-archive", "jar");
        SIMPLE_FILE_TYPE_MAP.put("application/vnd.android.package-archive", "apk");
        SIMPLE_FILE_TYPE_MAP.put("application/octet-stream", "exe");
        SIMPLE_FILE_TYPE_MAP.put("application/x-x509-user-cert", "crt");
    }
    public static void deleteFile(String path){
        File file = new File(path);
        deleteFile(file);
    }
    /**递归删除文件*/
    public static void deleteFile(File file) {
        if(!file.exists())return;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                deleteFile(f);
            }
        }
        file.delete();
    }
    /**递归获取后缀文件*/
    public static List<File> getFilesByEndsWith(String path, String endsWith) {
        List<File> list = new ArrayList<>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    list.addAll(getFilesByEndsWith(f.getAbsolutePath(), endsWith));
                } else if (f.getName().endsWith(endsWith)) {
                    list.add(f);
                }
            }
        }
        return list;
    }
    /**
     * 根据mime类型获取简单类型 所谓的简单的类型就是图片-img 音频-audio 视频-video 压缩包-zip 文档-doc 网页-html
     *
     * @param mimeType
     * @return
     */
    public static String getFileType(String mimeType) {
        return SIMPLE_FILE_TYPE_MAP.containsKey(mimeType) ? SIMPLE_FILE_TYPE_MAP.get(mimeType) : "unknown";
    }

    /**
     * 下载线上资源返回字节数组
     * @param fileURL 下载地址
     */
    public static byte[] downloadFileToBytes(String fileURL,Map<String, String> header) throws IOException {
        InputStream in = downloadFileToStream(fileURL,header);
        byte[] bytes = new byte[102400];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int index = 0;
            while (-1 != (index = in.read(bytes, 0, bytes.length))) {
                baos.write(bytes, 0, index);
            }
            in.close();
            return baos.toByteArray();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            if (in != null) {
                in.close();
            }
            if (baos != null) {
                baos.flush();
                baos.close();
            }
        }
    }
    /**
     * 下载线上资源 返回流
     * @param fileURL 下载地址
     */
    public static InputStream downloadFileToStream(String fileURL,Map<String, String> header) throws IOException {
        URL url = new URL(fileURL);
        //链接网络地址
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(1000 * 60 * 60);
        connection.setReadTimeout(1000 * 60 * 60);
        if (header != null) {
            Set<String> keys = header.keySet();
            for (String key : keys) {
                connection.addRequestProperty(key, header.get(key));
            }
        }
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
        return connection.getInputStream();
    }
    /**
     * 下载线上资源
     *
     * @param fileURL 下载地址
     * @param desc 文件存放目标地址(可以是临时文件)
     * @param fileName 文件存放的文件名
     * @param header 访问时携带的请求头
     */
    public static ResultDTO downloadFileToFile(String fileURL, String desc, String fileName, Map<String, String> header) throws IOException {
        //创建文件目录
        File file = new File(desc);
        if (!file.exists()) {
            file.mkdirs();
        }
        File newFile = new File(desc, fileName);
        if (newFile.exists()) {
            return ResultDTO.putSuccess("ok");
        }
        InputStream is = downloadFileToStream(fileURL,header);
        //获取链接的输出流
        //根据输入流写入文件
        FileOutputStream out = new FileOutputStream(newFile);
        int i = 0;
        while ((i = is.read()) != -1) {
            out.write(i);
        }
        out.close();
        is.close();
        return ResultDTO.putSuccess("ok");
    }

    public static void writeFile(String desc, String fileName, String content, boolean append) throws IOException {
        File file = new File(desc, fileName);
        //不是追加模式 并且已存在,直接返回
        if (!append && file.exists()) {
            return;
        }
        //如果不存在 就创建文件夹
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        //开始写入文件
        FileOutputStream out = new FileOutputStream(file, append);
        out.write(content.getBytes());
        out.close();
    }

    /**
     * @param desc       目标文件
     * @param fileName   文件名
     * @param uplaodFile 文件
     * @return
     * @throws IOException
     */
    public static ResultDTO writeFile(String desc, String fileName, MultipartFile uplaodFile) throws IOException {
        File file = new File(desc, fileName);
        //如果已存在,直接返回
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            out.write(uplaodFile.getBytes());
            out.close();
        }
        return ResultDTO.putSuccess("文件成功写入!");
    }

    /**
     * 写文件
     *
     * @param desc       目标路径
     * @param fileName   文件名
     * @param uplaodFile 源文件
     * @param cover      是否覆盖
     */
    public static ResultDTO writeFile(String desc, String fileName, MultipartFile uplaodFile, boolean cover) throws IOException {
        File file = new File(desc, fileName);
        //如果已存在,直接返回
        if (!file.exists() || cover) {
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            out.write(uplaodFile.getBytes());
            out.close();
        }
        return ResultDTO.putSuccess("文件成功写入!");
    }

    /**
     * 获取文件的md5值
     *
     * @return
     */
    public static String getFileMD5(InputStream in) throws IOException {
        return DigestUtils.md5DigestAsHex(in);
    }

    /**
     * 获取文件的md5值
     *
     * @return
     */
    public static String getFileMD5(byte[] bs) {
        return DigestUtils.md5DigestAsHex(bs);
    }

    /**
     * 将在线文件转为base64
     */
    public static String encodeImageToBase64(String strUrl) throws Exception {
        return encodeImageToBase64(readRemoteFile(strUrl));
    }

    public static String encodeImageToBase64(InputStream in) throws Exception {
        // 对字节数组Base64编码
        return Base64.getEncoder().encodeToString(readFile(in));// 返回Base64编码过的字节数组字符串
    }

    /**
     * 读取远程文件为流
     */
    public static InputStream readRemoteFile(String strUrl) throws IOException {
        URL url = new URL(strUrl);
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        // 打开链接
        HttpURLConnection conn;
        conn = (HttpURLConnection) url.openConnection();
        // 设置请求方式为"GET"
        conn.setRequestMethod("GET");
        // 超时响应时间为5秒
        conn.setConnectTimeout(1000 * 60 * 60);
        conn.setReadTimeout(1000 * 60 * 60);
        // 通过输入流获取图片数据
        return conn.getInputStream();
    }

    /**
     * 文件绝对路径或相对路径
     *
     * @return 读取到的缓存图像
     */
    public static BufferedImage getBufferedImage(String url) throws IOException {
        URL restURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
        //超时时间设置为1小时
        conn.setConnectTimeout(1000 * 60 * 60);
        conn.setReadTimeout(1000 * 60 * 60);
        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        conn.setRequestMethod("GET");
        conn.setDoOutput(true);
        conn.setAllowUserInteraction(false);
        return ImageIO.read(conn.getInputStream());
    }

    public static byte[] readFile(InputStream in) throws Exception {
        // 得到图片的二进制数据，以二进制封装得到数据，具有通用性
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // 创建一个Buffer字符串
        byte[] buffer = new byte[1024];
        // 每次读取的字符串长度，如果为-1，代表全部读取完毕
        int len = 0;
        // 使用一个输入流从buffer里把数据读取出来
        while ((len = in.read(buffer)) != -1) {
            // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
            outStream.write(buffer, 0, len);
        }
        // 关闭输入流
        in.close();
        return outStream.toByteArray();
    }

    /**
     * 保存图片到文件夹
     *
     * @param savedImg
     * @param path
     */
    public static void saveImage(BufferedImage savedImg, String path) throws IOException {
        ImageIO.write(savedImg, "png", new File(path));
    }

    /**
     * 重新放大缩小图片
     *
     * @param img
     * @param targetWidth
     * @param targetHeight
     */
    public static BufferedImage resizeImage(BufferedImage img, int targetWidth, int targetHeight) throws IOException {
        int type = img.getColorModel().getTransparency();
        int width = img.getWidth();
        int height = img.getHeight();
        // 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        BufferedImage newImg = new BufferedImage(targetWidth, targetHeight, type);
        Graphics2D graphics2d = newImg.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(img, 0, 0, targetWidth, targetHeight, 0, 0, width, height, null);
        graphics2d.dispose();
        return newImg;
    }

    /**
     * 图片上添加文字
     *
     * @param color    十六进制颜色  如：#ffffff
     * @param fontName 字体 如 微软雅黑
     */
    public static BufferedImage drawTextInImg(BufferedImage img, String text, int size, int left, int top, String color, String fontName) {
        Graphics2D g = img.createGraphics();
        g.setColor(getColor(color));
        g.setBackground(Color.white);
        Font font = new Font("微软雅黑", Font.BOLD, size);
        g.setFont(font);
        g.drawString(text, left, top);
        g.dispose();
        return img;
    }

    /**
     * @param color 十六进制颜色  如：#ffffff
     */
    public static Color getColor(String color) {
        if (color.charAt(0) == '#') {
            color = color.substring(1);
        }
        if (color.length() != 6) {
            return null;
        }
        int r = Integer.parseInt(color.substring(0, 2), 16);
        int g = Integer.parseInt(color.substring(2, 4), 16);
        int b = Integer.parseInt(color.substring(4), 16);
        return new Color(r, g, b);
    }

    /**
     * 获取图片InputStream
     */
    public static InputStream getImageStream(BufferedImage destImg) throws IOException {
        BufferedImage bi = destImg;
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageOutputStream imOut;
        imOut = ImageIO.createImageOutputStream(bs);
        ImageIO.write(bi, "png", imOut);
        return new ByteArrayInputStream(bs.toByteArray());
    }

    /**
     * 图片改为圆形 + 边框 如果不要边框将边框宽度设置为0即可
     *
     * @param border 边框宽度
     * @param color  边框颜色
     */
    public static BufferedImage circularImages(BufferedImage img, int border, String color) throws IOException {
        int width = img.getWidth();
        // 透明底的图片
        BufferedImage newImg = new BufferedImage(width, width, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = newImg.createGraphics();
        //把图片切成一个园
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Ellipse2D.Double shape = new Ellipse2D.Double(border-2, border-2, width - border, width - border);
        //进行裁剪
        graphics.setClip(shape);
        graphics.drawImage(img, border-2, border-2, width - border, width - border, null);
        graphics.dispose();
        //抗锯齿
        graphics = newImg.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //画笔大小
        Stroke s = new BasicStroke(border, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(s);
        graphics.setColor(FileUtil.getColor(color));
        //在圆形的边画一条边框线
        graphics.drawOval(border / 2, border / 2, width - (border + 1), width - (border + 1));
        graphics.dispose();
        ImageIO.write(newImg, "PNG", File.createTempFile("temp", "png"));
        return newImg;
    }
    /**
     * 图片灰度图
     */
    public static BufferedImage grayImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        BufferedImage grayBufferedImage = new BufferedImage(width, height, bufferedImage.getType());
        for (int i = 0; i < bufferedImage.getWidth(); i++) {
            for (int j = 0; j < bufferedImage.getHeight(); j++) {
                final int color = bufferedImage.getRGB(i, j);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                int newPixel = colorToRGB(255, gray, gray, gray);
                grayBufferedImage.setRGB(i, j, newPixel);
            }
        }
        return grayBufferedImage;
    }

    public static int colorToRGB(int alpha, int red, int green, int blue) {
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;
        return newPixel;

    }
    //图片二值化
    public static BufferedImage binaryImage(BufferedImage image, int threshold) {
        int w = image.getWidth();
        int h = image.getHeight();
        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();
        BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb = image.getRGB(x, y);
                int gray = rgb & 0xff;
                if (gray < threshold) {
                    bi.setRGB(x, y, black);
                } else {
                    bi.setRGB(x, y, white);
                }
            }
        }
        return bi;
    }
    //膨胀腐蚀
    public static BufferedImage erode(BufferedImage image, int[] kernel) {
        int black = new Color(0, 0, 0).getRGB();
        int white = new Color(255, 255, 255).getRGB();
        int w = image.getWidth();
        int h = image.getHeight();
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int min = 255;
                for (int i = x; i < x + kernel[0]; i++) {
                    for (int j = y; j < y + kernel[1]; j++) {
                        if (i >= 0 && i < w && j >= 0 && j < h) {
                            int value = image.getRGB(i, j) & 0xff;
                            if (value < min) {
                                min = value;
                            }
                        }
                    }
                }
                if (min == 255) {
                    image.setRGB(x, y, white);
                } else {
                    image.setRGB(x, y, black);
                }
            }
        }
        return image;
    }
    /**
     * 获取文件后缀名
     */
    public static String getEndFix(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1);
        }
        return "";
    }

    /**
     * 从一个完整的URL中获取最后一个/后的文件名
     */
    public static String getFileNameByUrl(String url){
        int lastIndex = url.lastIndexOf("/");
        if(lastIndex !=-1){
            return url.substring(lastIndex+1);
        }
        return url;
    }
}
