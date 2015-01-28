package com.heimi.traffic.utils;

import android.content.Context;
import android.net.TrafficStats;
import android.text.format.Formatter;

/**
 * traffic info fectching tools
 * 
 * @author sa
 * 
 */
public class TraffficUtils {

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
}
