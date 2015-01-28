package com.heimi.traffic.bean;

import java.io.Serializable;

import android.graphics.drawable.Drawable;

/**
 * basic app info like appName,version,uid,etc
 * 
 * @author sa
 * 
 */
public class AppInfo implements Serializable {
    private static final long serialVersionUID = 8049853882959777551L;
	private  String appName = "";
	private String packageName = "";
	private String versionName = "";
	private int versionCode = 0;
	private Drawable appIcon = null;
	private long uid;// get traffic info by uid
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public Drawable getAppIcon() {
		return appIcon;
	}
	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}
	public long getUid() {
		return uid;
	}
	public void setUid(long uid) {
		this.uid = uid;
	}

}
