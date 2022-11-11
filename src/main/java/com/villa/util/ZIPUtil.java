package com.villa.util;

import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.*;

/**
 * GZIP 压缩工具
 */
public class ZIPUtil {
    /**
     * 数据压缩传输
     */
    public static void compress2out_zip(byte[] bytes, OutputStream out) throws IOException {
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(out);
            gos.write(bytes);
            gos.finish();
            gos.flush();
        } finally {
            if (gos != null) {
                gos.close();
            }
        }
    }

    /**
     * gzip数据压缩
     */
    public static byte[] compress_gzip(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = null;
        GZIPOutputStream gos = null;
        try {
            out = new ByteArrayOutputStream();
            gos = new GZIPOutputStream(out);
            gos.write(bytes);
            gos.finish();
            gos.flush();
        } finally {
            if (gos != null) {
                gos.close();
            }
            if (out != null) {
                out.close();
            }
        }
        return out.toByteArray();
    }

    /**
     * gzip数据解压
     */
    public static byte[] decompress_gzip(byte[] bytes) throws IOException {
        ByteArrayInputStream in = null;
        GZIPInputStream gin = null;
        ByteArrayOutputStream out = null;
        try {
            in = new ByteArrayInputStream(bytes);
            gin = new GZIPInputStream(in);
            out = new ByteArrayOutputStream();
            int count;
            byte data[] = new byte[1024];
            while ((count = gin.read(data, 0, 1024)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
            if (gin != null) {
                gin.close();
            }
            if (in != null) {
                in.close();
            }
        }
        return out.toByteArray();
    }

    /**
     * deflater 解压
     *
     * @param inputByte 待解压缩的字节数组
     * @return 解压缩后的字节数组
     * @throws IOException
     */
    public static byte[] uncompress_deflater(byte[] inputByte) throws DataFormatException {
        Inflater infl = new Inflater();
        infl.setInput(inputByte);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outByte = new byte[1024];
        try {
            while (!infl.finished()) {
                // 解压缩并将解压缩后的内容输出到字节输出流bos中
                int len = infl.inflate(outByte);
                if (len == 0) {
                    break;
                }
                bos.write(outByte, 0, len);
            }
            infl.end();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }

    /**
     * deflater压缩
     *
     * @param inputByte 待压缩的字节数组
     * @return 压缩后的数据
     * @throws IOException
     */
    public static byte[] compress_deflater(byte[] inputByte) {
        Deflater defl = new Deflater(Deflater.BEST_COMPRESSION);
        defl.setInput(inputByte);
        defl.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] outputByte = new byte[1024];
        try {
            while (!defl.finished()) {
                // 压缩并将压缩后的内容输出到字节输出流bos中
                int len = defl.deflate(outputByte);
                bos.write(outputByte, 0, len);
            }
            defl.end();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bos.toByteArray();
    }
	/** * 压缩多个文件成一个zip文件
	 * @param srcFiles：源文件列表
	 * @param destZipFile：压缩后的文件
	 */
	public static void toZip(File[] srcFiles, File destZipFile) {
		byte[] buf = new byte[1024];
		try {
			// ZipOutputStream类：完成文件或文件夹的压缩
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destZipFile));
			for (int i = 0; i < srcFiles.length; i++) {
				FileInputStream in = new FileInputStream(srcFiles[i]);
				// 给列表中的文件单独命名
				out.putNextEntry(new ZipEntry(srcFiles[i].getName()));
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.closeEntry();
				in.close();
			}
			out.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

    /**
     * 压缩成ZIP 方法2
     *
     * @param srcFiles 需要压缩的文件列表
     * @param out      压缩文件输出流
     */
    public static void toZip(List<File> srcFiles, OutputStream out) throws RuntimeException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start("toZip-2");
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(out);
            for (File sourceFile : srcFiles) {
                byte[] buf = new byte[1024];
                zos.putNextEntry(new ZipEntry(sourceFile.getName()));
                int len;
                FileInputStream in = new FileInputStream(sourceFile);
                while ((len = in.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
                zos.closeEntry();
                in.close();
            }
        } catch (Exception e) {
            throw new RuntimeException("压缩zip出错");
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        stopWatch.stop();
    }
	/**
	 * 解压文件
	 * @param zipFile：需要解压缩的文件
	 * @param descDir：解压后的目标目录
	 */
	public static void unZipFiles(File zipFile, String descDir) throws IOException {
		File destFile = new File(descDir);
		if (!destFile.exists()) {
			destFile.mkdirs();
		}
		// 解决zip文件中有中文目录或者中文文件
		ZipFile zip = new ZipFile(zipFile, Charset.forName("UTF-8"));
		for (Enumeration entries = zip.entries(); entries.hasMoreElements(); ) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			InputStream in = zip.getInputStream(entry);
			String curEntryName = entry.getName();
			// 判断文件名路径是否存在文件夹
			int endIndex = curEntryName.lastIndexOf('/');
			// 替换
			String outPath = (descDir + curEntryName).replaceAll("\\*", "/");
			if (endIndex != -1) {
				File file = new File(outPath.substring(0, outPath.lastIndexOf("/")));
				if (!file.exists()) {
					file.mkdirs();
				}
			}
			// 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
			File outFile = new File(outPath);
			if (outFile.isDirectory()) {
				continue;
			}
			OutputStream out = new FileOutputStream(outPath);
			byte[] buf1 = new byte[1024];
			int len;
			while ((len = in.read(buf1)) > 0) {
				out.write(buf1, 0, len);
			}
			in.close();
			out.close();
		}
	}
}
