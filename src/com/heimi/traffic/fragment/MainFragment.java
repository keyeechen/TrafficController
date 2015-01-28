package com.heimi.traffic.fragment;

import static com.heimi.traffic.Constants.ACTION_SENDCONTEX_SMSQUERYTRAFFIC;
import static com.heimi.traffic.Constants.ACTION_SENDSMS_QUERYTRAFFIC;
import static com.heimi.traffic.Constants.NETWORKMANAGEMENT_SERVICE;
import static com.heimi.traffic.Constants.NETWORK_STATS_SERVICE;
import static android.net.NetworkTemplate.buildTemplateMobileAll;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.NetworkPolicyManager;
import android.net.NetworkTemplate;
import android.os.Bundle;
import android.os.INetworkManagementService;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.settings.DataUsageSummary.AppItem;
import com.android.settings.net.ChartData;
import com.android.settings.net.ChartDataLoader;
import com.android.settings.net.NetworkPolicyEditor;
import com.heimi.traffic.R;
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
	private NetworkPolicyEditor mPolicyEditor;
	private NetworkTemplate mTemplate;
    private ChartData mChartData;
    private AppItem mCurrentApp = null;//不为空则是app详情模式
    
	private Activity context;
	private Button traffic_match_btn;// 流量校正按钮
	private Timer timer;
	private boolean isFinish = false;
	TextView tv_data_traffic_used_number;// 已用流量
	TextView tv_data_traffic_left_number;// 剩余流量
	private float traffic_used_num;
	private float traffic_left_num;
	private BroadcastReceiver smsTrafficQueryReceiver = new BroadcastReceiver() {//短信流量校正的receiver

		@Override
		public void onReceive(Context context, Intent intent) {

		}
	};
	//异步从文件中获取流量数据厚的回调，在onLoadFinished里更新界面
	private final LoaderCallbacks<ChartData> mChartDataCallbacks = new LoaderCallbacks<
            ChartData>() {
        @Override
        public Loader<ChartData> onCreateLoader(int id, Bundle args) {
            return new ChartDataLoader(getActivity(), mStatsSession, args);
        }

        @Override
        public void onLoadFinished(Loader<ChartData> loader, ChartData data) {
            mChartData = data;
            // calcuate policy cycles based on available data
            updatePolicy(true);
        
        }

        @Override
        public void onLoaderReset(Loader<ChartData> loader) {
            mChartData = null;
        }
    };

	private static final String PREF_FILE = "data_usage";
    private static final String TEST_SUBSCRIBER_PROP = "test.subscriberid";
    private static final int LOADER_CHART_DATA = 2;//加载图表数据的标识（对应Settings里数据流量界面）

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
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
		init();
		setTheNumberProgressBar();
	}

	/**
	 * init;
	 */
	private void init() {
		initViews();
		// 注册短信查询流量广播接收者
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
		context.registerReceiver(smsTrafficQueryReceiver, filter);
        //流量相关的参数初始化
		mNetworkService = INetworkManagementService.Stub.asInterface(ServiceManager .getService(NETWORKMANAGEMENT_SERVICE));
		mStatsService = INetworkStatsService.Stub.asInterface(ServiceManager.getService(NETWORK_STATS_SERVICE));
		mPolicyManager = NetworkPolicyManager.from(context);
		mConnService = ConnectivityManager.from(context);// 隐藏api，源码下编译是可以通过的
		mPrefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
		mPolicyEditor = new NetworkPolicyEditor(mPolicyManager);
		mPolicyEditor.read();

	}

	/**
	 * 初始化view对象
	 */
	private void initViews() {
		traffic_match_btn = (Button) context.findViewById(R.id.traffic_match_btn);
		tv_data_traffic_used_number = (TextView) context.findViewById(R.id.tv_data_traffic_used_number);
		tv_data_traffic_left_number = (TextView) context.findViewById(R.id.tv_data_traffic_left_number);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		updateUI();
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

	}

	/**
	 * 短信校正流量
	 */
	private void sendSmsQuery() {
		Intent intent = new Intent();
		intent.setAction(ACTION_SENDSMS_QUERYTRAFFIC);
		getActivity().sendBroadcast(intent);
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
	 * 更新界面，主要流量信息
	 */
	private void updateUI() {
		final TelephonyManager tele = TelephonyManager.from(context);//隐藏api
		mTemplate = buildTemplateMobileAll(getActiveSubscriberId(context));
		 // kick off loader for network history
        // TODO: consider chaining two loaders together instead of reloading
        // network history when showing app detail.
        getLoaderManager().restartLoader(LOADER_CHART_DATA,ChartDataLoader.buildArgs(mTemplate, mCurrentApp), mChartDataCallbacks);

	}

	private static String getActiveSubscriberId(Context context) {
		final TelephonyManager tele = TelephonyManager.from(context);
		final String actualSubscriberId = tele.getSubscriberId();//隐藏api
		return SystemProperties.get(TEST_SUBSCRIBER_PROP, actualSubscriberId);
	}
   /**
    *根据netpolicy.xml更新
 * @param flag
 */
    private void updatePolicy(boolean flag)
   {
	   
   }
}
