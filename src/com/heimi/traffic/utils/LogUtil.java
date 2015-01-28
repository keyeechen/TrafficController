package com.heimi.traffic.utils;

import android.util.Log;
import static com.heimi.traffic.Constants.DEBUG_MODE;
/**
 * only output log info in debug mode
 * @author keyeechen
 */
public class LogUtil {


    public static void v(String tag, String msg) {
        if(DEBUG_MODE) {
            if(msg != null) {
                Log.v(tag, msg);
            }
        }
    }

    public static void d(String tag, String msg) {
        if(DEBUG_MODE) {
            if(msg != null) {
                Log.d(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if(DEBUG_MODE) {
            if(msg != null) {
                Log.i(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg) {
        if(DEBUG_MODE) {
            if(msg != null) {
                Log.w(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg) {
        if(DEBUG_MODE) {
            if(msg != null) {
                Log.e(tag, msg);
            }
        }
    }
}
