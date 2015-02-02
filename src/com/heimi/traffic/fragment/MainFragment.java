package com.heimi.traffic.fragment;

import static android.net.NetworkTemplate.buildTemplateMobileAll;
import static android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static com.heimi.traffic.Constants.ACTION_SENDCONTEX_SMSQUERYTRAFFIC;
import static com.heimi.traffic.Constants.ACTION_SENDSMS_QUERYTRAFFIC;
import static com.heimi.traffic.Constants.DEFAULT_TRAFFIC_NUM;
import static com.heimi.traffic.Constants.GB_UNIT;
import static com.heimi.traffic.Constants.MB_UNIT;
import static com.heimi.traffic.Constants.NETWORKMANAGEMENT_SERVICE;
import static com.heimi.traffic.Constants.NETWORK_STATS_SERVICE;
import static com.heimi.traffic.Constants.SMS_QUERYTRAFFIC_KEY;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE_FAIL;
import static com.heimi.traffic.Constants.SMS_QUERY_RESPONSE_OK;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicyManager;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.mms.query.ChinaMobileSmsQueryTrafficInfo;
import com.android.mms.query.ChinaUnicomSmsQueryTrafficInfo;
import com.android.mms.query.CommonQueryTrafficInfo;
import com.heimi.traffic.Constants;
import com.heimi.traffic.R;
import com.heimi.traffic.bean.AppItem;
import com.heimi.traffic.net.ChartData;
import com.heimi.traffic.net.ChartDataLoader;
import com.heimi.traffic.net.NetworkPolicyEditor;
import com.heimi.traffic.utils.TrafficUtils;
import com.heimi.traffic.view.NumberCircleProgressBar;

/**
 * 主界面的fragment（主界面展示流量使用总体情况）
 * 
 * @author sa
 * 
 */
public class MainFragment extends Fragment implements View.OnClickListener {
	private INetworkManagementService mNetworkService;
	private INetworkStatsService mStatsService;
	private NetworkPolicyManager mPolicyManager;
	private ConnectivityManager mConnService;
	private INetworkStatsSession mStatsSession;
	private SharedPreferences mPrefs;
	private SharedPreferences mTrafficPrefs;
	private NetworkPolicyEditor mPolicyEditor;
	private NetworkTemplate mTemplate;
	private ChartData mChartData;
	private AppItem mCurrentApp = null;// 不为空则是app详情模式

	private Activity context;
	private Button traffic_match_btn;// 流量校正按钮
	private Timer timer;
	private boolean isFinish = false;
	TextView tv_data_traffic_used_number;// 已用流量
	TextView tv_data_traffic_left_number;// 剩余流量
	TextView tv_data_traffic_used_txt;// 已用流量单位
	TextView tv_data_traffic_left_txt;// 剩余流量单位
	NumberCircleProgressBar circleProgressBar;
	private float traffic_used_num;
	private float traffic_left_num;
	private boolean smsQueryingFlag;
	private String simSerial;
	private BroadcastReceiver smsTrafficQueryReceiver = new BroadcastReceiver() {// 短信流量校正的receiver

		@Override
		public void onReceive(Context context, Intent intent) {
			smsQueryingFlag = false;
			traffic_match_btn.setText(R.string.traffic_match);
			traffic_match_btn.setEnabled(true);
			String flag = intent.getStringExtra(SMS_QUERY_RESPONSE);
			if (TextUtils.equals(flag, SMS_QUERY_RESPONSE_FAIL)) 	{
				Toast.makeText(context, R.string.text_traffic_match_failed, 0).show();
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
                    updateBody();
                   
				}
			   
			}
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
	};
	// 异步从文件中获取流量数据厚的回调，在onLoadFinished里更新界面
	private final LoaderCallbacks<ChartData> mChartDataCallbacks = new LoaderCallbacks<ChartData>() {
		@Override
		public Loader<ChartData> onCreateLoader(int id, Bundle args) {
			return new ChartDataLoader(getActivity(), mStatsSession, args);
		}

		@Override
		public void onLoadFinished(Loader<ChartData> loader, ChartData data) {
			mChartData = data;
			updateDetailData();
			updatePolicy(true);

		}

		@Override
		public void onLoaderReset(Loader<ChartData> loader) {
			mChartData = null;
		}
	};
    
	private static final String PREF_TRAFFIC_PREFIX = "heimi_traffic_";//不同的卡存在不同的SharedPreferences里
	private  long left_total_bytes=0L;
	private long used_total_bytes_before_start_time=0L;//start_time之前所用的流量
	private long used_month_total_bytes=0L;
	private long left_month_total_bytes=0L;
	private int left_percentage=0;//剩余流量百分比
	private long start_time=Long.MIN_VALUE;//从系统文件里获取流量信息的开始时间
	private static final String PREF_FILE = "data_usage";
	private static final String TEST_SUBSCRIBER_PROP = "test.subscriberid";
	private static final int LOADER_CHART_DATA = 2;// 加载图表数据的标识（对应Settings里数据流量界面）
	

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
//		setTheNumberProgressBar();
		initViews();

	}

	/**
	 * init;
	 */
	private void init() {
		context = getActivity();
		// 注册短信查询流量广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
		context.registerReceiver(smsTrafficQueryReceiver, filter);
		// 流量相关的参数初始化
		mNetworkService = INetworkManagementService.Stub.asInterface(ServiceManager
		        .getService(NETWORKMANAGEMENT_SERVICE));
		mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService(NETWORK_STATS_SERVICE));
		mPolicyManager = NetworkPolicyManager.from(context);
		// mConnService = ConnectivityManager.from(context);// 隐藏api，不能够调用
		mConnService = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			mStatsSession = mStatsService.openSession();
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
		mPrefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		simSerial=TrafficUtils.getSimSerialNumber(context);
		if(!TextUtils.isEmpty(simSerial))//只有sim序列号不为空才实例化Prefs
		mTrafficPrefs=context.getSharedPreferences(PREF_TRAFFIC_PREFIX+simSerial, Context.MODE_PRIVATE);
		mPolicyEditor = new NetworkPolicyEditor(mPolicyManager);
		mPolicyEditor.read();

	}

	/**
	 * 初始化view对象
	 */
	private void initViews() {
		traffic_match_btn = (Button) context.findViewById(R.id.traffic_match_btn);
		traffic_match_btn.setOnClickListener(this);
		tv_data_traffic_used_number = (TextView) context.findViewById(R.id.tv_data_traffic_used_number);
		tv_data_traffic_left_number = (TextView) context.findViewById(R.id.tv_data_traffic_left_number);
		tv_data_traffic_used_txt = (TextView) context.findViewById(R.id.tv_data_traffic_used_txt);
		tv_data_traffic_left_txt = (TextView) context.findViewById(R.id.tv_data_traffic_left_txt);
		circleProgressBar = (NumberCircleProgressBar) context.findViewById(R.id.numbercircleprogress_bar);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateBody();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		getActivity().unregisterReceiver(smsTrafficQueryReceiver);// 注销广播
		if (timer != null) {
			timer.cancel();
			timer = null;
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.traffic_match_btn:
			if (!smsQueryingFlag) {
				sendSmsQuery();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 短信校正流量
	 */
	private void sendSmsQuery() {
		smsQueryingFlag = true;
		traffic_match_btn.setText(R.string.traffic_matching);
		traffic_match_btn.setEnabled(false);
		Intent intent = new Intent();
		intent.setAction(ACTION_SENDSMS_QUERYTRAFFIC);
		context.sendBroadcast(intent);
	}

	/**
	 * 测试流量进度条
	 */
	public void setTheNumberProgressBar() {

		final NumberCircleProgressBar bnp = (NumberCircleProgressBar) getActivity().findViewById(
		        R.id.numbercircleprogress_bar);
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (!isFinish) {
							bnp.incrementProgressBy(2);
							if (bnp.isFinished()) {
								isFinish = false;
							}
						}
					}
				});
			}
		}, 1000, 100);
	}

	/**
	 * 根据template，加载流量信息（异步回调）
	 */
	private void updateBody() {
		// final TelephonyManager tele = TelephonyManager.from(context);//隐藏api
//		final TelephonyManager tele = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// 隐藏api
		mTemplate = buildTemplateMobileAll(getActiveSubscriberId(context));
		// kick off loader for network history
		// TODO: consider chaining two loaders together instead of reloading
		// network history when showing app detail.
		getLoaderManager().restartLoader(LOADER_CHART_DATA, ChartDataLoader.buildArgs(mTemplate, mCurrentApp),
		        mChartDataCallbacks);

	}

	/**
	 * 获取回调数据，更新界面
	 */
	private void updateDetailData() {
		start_time=mTrafficPrefs.getLong("start_time", Long.MIN_VALUE);
		final long start =start_time ;
		final long end = System.currentTimeMillis();
		final long now = System.currentTimeMillis();
		NetworkStatsHistory.Entry entry = null;
		if (mChartData != null) {
			entry = mChartData.network.getValues(start, end, now, null);
			used_total_bytes_before_start_time=mTrafficPrefs.getLong("used_total_bytes_before_start_time", 0L);
			final long totalUsedBytes = entry != null ? entry.rxBytes + entry.txBytes +used_total_bytes_before_start_time: 0;
			left_total_bytes=mTrafficPrefs.getLong("left_total_bytes", (long)(DEFAULT_TRAFFIC_NUM*GB_UNIT)-totalUsedBytes);
			final String totalUsedPhrase = Formatter.formatFileSize(context, totalUsedBytes);
			final String leftTotalPhrase=Formatter.formatFileSize(context, left_total_bytes);
//			final String rangePhrase = formatDateRange(context, start, end);
			tv_data_traffic_used_number.setText(totalUsedPhrase.substring(0, totalUsedPhrase.length() - 2));
			tv_data_traffic_used_txt.setText(String.format(getString(R.string.text_used_mb),
		    totalUsedPhrase.substring(totalUsedPhrase.length() - 2)));
			left_percentage=mTrafficPrefs.getInt("left_percentage", (int)((left_total_bytes*100)/(left_total_bytes+totalUsedBytes)));
			circleProgressBar.setProgress(left_percentage);
			tv_data_traffic_left_number.setText(leftTotalPhrase.substring(0, leftTotalPhrase.length() - 2));
			tv_data_traffic_left_txt.setText(String.format(getString(R.string.text_left_mb),leftTotalPhrase.substring(leftTotalPhrase.length() - 2)));

		}

	}

	private static String getActiveSubscriberId(Context context) {
		// final TelephonyManager tele = TelephonyManager.from(context);//隐藏api
		final TelephonyManager tele = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);// 隐藏api
		final String actualSubscriberId = tele.getSubscriberId();
		return SystemProperties.get(TEST_SUBSCRIBER_PROP, actualSubscriberId);
	}

	/**
	 * 根据netpolicy.xml更新
	 * 
	 * @param flag
	 */
	private void updatePolicy(boolean flag) {

	}

	private static final StringBuilder sBuilder = new StringBuilder(50);
	private static final java.util.Formatter sFormatter = new java.util.Formatter(sBuilder, Locale.getDefault());
	public static String formatDateRange(Context context, long start, long end) {
		final int flags = FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH;
		synchronized (sBuilder) {
			sBuilder.setLength(0);
			return DateUtils.formatDateRange(context, sFormatter, start, end, flags, null).toString();
		}
	}
}
