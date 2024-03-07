package com.villa.log;

import com.villa.log.io.MultiOutputStream;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 1. 默认将日志生成在项目jar的同级目录的logs文件夹下以日期为子文件夹
 * 2. 默认日志文件以yyyy-MM-dd HH:mm:ss 命名 2020-02-02 02:02:02.log
 * 3. 默认日志文件以M为单位进行分割  分割后命名加上 毫秒值 2020-02-02 02:02:02 598.log
 */
public class Log {
    /**
     * 日志输出等级 默认err
     */
    private static int log_lv = LogLevel.debug;
    private static File logoutFile;
    private static File logerrFile;
    private static File logPath;
    //标准输出流
    private static PrintStream out;
    //标准错误输出流
    private static PrintStream err;
    //日志文件分割的大小 M为单位
    public static int logSize = 10;
    //方法调用层级 需要往方法栈中的多少层才能获取到调用者真正所在方法
    public static int index = 3;
    public static void init() {
        init(log_lv, true, null);
    }

    public static void init(int lv) {
        init(lv, true, null);
    }

    /**
     * 初始化日志等级
     *
     * @param lv 通过LogLevel中常量赋值
     */
    public static void init(int lv, boolean removeOldLogFlag, File outPath) {
        log_lv = lv;
        logPath = outPath;
        //如果没有传文件路径 则使用默认项目路径
        if (logPath == null) {
            logPath = new File(System.getProperty("user.dir"), "logs");
        }
        if (logPath.exists() && logPath.isFile()) {
            throw new RuntimeException("【日志组件】日志生成路径不能为文件.");
        } else if (!logPath.exists()) {
            //如果路径不存在 则创建此路径
            logPath.mkdirs();
        }
        //启动日志输出线程
        if (removeOldLogFlag) {
            //删除已经存在的日志文件
            removeBeforeLogs();
        }
        try {
            initOutStream();
            out("【日志组件】初始化完成!");
        } catch (IOException e) {
            e.printStackTrace();
            err("【日志组件】输出流初始化失败!");
        }
    }

    /**
     * 初始化输出流
     */
    private static void initOutStream() throws IOException {
        if (out == null) {
            out = System.out;
        }
        if (err == null) {
            err = System.err;
        }
        logoutFile = getFile("out");
        logerrFile = getFile("err");
        PrintStream outPrintStream = new PrintStream(new MultiOutputStream(Files.newOutputStream(logoutFile.toPath()), out));
        PrintStream errPrintStream = new PrintStream(new MultiOutputStream(Files.newOutputStream(logerrFile.toPath()), err));
        System.setOut(outPrintStream);
        System.setErr(errPrintStream);
    }

    /**
     * 删除之前的日志
     */
    private static void removeBeforeLogs() {
        File[] files = logPath.listFiles();
        for (File file : files) {
            if (file.getName().endsWith(".log")) {
                file.delete();
            }
        }
    }
    /**
     * 常规输出debug日志
     */
    public static void out(Object msg,Object...params) {
        if (log_lv == LogLevel.err) {
            return;
        }
        System.out.println(String.format(getMsg(msg,index), params));
    }

    public static void err(Object msg,Object...params){
        String realMsg = String.format(getMsg(msg,index), params);
        System.err.println(realMsg);
    }

    private static String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm:ss").format(new Date()) + "\t";
    }

    private static String getMsg(Object msg,int index) {
        //全限定名  [index]代表的是从调用开始 第几层调用者
        String fullClassName = Thread.currentThread().getStackTrace()[index].getClassName();
        //方法名
        String methodName = Thread.currentThread().getStackTrace()[index].getMethodName();
        //行号
        int lineNumber = Thread.currentThread().getStackTrace()[index].getLineNumber();
        return getTime() + "\t" + msg + "\t" + fullClassName + "." + methodName + "():" + lineNumber;
    }
    private static File getFile(String outType) throws IOException {
        File file = new File(logPath, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) +("err".equals(outType)?"-err":"")+ ".log");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 判断当前文件多大了  是否超过10M 如果超过10M就将此文件关闭并压缩 重新生成新的日志文件
     */
    public static void validateFile() {
        if (logoutFile.length() < 1024L * 1024 * logSize) {
            return;
        }
        //大于10M了
        try {
            //先获取原本的两个输出流
            PrintStream out = System.out;
            PrintStream err = System.err;
            //重新初始化流 再替换
            initOutStream();
            //这里可以将存在的日志文件进行压缩
            //最后关闭老的
            out.close();
            err.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
