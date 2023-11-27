package com.villa.log;

import com.villa.util.Util;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LogConfig {
    @Value("${villa.log.path:}")
    private String logPath;
    @Value("${villa.log.removeOldLogFlag:true}")
    private boolean removeOldLogFlag;
    @Value(("${villa.log.lv:debug}"))
    private String lv;
    private static final String LOG_LV_DEBUG = "debug";
    public void init(){
        if(Util.isNullOrEmpty(logPath)){
            Log.init(LOG_LV_DEBUG.equals(lv)?LogLevel.debug:LogLevel.err,removeOldLogFlag,null);
            return;
        }
        Log.init(LOG_LV_DEBUG.equals(lv)?LogLevel.debug:LogLevel.err,removeOldLogFlag,new File(logPath));
    }
}
