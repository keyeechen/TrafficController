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
public class CommonQueryTrafficInfo implements Parcelable
{
    private static final String LOG_TAG = "OperateCommonQueryTrafficInfo";
    private final String mOperateCommonQueryTrafficMaxCountUsed  = "100";
    private final String mOperateCommonQueryTrafficMaxCountUunsed  = "100";

	public  final static String ACTION_SENDCONTEX_SMSQUERYTRAFFIC =  "android.provider.sms.SENDCONTEX_SMSQUERYTRAFFIC";
	public  final static String SMS_QUERYTRAFFIC_KEY = "com.android.mms.query.objecttran.key";
	public  final static String SMS_QUERY_RESPONSE = "SmsSendSuccessForQueryTrafficFlag";

    public boolean mTrafficQueryFlag = false;


	public String usedTotalTraffic;
	public String unusedTotalTraffic;
	public String usedMonthTraffic;
	public String unusedMonthTraffic;
	public String usedMonthBeyondTraffic;
    
	public String mOperators;
	/*select config is right or not*/
	public String mMcc;
	/*select config is right or not*/
	public String mMnc;
	public String mOperatorsServerNumber;
	public String mQueryMessageContent;
	public String mSmservicEnable;


	public int mTrafficQueryCountByUsed = 0;
	public int mTrafficQueryCountByUnused = 0;
	public String mFilterTrafficUsed[] = new String[Integer.parseInt(mOperateCommonQueryTrafficMaxCountUsed, 10)];
	public String mFilterTrafficUnused[] = new String[Integer.parseInt(mOperateCommonQueryTrafficMaxCountUunsed, 10)];

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(usedTotalTraffic);
		parcel.writeString(unusedTotalTraffic);
		parcel.writeString(usedMonthTraffic);
		parcel.writeString(unusedMonthTraffic);
		parcel.writeString(usedMonthBeyondTraffic);
	}

	public void loadSmsQueryTrafficFileCommon(XmlPullParser confparser, String trName)
	{
	    if("TotalTraffic".equalsIgnoreCase(trName))
        {
        	confparser.getAttributeCount();
        	String trafficUsedCountTmp = confparser.getAttributeValue(null, "totalTrafficUsedCount");
        	String trafficUnusedCountTmp = confparser.getAttributeValue(null, "totalTrafficUnusedCount");

        	Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon trafficUsedCountTmp:" + trafficUsedCountTmp);
        	Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon trafficUnusedCountTmp:" + trafficUnusedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsed++, j++)
        	{
        		mFilterTrafficUsed[mTrafficQueryCountByUsed] = 'a' + confparser.getAttributeValue(null, "totalTrafficUsed"+j);
        		Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon mFilterTrafficUsed:" + mFilterTrafficUsed[mTrafficQueryCountByUsed]+mTrafficQueryCountByUsed);
        	}
        	for(int j = 0; j < Integer.parseInt(trafficUnusedCountTmp, 10); mTrafficQueryCountByUnused++, j++)
        	{
        		mFilterTrafficUnused[mTrafficQueryCountByUnused] ='a'+ confparser.getAttributeValue(null, "totalTrafficUnused"+j);
        		Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon mFilterTrafficUnused:" + mFilterTrafficUnused[mTrafficQueryCountByUnused]);
        	}
        }
        else if("MonthTraffic".equalsIgnoreCase(trName))
        {
            String trafficUsedCountTmp = confparser.getAttributeValue(null, "monthTrafficUsedCount");
        	String trafficUnusedCountTmp = confparser.getAttributeValue(null, "monthTrafficUnusedCount");
        	Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon trafficUsedCountTmp:" + trafficUsedCountTmp);
        	Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon trafficUnusedCountTmp:" + trafficUnusedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsed ++, j++)
        	{
        		mFilterTrafficUsed[mTrafficQueryCountByUsed] = 'b' + confparser.getAttributeValue(null, "monthTrafficUsed"+j);
        		Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon mFilterTrafficUsed:" + mFilterTrafficUsed[mTrafficQueryCountByUsed]+mTrafficQueryCountByUsed);
        	}
        	for(int j = 0; j < Integer.parseInt(trafficUnusedCountTmp, 10); mTrafficQueryCountByUnused++, j++)
        	{
        		mFilterTrafficUnused[mTrafficQueryCountByUnused] ='b'+ confparser.getAttributeValue(null, "monthTrafficUnused"+j);
        		Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon mFilterTrafficUnused:" + mFilterTrafficUnused[mTrafficQueryCountByUnused]);
        	}
        }
		else if("BeyondTraffic".equalsIgnoreCase(trName))
        {
        	String trafficUsedCountTmp = confparser.getAttributeValue(null, "beyondTrafficUsedCount");

        	Log.d(LOG_TAG, "SmsQueryTraffic trafficUsedCountTmp:" + trafficUsedCountTmp);

        	for(int j = 0; j < Integer.parseInt(trafficUsedCountTmp, 10); mTrafficQueryCountByUsed ++, j++)
        	{
        		mFilterTrafficUsed[mTrafficQueryCountByUsed] = 'e' + confparser.getAttributeValue(null, "beyondTrafficUsed"+j);
        		Log.d(LOG_TAG, "loadSmsQueryTrafficFileCommon mFilterTrafficUsed:" + mFilterTrafficUsed[mTrafficQueryCountByUsed]+mTrafficQueryCountByUsed);
        	}
        }

		return ;
	}

	private String trafficQueryForCommon(String stMsgs, char c,String stFilterTrafficUsed, int iFilterTrafficUnusedCount, String szFilterTrafficUnused[])
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
					case 'a':
						switch(tmpFilterKey.charAt(0))
						{
							case 'a':
								stMsgs = stMsgs.replace(matcher.group(0), "");
								unusedTotalTraffic = matcher.group(2).replace(",", "").trim();
								break;
							default:
						}
						break;
					case 'b':
						switch(tmpFilterKey.charAt(0))
						{
							case 'b':
								stMsgs = stMsgs.replace(matcher.group(0), "");
								unusedMonthTraffic = matcher.group(2).replace(",", "").trim();
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

	public String isTrafficQueryForCommon(String stMsgs,
		int  iFilterTrafficUsedCount,
		String szFilterTrafficUsed[],
		int  iFilterTrafficUnusedCount,
		String szFilterTrafficUnused[],
		int  iCheckUsedCount,
		String szCheckUsed[],
		int  iCheckUnusedCount,
		String szCheckUnused[])
	{
		String stMsgsTmp;
		stMsgs = stMsgs.replace("，", "").replace(",", "");

		boolean findTotalUsed = false;
		boolean findMonthUsed = false;
		boolean findBeyondUsed = false;

		for(int i = 0; i < iFilterTrafficUsedCount; i++)
		{
			String tmpFilterKey = (String) szFilterTrafficUsed[i].subSequence(0, 1);
			String regex = szFilterTrafficUsed[i].substring(1).replace(",", "");
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stMsgs);
			Log.d(LOG_TAG, regex+","+ tmpFilterKey+",,,,"+stMsgs);
			if(matcher.find())
			{
				mTrafficQueryFlag = true;
				switch(tmpFilterKey.charAt(0))
				{
					case 'a':
						if(findTotalUsed)
							break;
						findTotalUsed =true;
						usedTotalTraffic = matcher.group(1).replace(",", "").trim();
						stMsgsTmp = trafficQueryForCommon(stMsgs, tmpFilterKey.charAt(0), regex, iFilterTrafficUnusedCount, szFilterTrafficUnused);

						if(!isNum(usedTotalTraffic))
						{
                            usedTotalTraffic = null;
						}

						if((stMsgsTmp.equals(stMsgs))&&(CheckExceptFirstSameStringInArray(iCheckUsedCount, 'a', regex, szCheckUsed)))
							usedTotalTraffic =null;

						if(stMsgsTmp.equals(stMsgs))
						{
							unusedTotalTraffic = "0.00";
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedTotalTraffic = "+ usedTotalTraffic + " , " + "unusedTotalTraffic = "+ unusedTotalTraffic);
						stMsgs = stMsgsTmp;
						break;
					case 'b':
						if(findMonthUsed)
							break;
						findMonthUsed = true;
						usedMonthTraffic = matcher.group(1).replace(",", "").trim();
						stMsgsTmp = trafficQueryForCommon(stMsgs, tmpFilterKey.charAt(0), regex, iFilterTrafficUnusedCount, szFilterTrafficUnused);

						if(!isNum(usedMonthTraffic))
						{
                            usedMonthTraffic = null;
						}

						if((stMsgsTmp.equals(stMsgs))&&(CheckExceptFirstSameStringInArray(iCheckUsedCount, 'b', regex, szCheckUsed)))
							usedMonthTraffic =null;

						if(stMsgsTmp.equals(stMsgs))
						{
							unusedMonthTraffic = "0.00";
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedMonthTraffic = "+ usedMonthTraffic + " , " + "unusedMonthTraffic = "+ unusedMonthTraffic);
						stMsgs = stMsgsTmp;
						break;
					case 'e':
						if(findBeyondUsed)
							break;
						findBeyondUsed = true;
						usedMonthBeyondTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(usedMonthBeyondTraffic))
						{
                            usedMonthBeyondTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iCheckUsedCount, 'e', regex, szCheckUsed))
							usedMonthBeyondTraffic = null;

						Log.d(LOG_TAG, "filterSmsForTraffic:usedMonthBeyondTraffic = "+ usedMonthBeyondTraffic);
						break;
					default:
				}
			}
		}

		boolean findTotalUnused = false;
		boolean findMonthUnused = false;

		for(int i = 0; i < iFilterTrafficUnusedCount; i++)
		{
			String tmpFilterKey = (String) szFilterTrafficUnused[i].subSequence(0, 1);

			String regex = szFilterTrafficUnused[i].substring(1).replace(",", "");
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stMsgs);
			Log.d(LOG_TAG, regex+","+ tmpFilterKey+",,,,"+stMsgs);
			if(matcher.find())
			{
				mTrafficQueryFlag = true;
				switch(tmpFilterKey.charAt(0))
				{
					case 'a':
						if(findTotalUnused)
							break;
						findTotalUnused = true;

                        unusedTotalTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(unusedTotalTraffic))
						{
                            unusedTotalTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iCheckUnusedCount, 'a', regex, szCheckUnused))
						{
						    unusedTotalTraffic = null;
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:usedTotalTraffic = "+ unusedTotalTraffic);
						break;
					case 'b':
						if(findMonthUnused)
							break;
						findMonthUnused = true;

						unusedMonthTraffic = matcher.group(1).replace(",", "").trim();

						if(!isNum(unusedMonthTraffic))
						{
                            unusedMonthTraffic = null;
						}

						if(CheckExceptFirstSameStringInArray(iCheckUnusedCount, 'b', regex, szCheckUnused))
						{
						    unusedMonthTraffic = null;
						}

						Log.d(LOG_TAG, "filterSmsForTraffic:unusedMonthTraffic = "+ unusedMonthTraffic);
						break;
					default:
				}
			}
		}

		return stMsgs;

	}

	public static boolean isNum(String str)
	{
	    if(str == null)
	    {
			return true;
	    }

		return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
	}

    public void sendContexForSmsQueryTrafficForCommon(Context clCxt,String stSmSendSucessForQueryTrafficFlag, CommonQueryTrafficInfo clChinaOperateCommonQueryTrafficInfo)
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
    				   +"unusedMonthBeyondTraffic = " + usedMonthBeyondTraffic + "\n"
    				   +"smSendSucessForQueryTrafiicFlag = " + stSmSendSucessForQueryTrafficFlag + "\n"
    				   +"ACTION_SENDCONTEX_SMSQUERYTRAFFIC = " + ACTION_SENDCONTEX_SMSQUERYTRAFFIC);
    }

	public boolean CheckExceptFirstSameStringInArray(int Size, char ch, String key, String array[])
	{
		for(int i=0; i< Size; i++)
		{
			String tmpFilterKey = (String) array[i].subSequence(0, 1);
		    if((ch==tmpFilterKey.charAt(0)))
		    {
		    	continue;
		    }
		    if(ch!=(tmpFilterKey.charAt(0))&&((array[i].substring(1).equals(key))))
		    {
		    	return true;
		    }
		}
		return false;
	}

}
