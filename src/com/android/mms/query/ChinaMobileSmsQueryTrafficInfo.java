package com.android.mms.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

@SuppressLint("ParcelCreator")
public class ChinaMobileSmsQueryTrafficInfo extends
		CommonQueryTrafficInfo implements Parcelable
{
	private static final String LOG_TAG = "ChinaMobileSmsQueryTrafficInfo";
    private final String mOperateChinaMobileQueryTrafficMaxCountUsed  = "100";
    private final String mOperateChinaMobileQueryTrafficMaxCountUunsed  = "100";

	private String usedMonthNativeCommonTraffic;
	private String unusedMonthNativeCommonTraffic;
	private String usedMonthNativeIdleTraffic;
	private String unusedMonthNativeIdleTraffic;

	public int mTrafficQueryCountByUsedForChinaMobile = 0;
	public int mTrafficQueryCountByUnusedForChinaMobile = 0;
	public String mFilterTrafficUsedForChinaMobile[] = new String[Integer.parseInt(mOperateChinaMobileQueryTrafficMaxCountUsed, 10)];
	public String mFilterTrafficUnusedForChinaMobile[] = new String[Integer.parseInt(mOperateChinaMobileQueryTrafficMaxCountUunsed, 10)];

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);

		parcel.writeString(unusedMonthNativeCommonTraffic);
		parcel.writeString(unusedMonthNativeIdleTraffic);
	}

	public static final Parcelable.Creator<ChinaMobileSmsQueryTrafficInfo> CREATOR = new Creator<ChinaMobileSmsQueryTrafficInfo>()
	{
        public ChinaMobileSmsQueryTrafficInfo createFromParcel(Parcel source)
        {
            //如果有boolean类型的数据，在这里就得传为数值型，然后再转回去
        	ChinaMobileSmsQueryTrafficInfo mChinaMobileSmsQueryTrafficInfo = new ChinaMobileSmsQueryTrafficInfo();
        	mChinaMobileSmsQueryTrafficInfo.usedTotalTraffic = source.readString();
        	mChinaMobileSmsQueryTrafficInfo.unusedTotalTraffic = source.readString();
        	mChinaMobileSmsQueryTrafficInfo.usedMonthTraffic = source.readString();
        	mChinaMobileSmsQueryTrafficInfo.unusedMonthTraffic = source.readString();
        	mChinaMobileSmsQueryTrafficInfo.usedMonthBeyondTraffic = source.readString();

        	mChinaMobileSmsQueryTrafficInfo.unusedMonthNativeCommonTraffic = source.readString();
        	mChinaMobileSmsQueryTrafficInfo.unusedMonthNativeIdleTraffic = source.readString();
            return mChinaMobileSmsQueryTrafficInfo;
        }
        public ChinaMobileSmsQueryTrafficInfo[] newArray(int size) {
            return new ChinaMobileSmsQueryTrafficInfo[size];
        }
    };


    public void loadSmsQueryTrafficFileForChinaMobile(XmlPullParser confparser, String trName)
	{
	    if("MonthNativeCommonTraffic".equalsIgnoreCase(trName))
        {
        	String trafficUsedCountTmp = confparser.getAttributeValue(null, "monthNativeCommonTrafficUsedCount");
        	String trafficUnusedCountTmp = confparser.getAttributeValue(null, "monthNativeCommonTrafficUnusedCount");

        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUsedCountTmp:" + trafficUsedCountTmp);
        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUnusedCountTmp:" + trafficUnusedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsedForChinaMobile++, j++)
        	{
        		mFilterTrafficUsedForChinaMobile[mTrafficQueryCountByUsedForChinaMobile] = 'c' + confparser.getAttributeValue(null, "monthNativeCommonTrafficUsed"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUsed:" + mFilterTrafficUsedForChinaMobile[mTrafficQueryCountByUsedForChinaMobile]+mTrafficQueryCountByUsedForChinaMobile);
        	}
        	for(int j = 0; j < Integer.parseInt(trafficUnusedCountTmp, 10); mTrafficQueryCountByUnusedForChinaMobile++, j++)
        	{
        		mFilterTrafficUnusedForChinaMobile[mTrafficQueryCountByUnusedForChinaMobile] ='c'+ confparser.getAttributeValue(null, "monthNativeCommonTrafficUnused"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUnused:" + mFilterTrafficUnusedForChinaMobile[mTrafficQueryCountByUnusedForChinaMobile]);
        	}

        }
        else if("MonthNativeIdleTraffic".equalsIgnoreCase(trName))
        {
        	String trafficUsedCountTmp = confparser.getAttributeValue(null, "monthNativeIdleTrafficUsedCount");
        	String trafficUnusedCountTmp = confparser.getAttributeValue(null, "monthNativeIdleTrafficUnusedCount");

        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUsedCountTmp:" + trafficUsedCountTmp);
        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUnusedCountTmp:" + trafficUnusedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsedForChinaMobile++, j++)
        	{
        		mFilterTrafficUsedForChinaMobile[mTrafficQueryCountByUsedForChinaMobile] = 'd' + confparser.getAttributeValue(null, "monthNativeIdleTrafficUsed"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUsed:" + mFilterTrafficUsedForChinaMobile[mTrafficQueryCountByUsedForChinaMobile]+mTrafficQueryCountByUsedForChinaMobile);
        	}
        	for(int j = 0; j < Integer.parseInt(trafficUnusedCountTmp, 10); mTrafficQueryCountByUnusedForChinaMobile++, j++)
        	{
        		mFilterTrafficUnusedForChinaMobile[mTrafficQueryCountByUnusedForChinaMobile] ='d'+ confparser.getAttributeValue(null, "monthNativeIdleTrafficUnused"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUnused:" + mFilterTrafficUnusedForChinaMobile[mTrafficQueryCountByUnusedForChinaMobile]);
        	}

        }
    }

	private String trafficQueryForChinaMobile(String stMsgs, char c,String stFilterTrafficUsed, int iFilterTrafficUnusedCount, String szFilterTrafficUnused[])
	{
		for(int i = 0; i < iFilterTrafficUnusedCount; i++)
		{
			String tmpFilterKey = (String) szFilterTrafficUnused[i].subSequence(0, 1);
			String regex = (stFilterTrafficUsed + szFilterTrafficUnused[i].substring(1));
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stMsgs);
			Log.d(LOG_TAG, regex+","+ tmpFilterKey+",,,,"+stMsgs);
			if(matcher.find())
			{
				switch(c)
				{
				    case 'c':
				    	switch(tmpFilterKey.charAt(0))
						{
						    case 'c':
						    	stMsgs = stMsgs.replace(matcher.group(0), "");
						    	unusedMonthNativeCommonTraffic = matcher.group(2).replace(",", "").trim();
						    	break;
						    default:
						}
				    	break;
				    case 'd':
				    	switch(tmpFilterKey.charAt(0))
						{
						    case 'd':
						    	stMsgs = stMsgs.replace(matcher.group(0), "");
						    	unusedMonthNativeIdleTraffic = matcher.group(2).replace(",", "").trim();
						    	break;
						    default:
						}
				    	break;
				    default:
				    	break;
				}
			}
		}
		return stMsgs;

	}

	public boolean isTrafficQueryForChinaMobile(String stMsgs, int  iFilterTrafficUsedCount, String szFilterTrafficUsed[], int  iFilterTrafficUnusedCount, String szFilterTrafficUnused[])
	{
    	boolean isTrafficQueryFlag = false;

    	String stMsgsTmp;
    	stMsgs = stMsgs.replace("，", "");

		boolean findMonthNativeCommonUsed = false;
		boolean findMonthNativeIdleUsed = false;

		for(int i = 0; i < iFilterTrafficUsedCount; i++)
		{
		    String tmpFilterKey = (String) szFilterTrafficUsed[i].subSequence(0, 1);
			String regex = szFilterTrafficUsed[i].substring(1).replace(",", "");
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stMsgs);
			Log.d(LOG_TAG, regex+","+ tmpFilterKey+",,,,"+stMsgs);
			if(matcher.find())
			{
				isTrafficQueryFlag = true;
				switch(tmpFilterKey.charAt(0))
				{
				    case 'c':
						if(findMonthNativeCommonUsed)
							break;
						findMonthNativeCommonUsed = true;
						usedMonthNativeCommonTraffic = matcher.group(1).replace(",", "").trim();
						stMsgsTmp = trafficQueryForChinaMobile(stMsgs, tmpFilterKey.charAt(0), regex, iFilterTrafficUnusedCount, szFilterTrafficUnused);

						if(!isNum(usedMonthNativeCommonTraffic))
						{
                            usedMonthNativeCommonTraffic = null;
						}

						if((stMsgsTmp.equals(stMsgs))&&(CheckExceptFirstSameStringInArray(iFilterTrafficUsedCount, 'c', regex, szFilterTrafficUsed)))
							usedMonthNativeCommonTraffic =null;

						if(stMsgsTmp.equals(stMsgs))
						{
							unusedMonthNativeCommonTraffic = "0.00";
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedMonthNativeCommonTraffic = "+ usedMonthNativeCommonTraffic + " , " + "unusedMonthNativeCommonTraffic = "+ unusedMonthNativeCommonTraffic);
						stMsgs = stMsgsTmp;
						break;
				    case 'd':
						if(findMonthNativeIdleUsed)
							break;
						findMonthNativeIdleUsed = true;
						usedMonthNativeIdleTraffic = matcher.group(1).replace(",", "").trim();
						stMsgsTmp = trafficQueryForChinaMobile(stMsgs, tmpFilterKey.charAt(0), regex, iFilterTrafficUnusedCount, szFilterTrafficUnused);

						if(!isNum(usedMonthNativeIdleTraffic))
						{
							usedMonthNativeIdleTraffic = null;
						}

						if((stMsgsTmp.equals(stMsgs))&&(CheckExceptFirstSameStringInArray(iFilterTrafficUsedCount, 'd', regex, szFilterTrafficUsed)))
							usedMonthNativeIdleTraffic =null;

						if(stMsgsTmp.equals(stMsgs))
						{
							unusedMonthNativeIdleTraffic = "0.00";
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedMonthNativeIdleTraffic = "+ usedMonthNativeIdleTraffic + " , "+ "unusedMonthNativeIdleTraffic = "+ unusedMonthNativeIdleTraffic);
				    	stMsgs = stMsgsTmp;
						break;
				    default:
				}
    		}
		}

		boolean findMonthNativeCommonUnused = false;
		boolean findMonthNativeIdleUnused = false;

		for(int i = 0; i < iFilterTrafficUnusedCount; i++)
		{
			String tmpFilterKey = (String) szFilterTrafficUnused[i].subSequence(0, 1);

			String regex = szFilterTrafficUnused[i].substring(1).replace(",", "");
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stMsgs);
			Log.d(LOG_TAG, regex+","+ tmpFilterKey+",,,,"+stMsgs);
			if(matcher.find())
			{
				isTrafficQueryFlag = true;
				switch(tmpFilterKey.charAt(0))
				{
				    case 'c':
						if(findMonthNativeCommonUnused)
							break;
						findMonthNativeCommonUnused= true;
				    	unusedMonthNativeCommonTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(unusedMonthNativeCommonTraffic))
						{
                            unusedMonthNativeCommonTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iFilterTrafficUnusedCount, 'c', regex, szFilterTrafficUnused))
						{
						    unusedMonthNativeCommonTraffic = null;
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:unusedMonthNativeCommonTraffic = "+ unusedMonthNativeCommonTraffic);
				    	break;
				    case 'd':
						if(findMonthNativeIdleUnused)
							break;
						findMonthNativeIdleUnused = true;
				    	unusedMonthNativeIdleTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(unusedMonthNativeIdleTraffic))
						{
                            unusedMonthNativeIdleTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iFilterTrafficUnusedCount, 'd', regex, szFilterTrafficUnused))
						{
						    unusedMonthNativeIdleTraffic = null;
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:unusedMonthNativeIdleTraffic = "+ unusedMonthNativeIdleTraffic);
				    	break;
				    default:
				}
			}
		}

		return isTrafficQueryFlag;
	}

    /*接收形式
     * Bundle bundle = intent.getExtras();
     * ChinaUnionSmsQueryTrafficInfo mChinaUnionSmsQueryTrafficInfo =
    	    (ChinaUnionSmsQueryTrafficInfo)bundle.getParcelable(SMS_QUERYTRAFFIC_KEY);
     *   其中：ChinaUnionSmsQueryTrafficInfo类应该放在 package com.android.mms.query包内，Android跨进程传递，保持一致
     */
	public void sendContexForSmsQueryTrafficForChinaMobile(Context clCxt,String SmSendSucessForQueryTrafficFlag, ChinaMobileSmsQueryTrafficInfo clChinaMobileSmsQueryTraffic)
	{
        Intent intent = new Intent(ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
        Bundle Bundle = new Bundle();
        Bundle.putParcelable(SMS_QUERYTRAFFIC_KEY, clChinaMobileSmsQueryTraffic);
        intent.putExtras(Bundle);
		intent.putExtra(SMS_QUERY_RESPONSE, SmSendSucessForQueryTrafficFlag);

        clCxt.sendBroadcast(intent);
		Log.d(LOG_TAG, "sendContexForSmsQueryTraffic :"+ "\n"
		              +"usedTotalTraffic = " + usedTotalTraffic + "\n"
		              +"unusedTotalTraffic = " + unusedTotalTraffic + "\n"
		              +"usedMonthTraffic = " + usedMonthTraffic + "\n"
		              +"unusedMonthTraffic = " + unusedMonthTraffic + "\n"

		              +"unusedMonthNativeCommonTraffic = " + unusedMonthNativeCommonTraffic + "\n"
		              +"unusedMonthNativeIdleTraffic = " + unusedMonthNativeIdleTraffic + "\n"
		              +"unusedMonthBeyondTraffic = " + usedMonthBeyondTraffic + "\n"

		              +"smSendSucessForQueryTrafiicFlag = " + SmSendSucessForQueryTrafficFlag + "\n"
		              +"ACTION_SENDCONTEX_SMSQUERYTRAFFIC = " + ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
	}

}
