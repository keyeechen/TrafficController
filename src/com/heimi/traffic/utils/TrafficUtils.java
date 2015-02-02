package com.heimi.traffic.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;

/**
 * traffic info fectching tools
 * 
 * @author sa
 * 
 */
public class TrafficUtils {

	/**
	 * get down traffic by uid
	 * 
	 * @param uid app uid
	 * @return traffic unit, converted automatically
	 */
	public static String getReceivedMobileTrafficByUid(long uid, Context context) {

		return Formatter.formatFileSize(context, TrafficStats.getUidRxBytes((int) uid));
	}

	/**
	 * get up traffic by uid
	 * 
	 * @param uid  app uid
	 * @return
	 */
	public static String getSentMobileTrafficByUid(long uid, Context context) {
		return Formatter.formatFileSize(context, TrafficStats.getUidTxBytes((int) uid));
	}
	
	//sim卡是否可读 
	public static boolean isCanUseSim(Context context) { 
	    try { 
	        TelephonyManager mgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
	 
	        return TelephonyManager.SIM_STATE_READY == mgr.getSimState(); 
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
	    return false; 
	}
	
	/**
	 * 返回sim卡的序列号
	 * @param context
	 * @return
	 */
	public static String getSimSerialNumber(Context context)
	{
		String serial=null;
		 TelephonyManager tele = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE); 
		    if(isCanUseSim(context))
	        serial= tele.getSimSerialNumber();         
	        return serial;
	}
}
