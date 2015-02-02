package com.heimi.traffic.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

public  class AppItem implements Comparable<AppItem>, Parcelable {
    public final int key;
    public boolean restricted;
    public SparseBooleanArray uids = new SparseBooleanArray();
    public long total;

    public AppItem(int key) {
        this.key = key;
    }

    public AppItem(Parcel parcel) {
        key = parcel.readInt();
        uids = parcel.readSparseBooleanArray();
        total = parcel.readLong();
    }

    public void addUid(int uid) {
        uids.put(uid, true);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeSparseBooleanArray(uids);
        dest.writeLong(total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int compareTo(AppItem another) {
    	if(another.total==total)
    	{
    		return 0;
    	}
    	else if(another.total<total)
    	{
    		return -1;
    	}
    	else 
    		return 1;
//       return Long.compare(another.total, total);
    }

    public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
        @Override
        public AppItem createFromParcel(Parcel in) {
            return new AppItem(in);
        }

        @Override
        public AppItem[] newArray(int size) {
            return new AppItem[size];
        }
    };
}
