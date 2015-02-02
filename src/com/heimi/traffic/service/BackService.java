package com.heimi.traffic.service;

import static com.heimi.traffic.Constants.ACTION_SENDCONTEX_SMSQUERYTRAFFIC;
import static com.heimi.traffic.Constants.MB_UNIT;
import static com.heimi.traffic.Constants.PREF_TRAFFIC_PREFIX;
import static com.heimi.traffic.Constants.SMS_QUERYTRAFFIC_KEY;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE_FAIL;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE_OK;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.mms.query.ChinaMobileSmsQueryTrafficInfo;
import com.android.mms.query.ChinaUnicomSmsQueryTrafficInfo;
import com.android.mms.query.CommonQueryTrafficInfo;
import com.heimi.traffic.Constants;
import com.heimi.traffic.R;
import com.heimi.traffic.utils.LogUtil;
import com.heimi.traffic.utils.TrafficUtils;

/**
 * 开机启动的Service，用于在应用未启动的情形下，从短信应用发来的广播中获取流量的信息
 * 
 * @author sa
 * 
 */
public class BackService extends Service {
	private SharedPreferences mTrafficPrefs;
	private String simSerial;
	private float traffic_left_num=0.0f;
	private float traffic_used_num=0.0f;
	private BroadcastReceiver smsTrafficQueryReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String flag = intent.getStringExtra(SMS_QUERY_RESPONSE);
			if (TextUtils.equals(flag, SMS_QUERY_RESPONSE_FAIL)) 	{
				Toast.makeText(context, R.string.text_traffic_match_failed, Toast.LENGTH_SHORT).show();
				return;
			} 
		   else if
			(TextUtils.equals(flag, SMS_QUERY_RESPONSE_OK)) {
				Object trafficQueryResult = intent.getExtras().get(SMS_QUERYTRAFFIC_KEY);
				Editor editor=mTrafficPrefs.edit();
				if (trafficQueryResult instanceof ChinaMobileSmsQueryTrafficInfo) {//移动卡暂时按联通的标准走
					ChinaMobileSmsQueryTrafficInfo mobileResult = (ChinaMobileSmsQueryTrafficInfo) trafficQueryResult;
					if(!TextUtils.isEmpty(mobileResult.unusedTotalTraffic) &&!TextUtils.isEmpty(mobileResult.usedTotalTraffic))
					{
						genTrafficAuto(mobileResult, editor);					
					}
					if(!TextUtils.isEmpty(mobileResult.unusedMonthTraffic) &&!TextUtils.isEmpty(mobileResult.usedMonthTraffic))
					{
						genTrafficManual(mobileResult, editor);
					}
					
				} else if (trafficQueryResult instanceof ChinaUnicomSmsQueryTrafficInfo) {
					//联通的cxll查询流量方式，返回的是月消费情况，而月消费返回的始终是0.00B，视为无效
					ChinaUnicomSmsQueryTrafficInfo unionResult = (ChinaUnicomSmsQueryTrafficInfo) trafficQueryResult;
					//目前分两种返回形式：联通自动短信提示的流量格式；手动查询返回的短信格式
					//联通自动短信提示的流量格式
					if(!TextUtils.isEmpty(unionResult.unusedTotalTraffic) &&!TextUtils.isEmpty(unionResult.usedTotalTraffic))
					{
						genTrafficAuto(unionResult, editor);					
					}
					//手动查询返回的短信格式。注意：本月使用流量返回不准确，剩余流量是准确的。
					if(!TextUtils.isEmpty(unionResult.unusedMonthTraffic) &&!TextUtils.isEmpty(unionResult.usedMonthTraffic))
					{
						genTrafficManual(unionResult, editor);
					}
					editor.commit();
				}
			   
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		LogUtil.i(BackService.class.getSimpleName(), "onCreate");
		// TODO Auto-generated method stub
		// 注册短信查询流量广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
		registerReceiver(smsTrafficQueryReceiver, filter);
		simSerial=TrafficUtils.getSimSerialNumber(this);
		if(!TextUtils.isEmpty(simSerial))//只有sim序列号不为空才实例化Prefs
		mTrafficPrefs=getSharedPreferences(PREF_TRAFFIC_PREFIX+simSerial, Context.MODE_PRIVATE);
		super.onCreate();
	}
	@Override
	public void onDestroy() {
		unregisterReceiver(smsTrafficQueryReceiver);
	    super.onDestroy();
	}
	/**
	 * 联通手动查询结果
	 * @param unionResult
	 * @param editor
	 */
	private void genTrafficManual(CommonQueryTrafficInfo unionResult, Editor editor) {
        String month_left_traffic_str;
        String month_used_traffic_str;
        month_left_traffic_str=unionResult.unusedMonthTraffic;//因为当前流量卡不清零，统计的为总的剩余流量
        month_used_traffic_str=unionResult.usedMonthTraffic;//暂不纳入统计,也不要在此时更新start_time
        //校验不合法直接返回
        if(!month_used_traffic_str.matches("[0-9,.]+")|| !month_left_traffic_str.matches("[0-9,.]+")) return;
        traffic_left_num=Float.valueOf(month_left_traffic_str);
        editor.putLong("left_total_bytes", (long)traffic_left_num*MB_UNIT);//把剩余流量值存入SP里
    }

	/**
	 * 联通自动查询结果
	 * @param unionResult
	 * @param editor
	 */
	private void genTrafficAuto(CommonQueryTrafficInfo unionResult, Editor editor) {
        String used_total_traffic_str;
        String left_total_traffic_str;
        used_total_traffic_str=unionResult.unusedTotalTraffic;
        left_total_traffic_str=unionResult.usedTotalTraffic;
        if(!used_total_traffic_str.matches("[0-9,.]+") ||!left_total_traffic_str.matches("[0-9,.]+")) return;
        traffic_left_num=Float.valueOf(left_total_traffic_str);
        traffic_used_num=Float.valueOf(used_total_traffic_str);
        editor.putLong("start_time", System.currentTimeMillis());
        final int left_percentage=(int)((traffic_left_num*100)/(traffic_left_num+traffic_used_num));
        editor.putInt("left_percentage", left_percentage);
        editor.putLong("left_total_bytes", (long)traffic_left_num*Constants.MB_UNIT);
        editor.putLong("used_total_bytes_before_start_time", (long)traffic_used_num*MB_UNIT);
    }

}

	