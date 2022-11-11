package com.villa.util;

import com.villa.dto.ResultDTO;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileUtil {
	//文件常见类型
	private final static Map<String, String> SIMPLE_FILE_TYPE_MAP = new HashMap<String, String>();
	static{
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

	/**
	 * 根据mime类型获取简单类型 所谓的简单的类型就是图片-img 音频-audio 视频-video 压缩包-zip 文档-doc 网页-html
	 * @param mimeType
	 * @return
	 */
	public static String getFileType(String mimeType){
		return SIMPLE_FILE_TYPE_MAP.containsKey(mimeType)?SIMPLE_FILE_TYPE_MAP.get(mimeType):"unknown";
	}
	/**
	 * 下载线上资源
	 * @param fileURL 下载地址
	 */
	public static ResultDTO downloadFile(String fileURL, String desc, String fileName, Map<String,String> header) {
		try {
			//创建文件目录
			File file = new File(desc);
			if (!file.exists()) {
				file.mkdirs();
			}
			File newFile = new File(desc,fileName);
			if(newFile.exists()) {
				return ResultDTO.putSuccess("ok");
			}
			URL url = new URL(fileURL);
			//链接网络地址
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			if(header!=null) {
				Set<String> keys = header.keySet();
				for (String key : keys) {
					connection.addRequestProperty(key,header.get(key));
				}
			}
			InputStream is = connection.getInputStream();
			//获取链接的输出流
			//根据输入流写入文件
			FileOutputStream out = new FileOutputStream(newFile);
			int i = 0;
			while((i = is.read()) != -1){
				out.write(i);
			}
			out.close();
			is.close();
			return ResultDTO.putSuccess("ok");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.put500("资源下载失败.");
		}
	}
	public static void writeFile(String desc, String fileName, String content,boolean append){
		File file = new File(desc,fileName);
		try {
			//不是追加模式 并且已存在,直接返回
			if(!append&&file.exists()){
				return;
			}
			//如果不存在 就创建文件夹
			if(!file.exists()){
				file.getParentFile().mkdirs();
			}
			//开始写入文件
			FileOutputStream out = new FileOutputStream(file,append);
			out.write(content.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static ResultDTO writeFile(String desc, String fileName, MultipartFile uplaodFile){
		File file = new File(desc,fileName);
		try {
			//如果已存在,直接返回
			if(!file.exists()){
				file.getParentFile().mkdirs();
				FileOutputStream out = new FileOutputStream(file);
				out.write(uplaodFile.getBytes());
				out.close();
			}
			return ResultDTO.putSuccess("上传成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.put500("上传失败,请稍后重试.");
		}
	}

	/**
	 * 写文件
	 * @param desc 目标路径
	 * @param fileName 文件名
	 * @param uplaodFile 源文件
	 * @param cover 是否覆盖
	 */
	public static ResultDTO writeFile(String desc, String fileName, MultipartFile uplaodFile, boolean cover){
		File file = new File(desc,fileName);
		try {
			//如果已存在,直接返回
			if(!file.exists()||cover){
				file.getParentFile().mkdirs();
				FileOutputStream out = new FileOutputStream(file);
				out.write(uplaodFile.getBytes());
				out.close();
			}
			return ResultDTO.putSuccess("上传成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return ResultDTO.put500("上传失败,请稍后重试.");
		}
	}
	/**
	 * 获取文件的md5值
	 * @return
	 */
	public static String getFileMD5(InputStream in) throws IOException {
		return DigestUtils.md5DigestAsHex(in);
	}
	/**
	 * 获取文件的md5值
	 * @return
	 */
	public static String getFileMD5(byte[] bs){
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
		conn.setConnectTimeout(5 * 1000);
		// 通过输入流获取图片数据
		return conn.getInputStream();
	}
	public static byte[] readFile(InputStream in)throws Exception{
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
	 * 获取文件后缀名
	 */
	public static String getEndFix(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index!=-1) {
			return fileName.substring(index + 1);
		}
		return "";
	}
}