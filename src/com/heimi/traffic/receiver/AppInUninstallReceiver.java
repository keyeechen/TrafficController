package com.heimi.traffic.receiver;

import static com.heimi.traffic.Constants.ACTION_APP_INSTALLED;
import static com.heimi.traffic.Constants.ACTION_APP_UNINSTALLED;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.heimi.traffic.utils.LogUtil;

/**
 * receiver broadcast when one app is installed or uninstalled
 * @author sa
 *
 */
public class AppInUninstallReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        String packageName = intent.getDataString();  
        String action = intent.getAction();  
        
        if(TextUtils.equals(action, ACTION_APP_UNINSTALLED))//uninstall
        {
        	LogUtil.i(AppInUninstallReceiver.class.getSimpleName(), "uninstall:"+packageName);
        }
        else if(TextUtils.equals(action, ACTION_APP_INSTALLED))//install
        {
        	LogUtil.i(AppInUninstallReceiver.class.getSimpleName(), "install:"+packageName);

        }
         
	}

}
