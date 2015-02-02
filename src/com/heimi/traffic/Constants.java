package com.heimi.traffic;

/**
 * 
 * constant variables
 * 
 * @author sa
 * 
 */
public interface Constants {

	String DEFAULT_DB_NAME = "android747.db";
	String ACTION_APP_INSTALLED = "android.intent.action.PACKAGE_ADDED";// app
																		// installed
	String ACTION_APP_UNINSTALLED = "android.intent.action.PACKAGE_REMOVED";// app
																			// uninstalled
	boolean DEBUG_MODE = true;// debug mode
	/*
	 * sms traffic query related
	 */
	String TRAFFIC_SMS_QUERY_USED = "traffic_sms_query_telecom_used";// used
																	 // traffic
																	 // from sms
																	 // query
	String TRAFFIC_SMS_QUERY_LEFT = "traffic_sms_query_telecom_left";// used
																	 // traffic
																	 // from sms
																	 // query
	String ACTION_SENDCONTEX_SMSQUERYTRAFFIC = "android.provider.sms.SENDCONTEX_SMSQUERYTRAFFIC";// received
																								 // from
																								 // mms
	String ACTION_SENDSMS_QUERYTRAFFIC = "android.provider.sms.SENDSMS_QUERYTRAFFIC";// send
																					 // to
																					 // mms
	String SMS_USED_TRAFFIC = "usedTraffic";// result:used traffic
	String SMS_LEFT_TRAFFIC = "unusedTraffic";// result:left traffic
	String SMS_LOCAL_USED_TRAFFIC = "sms_local_used_traffic";// 本地使用流量
	String SMS_LOCAL_LEFT_TRAFFIC = "sms_local_left_traffic";// 本地剩余流量
	String SMS_NATIONAL_USED_TRAFFIC = "sms_national_used_traffic";// 全国使用流量
	String SMS_NATIONAL_LEFT_TRAFFIC = "sms_national_left_traffic";// 全国剩余流量
	String SMS_QUERY_RESPONSE = "SmsSendSuccessForQueryTrafficFlag";// query
																  // result
	String SMS_QUERY_RESPONSE_OK = "success";
	String SMS_QUERY_RESPONSE_FAIL = "failure";
	String SMS_QUERYTRAFFIC_KEY = "com.android.mms.query.objecttran.key";

	// Context hiden Constants
	String NETWORKMANAGEMENT_SERVICE = "network_management";
	/** {@hide} */
	String NETWORK_STATS_SERVICE = "netstats";
	/** {@hide} */
	String NETWORK_POLICY_SERVICE = "netpolicy";
	//SharedPreferences
	String PREF_TRAFFIC_PREFIX = "heimi_traffic_";
	long KB_UNIT=1024;
    long MB_UNIT=KB_UNIT*KB_UNIT;
    long GB_UNIT=KB_UNIT*MB_UNIT;
    float  DEFAULT_TRAFFIC_NUM=3.5f;//以3.5G流量为默认值
}
