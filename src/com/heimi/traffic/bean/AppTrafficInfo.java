package com.heimi.traffic.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * app info about traffic
 * id   int
   uid int
   appName
   packName
  time long  unit:ms
  long todMobTrafficTotal;//今天使用的数据流量，每隔一段时间更新
  long yesMobTrafficTotal;//昨天使用的数据流量，备用
  long monthMobTrafficTotal;//当月使用的数据流量，与当天的流量更新机制一致
 * @author sa
 * 
 */
@Table(name = "App_Traffic_Info")
public class AppTrafficInfo implements Serializable {
    private static final long serialVersionUID = 1L;
	private int id;
 	@Column(column = "uid")
 	private int uid;
 	@Column(column = "appName")
 	private String appName;
 	@Column(column = "packageName")
 	private String packageName;
 	@Column(column = "time")
 	private long time;//unit:ms	
 	@Column(column = "todMobTrafficTotal")
 	private long todMobTrafficTotal;//今天使用的数据流量，每隔一段时间更新
 	@Column(column = "yesMobTrafficTotal")
 	private long yesMobTrafficTotal;//昨天使用的数据流量，备用
 	@Column(column = "monthMobTrafficTotal")
 	private long monthMobTrafficTotal;//当月使用的数据流量，与当天的流量更新机制一致
 	
// 	private long mobTraffic
 	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packName) {
		this.packageName = packName;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
}
