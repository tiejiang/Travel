/**
 * 初始化百度地图
 * 
 * */
package com.xiangxun.ework.legwork;

import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class ApplicationData extends Application {

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(this);
	}
	
}
