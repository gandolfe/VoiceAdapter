package com.zhonghong.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;

public class SettingsProviderStateHelper {
    private static final String TAG = "SettingsProviderStateHelper";
    private Context mContext;
    private StateListener mStateListener;
    private StateObserver mStateObserver;
    private final String mSystemKey;

    public interface StateListener {
        void onStateChanged(String str);
    }

    private class StateObserver extends ContentObserver {
        private final Uri STATE_URI;

        public StateObserver(Handler handler) {
            super(handler);
            this.STATE_URI = System.getUriFor(SettingsProviderStateHelper.this.mSystemKey);
        }

        @SuppressLint({"NewApi"})
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.i(SettingsProviderStateHelper.TAG, new StringBuilder(String.valueOf(SettingsProviderStateHelper.this.mSystemKey)).append(" onChanged").toString());
            if (this.STATE_URI.equals(uri) && SettingsProviderStateHelper.this.mStateListener != null) {
                SettingsProviderStateHelper.this.mStateListener.onStateChanged(SettingsProviderStateHelper.this.mSystemKey);
            }
        }

        public void startObserver() {
            SettingsProviderStateHelper.this.mContext.getContentResolver().registerContentObserver(this.STATE_URI, false, this);
        }

        public void stopObserver() {
            SettingsProviderStateHelper.this.mContext.getContentResolver().unregisterContentObserver(this);
        }
    }

    public SettingsProviderStateHelper(Context context, String key) {
        this.mContext = context;
        this.mSystemKey = key;
        if (TextUtils.isEmpty(key)) {
            throw new IllegalArgumentException("Empty key");
        }
    }

//    public void setIntKeyValue(int value) {
//        System.putInt(this.mContext.getContentResolver(), this.mSystemKey, value);
//    }
//
//    public void setFloatKeyValue(float value) {
//        System.putFloat(this.mContext.getContentResolver(), this.mSystemKey, value);
//    }

//    public void setLongKeyValue(long value) {
//        System.putLong(this.mContext.getContentResolver(), this.mSystemKey, value);
//    }

//    public void setStringKeyValue(String value) {
//        System.putString(this.mContext.getContentResolver(), this.mSystemKey, value);
//    }

    public int getIntKeyValue(int defaultValue) {
        return System.getInt(this.mContext.getContentResolver(), this.mSystemKey, defaultValue);
    }

//    public float getFloatValue(float defaultValue) {
//        return System.getFloat(this.mContext.getContentResolver(), this.mSystemKey, defaultValue);
//    }

//    public float getLongValue(Long defaultValue) {
//        return (float) System.getLong(this.mContext.getContentResolver(), this.mSystemKey, defaultValue.longValue());
//    }

//    public String getStringValue(String defaultValue) {
//        return System.getString(this.mContext.getContentResolver(), this.mSystemKey);
//    }

    public void startObserver() {
        if (this.mStateObserver == null) {
            this.mStateObserver = new StateObserver(new Handler(Looper.getMainLooper()));
        }
        this.mStateObserver.startObserver();
    }

    public void stopObserver() {
        if (this.mStateObserver != null) {
            this.mStateObserver.stopObserver();
        }
    }

    public void setStateListener(StateListener listener) {
        this.mStateListener = listener;
    }
}