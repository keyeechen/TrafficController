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
public class ChinaUnicomSmsQueryTrafficInfo extends CommonQueryTrafficInfo
{
	private static final String LOG_TAG = "ChinaUnionSmsQueryTrafficInfo";
    private final String mOperateChinaUniomQueryTrafficMaxCountUsed  = "100";
    private final String mOperateChinaUniomQueryTrafficMaxCountUunsed  = "100";

	public String usedMonthPackageTraffic;
	public String unusedMonthPackageTraffic;
	public String usedMonthOverlapTraffic;
	public String unusedMonthOverlapTraffic;

	public int mTrafficQueryCountByUsedForChinaUnicom = 0;
	public int mTrafficQueryCountByUnusedForChinaUnicom = 0;
	public String mFilterTrafficUsedForChinaUnicom[] = new String[Integer.parseInt(mOperateChinaUniomQueryTrafficMaxCountUsed, 10)];
	public String mFilterTrafficUnusedForChinaUnicom[] = new String[Integer.parseInt(mOperateChinaUniomQueryTrafficMaxCountUunsed, 10)];

	@Override
	public int describeContents()
	{
		return 0;
	}
	@Override
	public void writeToParcel(Parcel parcel, int flags)
	{
        super.writeToParcel(parcel, flags);

		parcel.writeString(usedMonthPackageTraffic);
		parcel.writeString(unusedMonthPackageTraffic);
		parcel.writeString(usedMonthOverlapTraffic);
		parcel.writeString(unusedMonthOverlapTraffic);

	}

	public static final Parcelable.Creator<ChinaUnicomSmsQueryTrafficInfo> CREATOR = new Creator<ChinaUnicomSmsQueryTrafficInfo>()
	{
        public ChinaUnicomSmsQueryTrafficInfo createFromParcel(Parcel source)
        {
            //如果有boolean类型的数据，在这里就得传为数值型，然后再转回去
        	ChinaUnicomSmsQueryTrafficInfo mChinaUnicomSmsQueryTrafficInfo = new ChinaUnicomSmsQueryTrafficInfo();
        	mChinaUnicomSmsQueryTrafficInfo.usedTotalTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.unusedTotalTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.usedMonthTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.unusedMonthTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.usedMonthBeyondTraffic = source.readString();

        	mChinaUnicomSmsQueryTrafficInfo.usedMonthPackageTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.unusedMonthPackageTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.usedMonthOverlapTraffic = source.readString();
        	mChinaUnicomSmsQueryTrafficInfo.unusedMonthOverlapTraffic = source.readString();
            return mChinaUnicomSmsQueryTrafficInfo;
        }
        public ChinaUnicomSmsQueryTrafficInfo[] newArray(int size)
        {
            return new ChinaUnicomSmsQueryTrafficInfo[size];
        }
    };

    public void loadSmsQueryTrafficFileForChinaUnicom(XmlPullParser confparser, String trName)
	{
	    if("PackageTraffic".equalsIgnoreCase(trName))
        {
        	String trafficUsedCountTmp = confparser.getAttributeValue(null, "packageTrafficUsedCount");
        	String trafficUnusedCountTmp = confparser.getAttributeValue(null, "packageTrafficUnusedCount");

        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUsedCountTmp:" + trafficUsedCountTmp);
        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUnusedCountTmp:" + trafficUnusedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsedForChinaUnicom++, j++)
        	{
        		mFilterTrafficUsedForChinaUnicom[mTrafficQueryCountByUsedForChinaUnicom] = 'c' + confparser.getAttributeValue(null, "packageTrafficUsed"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUsed:" + mFilterTrafficUsedForChinaUnicom[mTrafficQueryCountByUsedForChinaUnicom]+mTrafficQueryCountByUsedForChinaUnicom);
        	}
        	for(int j = 0; j < Integer.parseInt(trafficUnusedCountTmp, 10); mTrafficQueryCountByUnusedForChinaUnicom++, j++)
        	{
        		mFilterTrafficUnusedForChinaUnicom[mTrafficQueryCountByUnusedForChinaUnicom] ='c'+ confparser.getAttributeValue(null, "packageTrafficUnused"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUnused:" + mFilterTrafficUnusedForChinaUnicom[mTrafficQueryCountByUnusedForChinaUnicom]);
        	}

        }
        else if("OverlapTraffic".equalsIgnoreCase(trName))
        {
        	String trafficUsedCountTmp = confparser.getAttributeValue(null, "overlapTrafficUsedCount");
        	String trafficUnusedCountTmp = confparser.getAttributeValue(null, "overlapTrafficUnusedCount");

        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUsedCountTmp:" + trafficUsedCountTmp);
        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUnusedCountTmp:" + trafficUnusedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsedForChinaUnicom++, j++)
        	{
        		mFilterTrafficUsedForChinaUnicom[mTrafficQueryCountByUsedForChinaUnicom] = 'd' + confparser.getAttributeValue(null, "overlapTrafficUsed"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUsed:" + mFilterTrafficUsedForChinaUnicom[mTrafficQueryCountByUsedForChinaUnicom]+mTrafficQueryCountByUsedForChinaUnicom);
        	}
        	for(int j = 0; j < Integer.parseInt(trafficUnusedCountTmp, 10); mTrafficQueryCountByUnusedForChinaUnicom++, j++)
        	{
        		mFilterTrafficUnusedForChinaUnicom[mTrafficQueryCountByUnusedForChinaUnicom] ='d'+ confparser.getAttributeValue(null, "overlapTrafficUnused"+j);
        		Log.d(LOG_TAG, "SmsQueryTraffic mFilterTrafficUnused:" + mFilterTrafficUnusedForChinaUnicom[mTrafficQueryCountByUnusedForChinaUnicom]);
        	}

        }
    }

	private String trafficQueryForChinaUnicom(String stMsgs, char c,String stFilterTrafficUsed, int iFilterTrafficUnusedCount, String szFilterTrafficUnused[])
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
						    	unusedMonthPackageTraffic = matcher.group(2).replace(",", "").trim();
						    	break;
						    default:
						}
				    	break;
				    case 'd':
				    	switch(tmpFilterKey.charAt(0))
						{
						    case 'd':
						    	stMsgs = stMsgs.replace(matcher.group(0), "");
						    	unusedMonthOverlapTraffic = matcher.group(2).replace(",", "").trim();
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

    public boolean isTrafficQueryForChinaUnion(String stMsgs, int  iFilterTrafficUsedCount, String szFilterTrafficUsed[], int  iFilterTrafficUnusedCount, String szFilterTrafficUnused[])
	{
    	boolean isTrafficQueryFlag = false;

    	String stMsgsTmp;
    	stMsgs = stMsgs.replace("，", "");

		boolean findMonthPackageUsed =false;
		boolean findMonthOverlapUsed =false;

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
						if(findMonthPackageUsed)
							break;
						findMonthPackageUsed = true;
						usedMonthPackageTraffic = matcher.group(1).replace(",", "").trim();
						stMsgsTmp = trafficQueryForChinaUnicom(stMsgs, tmpFilterKey.charAt(0), regex, iFilterTrafficUnusedCount, szFilterTrafficUnused);

						if(!isNum(usedMonthPackageTraffic))
						{
                            usedMonthPackageTraffic = null;
						}

						if((stMsgsTmp.equals(stMsgs))&&(CheckExceptFirstSameStringInArray(iFilterTrafficUsedCount, 'c', regex, szFilterTrafficUsed)))
							usedMonthPackageTraffic =null;

						if(stMsgsTmp.equals(stMsgs))
						{
							unusedMonthPackageTraffic = "0.00";
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedMonthPackageTraffic = "+ usedMonthPackageTraffic + " , " + "unusedMonthPackageTraffic = "+ unusedMonthPackageTraffic);
						stMsgs = stMsgsTmp;
						break;
				    case 'd':
						if(findMonthOverlapUsed)
							break;
						findMonthOverlapUsed = true;
						usedMonthOverlapTraffic = matcher.group(1).replace(",", "").trim();
						stMsgsTmp = trafficQueryForChinaUnicom(stMsgs, tmpFilterKey.charAt(0), regex, iFilterTrafficUnusedCount, szFilterTrafficUnused);

						if(!isNum(usedMonthOverlapTraffic))
						{
                            usedMonthOverlapTraffic = null;
						}

						if((stMsgsTmp.equals(stMsgs))&&(CheckExceptFirstSameStringInArray(iFilterTrafficUsedCount, 'd', regex, szFilterTrafficUsed)))
							usedMonthOverlapTraffic =null;

						if(stMsgsTmp.equals(stMsgs))
						{
							unusedMonthOverlapTraffic = "0.00";
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedMonthOverlapTraffic = "+ usedMonthOverlapTraffic + " , "+ "unusedMonthOverlapTraffic = "+ unusedMonthOverlapTraffic);
				    	stMsgs = stMsgsTmp;
						break;
				    default:
				}
    		}
		}

		boolean findMonthPackageUnused =false;
		boolean findMonthOverlapUnused =false;

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
						if(findMonthPackageUnused)
							break;
						findMonthPackageUnused = true;

				    	unusedMonthPackageTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(unusedMonthPackageTraffic))
						{
                            unusedMonthPackageTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iFilterTrafficUnusedCount, 'c', regex, szFilterTrafficUnused))
						{
						    unusedMonthPackageTraffic = null;
						}

				    	Log.d(LOG_TAG, "filterSmsForTraffic:unusedMonthPackageTraffic = "+ unusedMonthPackageTraffic);
				    	break;
				    case 'd':
						if(findMonthOverlapUnused)
							break;
						findMonthOverlapUnused= true;

				    	unusedMonthOverlapTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(unusedMonthOverlapTraffic))
						{
                            unusedMonthOverlapTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iFilterTrafficUnusedCount, 'd', regex, szFilterTrafficUnused))
						{
						    unusedMonthOverlapTraffic = null;
						}

				    	Log.d(LOG_TAG, "filterSmsForTraffic:unusedMonthOverlapTraffic = "+ unusedMonthOverlapTraffic);
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
	public void sendContexForSmsQueryTrafficForChinaUnion(Context clCxt,String stSmSendSucessForQueryTrafficFlag, ChinaUnicomSmsQueryTrafficInfo clChinaUnionSmsQueryTraffic)
	{
        Intent intent = new Intent(ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
        Bundle Bundle = new Bundle();
        Bundle.putParcelable(SMS_QUERYTRAFFIC_KEY, this);
        intent.putExtras(Bundle);
		intent.putExtra(SMS_QUERY_RESPONSE, stSmSendSucessForQueryTrafficFlag);

        clCxt.sendBroadcast(intent);
		Log.d(LOG_TAG, "sendContexForSmsQueryTraffic :"+ "\n"
			           +"usedTotalTraffic = " + usedTotalTraffic + "\n"
			           +"unusedTotalTraffic = " + unusedTotalTraffic + "\n"
			           +"usedMonthTraffic = " + usedMonthTraffic + "\n"
			           +"unusedMonthTraffic = " + unusedMonthTraffic + "\n"
			           +"usedMonthPackageTraffic = " + usedMonthPackageTraffic + "\n"
			           +"unusedMonthPackageTraffic = " + unusedMonthPackageTraffic + "\n"
			           +"usedMonthOverlapTraffic = " + usedMonthOverlapTraffic + "\n"
			           +"unusedMonthOverlapTraffic = " + unusedMonthOverlapTraffic + "\n"
			           +"unusedMonthBeyondTraffic = " + usedMonthBeyondTraffic + "\n"
			           +"smSendSucessForQueryTrafiicFlag = " + stSmSendSucessForQueryTrafficFlag + "\n"
			           +"ACTION_SENDCONTEX_SMSQUERYTRAFFIC = " + ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
	}

}
