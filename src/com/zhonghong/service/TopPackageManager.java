package com.zhonghong.service;

import java.util.Stack;

import android.content.Context;
import android.content.Intent;

import com.zhonghong.car.service.TopPackageChangedListener;
import com.zhonghong.utils.SlogUtil;

public class TopPackageManager implements TopPackageChangedListener{

	private SlogUtil slogUtil = new SlogUtil(getClass());
	private Stack<String> appPkgs;
	private Context mContext;
	
	public TopPackageManager(Context context){
		mContext = context;
		appPkgs = new Stack<>();
	}
	
	private void goHome(){
		slogUtil.println();
		appPkgs.clear();
		Intent i= new Intent(Intent.ACTION_MAIN); 
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP); 
        i.addCategory(Intent.CATEGORY_HOME); 
        mContext.startActivity(i);
	}
	
	@Override
	public boolean activityResuming(String pkg) {
		slogUtil.i("pkg=" + pkg);
			String prePkg = "";
			String nxtPkg = "";
			if(!appPkgs.empty()){
				prePkg = appPkgs.pop();
			}
			if(!appPkgs.empty()){
				nxtPkg = appPkgs.peek();
			}
			slogUtil.i("prePkg=" + prePkg + ",nxtPkg=" + nxtPkg);
			if(pkg.equals(prePkg)){
				appPkgs.push(pkg);
				return true;
			}
			if(!pkg.equals(nxtPkg) && nxtPkg.equals("com.zhonghong.carav")){
				goHome();
			}
		return true;
	}
	
	@Override
	public boolean activityStarting(Intent arg0, String pkg) {
		slogUtil.i("pkg=" + pkg);
		if(pkg.equals("com.zhonghong.carav")){
			appPkgs.clear();
		}
		if(appPkgs.contains(pkg)){
			appPkgs.remove(pkg);
		}
		appPkgs.push(pkg);
		return true;
	}
	
	@Override
	public boolean appCrashed(String processName, int pid, String shortMsg, String longMsg,
            long timeMillis, String stackTrace){
		slogUtil.i("processName=" + processName + ", pid=" + pid + ", shortMsg=" + shortMsg + ", longMsg=" + longMsg
				+ ", timeMillis=" + timeMillis);
		return true;
	}
	
	@Override
	public int appEarlyNotResponding(String processName, int pid, String annotation){
		slogUtil.i("processName=" + processName + ", pid=" + pid + ", annotation=" + annotation);
		return 0;
	}
	
	@Override
	public int appNotResponding(String processName, int pid, String processStats){
		slogUtil.i("processName=" + processName + ", pid=" + pid + ", processStats=" + processStats);
		return 0;
	}
	
	@Override
	public int systemNotResponding(String pkg){
		slogUtil.i("pkg=" + pkg);
		return 0;
	}
}
