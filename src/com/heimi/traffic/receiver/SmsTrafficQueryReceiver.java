package com.heimi.traffic.receiver;

import static com.heimi.traffic.Constants.SMS_LEFT_TRAFFIC;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE;
import static com.heimi.traffic.Constants.SMS_USED_TRAFFIC;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heimi.traffic.utils.LogUtil;

public class SmsTrafficQueryReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.i(SmsTrafficQueryReceiver.class.getSimpleName(), "" + intent.getStringExtra(SMS_QUERY_RESPONSE));
		LogUtil.i(SmsTrafficQueryReceiver.class.getSimpleName(), "usedTraffic:" + intent.getStringExtra(SMS_USED_TRAFFIC));
		LogUtil.i(SmsTrafficQueryReceiver.class.getSimpleName(), "unusedTraffic:" + intent.getStringExtra(SMS_LEFT_TRAFFIC));
	}

}
