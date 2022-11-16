package com.villa.log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteErrHandler {
    private File logPath;
    private File logFile;
    private FileWriter fileWriter;
    public WriteErrHandler(File logPath) throws IOException {
        this.logPath = logPath;
        logFile = new File(logPath, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + "-err.log");
        if (!logFile.exists()) {
            logFile.createNewFile();
        }
        fileWriter = new FileWriter(logFile);
    }

    public void write(String err){
        if(logFile.length() > 1024 * 1024 * Log.logSize){
            logFile = new File(logPath, new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()) + "-err.log");
        }
        try {
            fileWriter.append(err);
            fileWriter.append(java.security.AccessController.doPrivileged(
                    new sun.security.action.GetPropertyAction("line.separator")));
            fileWriter.flush();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
