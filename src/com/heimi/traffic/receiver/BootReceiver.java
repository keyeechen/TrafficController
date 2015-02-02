package com.heimi.traffic.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heimi.traffic.service.BackService;
import com.heimi.traffic.utils.LogUtil;

/**
 * 接收开机广播,开启后台服务
 * @author sa
 *
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
         LogUtil.i(BootReceiver.class.getSimpleName(),intent.getAction());
	     Intent service=new Intent(context,BackService.class);	    
	     context.startService(service);
	}

}
