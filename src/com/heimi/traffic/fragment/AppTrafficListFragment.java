package com.heimi.traffic.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heimi.traffic.R;
import com.heimi.traffic.bean.AppInfo;
import com.heimi.traffic.utils.TraffficUtils;

/**
 * 每个app的流量消耗列表界面：fragment
 * 
 * @author sa
 * 
 */
public class AppTrafficListFragment extends Fragment {
	private TextView tv                     ;
	private PackageManager pm;
	private List<PackageInfo> packages;
	private ArrayList<AppInfo> appList = new ArrayList<AppInfo>(); 

	public AppTrafficListFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		init();
//		tv = (TextView) getActivity().findViewById(R.id.tv);
//        for(int i=0;i<1;i++)
//        {
//        	tv.append(appList.get(i).getAppName()+"received:"+TraffficUtils.getReceivedMobileTrafficByUid(appList.get(i).getUid(), getActivity())+"\n");
//        	tv.append(appList.get(i).getAppName()+"sent:"+TraffficUtils.getSentMobileTrafficByUid(appList.get(i).getUid(), getActivity())+"\n");
//        }
	}

	/**
	 * init
	 */
	private void init() {
		pm = getActivity().getPackageManager();
		packages = pm.getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			AppInfo tmpInfo = new AppInfo();
			tmpInfo.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
			tmpInfo.setPackageName( packageInfo.packageName);
			tmpInfo.setVersionName(packageInfo.versionName);
			tmpInfo.setVersionCode(packageInfo.versionCode);
			tmpInfo.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
			tmpInfo.setUid(packageInfo.applicationInfo.uid);
			if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				appList.add(tmpInfo);// non-system app info
			}
		}
	}
}
