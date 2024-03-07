package com.villa.util;

import fr.opensagres.xdocreport.core.io.IOUtils;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.springframework.util.StopWatch;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.zip.*;

/**压缩工具*/
public class ZIPUtil {
    public static void unFile(String src, String dest, String password) throws IOException {
        if (src.toLowerCase().endsWith(".zip")) {
            unZip(src, dest, password);
        } else if (src.toLowerCase().endsWith(".rar")) {
            unRar(src, dest, password);
        } else if (src.toLowerCase().endsWith(".7z")) {
            un7z(src, dest, password);
        }
    }

    private static void un7z(String src, String dest, String password) throws IOException {
        File srcFile = new File(src);//获取当前压缩文件
        Util.assertionIsTrue(srcFile.exists(), srcFile.getPath() + "压缩文件不存在");
        //开始解压
        SevenZFile zIn = null;
        if (Util.isNotNullOrEmpty(password)) {
            zIn = new SevenZFile(srcFile, password.toCharArray());
        } else {
            zIn = new SevenZFile(srcFile);
        }

        SevenZArchiveEntry entry;
        File file;
        while ((entry = zIn.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                file = new File(dest, entry.getName());
                if (!file.exists()) {
                    new File(file.getParent()).mkdirs();//创建此文件的上级目录
                }
                OutputStream out = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(out);
                int len = -1;
                byte[] buf = new byte[1024];
                while ((len = zIn.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                }
                // 关流顺序，先打开的后关闭
                bos.close();
                out.close();
            }
        }
    }

    private static void unRar(String src, String dest, String password) throws IOException {
        RandomAccessFile randomAccessFile;
        IInArchive inArchive;
        // 第一个参数是需要解压的压缩包路径，第二个参数参考JdkAPI文档的RandomAccessFile
        randomAccessFile = new RandomAccessFile(src, "r");
        if (Util.isNotNullOrEmpty(password)){
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile), password);
        }else{
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
        }

        ISimpleInArchive simpleInArchive = inArchive.getSimpleInterface();
        for (final ISimpleInArchiveItem item : simpleInArchive.getArchiveItems()) {
            final int[] hash = new int[]{0};
            if (!item.isFolder()) {
                ExtractOperationResult result;
                final long[] sizeArray = new long[1];

                File outFile = new File(dest + item.getPath());
                File parent = outFile.getParentFile();
                if ((!parent.exists()) && (!parent.mkdirs())) {
                    continue;
                }
                if (Util.isNotNullOrEmpty(password)) {
                    result = item.extractSlow(data -> {
                        try {
                            IOUtils.write(data, new FileOutputStream(outFile, true));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        hash[0] ^= Arrays.hashCode(data); // Consume data
                        sizeArray[0] += data.length;
                        return data.length; // Return amount of consumed
                    }, password);
                } else {
                    result = item.extractSlow(data -> {
                        try {
                            IOUtils.write(data, new FileOutputStream(outFile, true));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        hash[0] ^= Arrays.hashCode(data); // Consume data
                        sizeArray[0] += data.length;
                        return data.length; // Return amount of consumed
                    });
                }
                Util.assertionIsTrue(result == ExtractOperationResult.OK, "解压失败");
            }
        }

        inArchive.close();
        randomAccessFile.close();
    }

    /**
     * zip带密码解压
     */
    private static void unZip(String src,String dest, String passWord) throws ZipException {
        ZipFile zipFile;
        if (Util.isNotNullOrEmpty(passWord)) {
            zipFile = new ZipFile(src, passWord.toCharArray());
        } else {
            zipFile = new ZipFile(src);
        }
        zipFile.setCharset(Charset.forName("GBK"));
        zipFile.extractAll(dest);
    }

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

    /**
     * 压缩多个文件成一个zip文件
     *
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
}
