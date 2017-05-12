package com.xiangxun.ework.legwork.util;


import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;

public class TransitRoutePlan implements OnGetRoutePlanResultListener {
	
	RouteLine route = null;
	boolean useDefaultIcon = false;
	private int[] address ;

	//搜索相关
	RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

	public TransitRoutePlan() {
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);
		address = new int[2];
	}

	/**
	* 发起路线规划搜索示例
	*
	* @param v
	*/
	public int[] SearchButtonProcess(String start, String end) {
		//重置浏览节点的路线数据
		route = null;
		//设置起终点信息，对于tranist search 来说，城市名无意义
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("厦门", start);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("厦门", end);
		
	    mSearch.transitSearch((new TransitRoutePlanOption())
	            .from(stNode)
	            .city("厦门")
	            .to(enNode));
	    return null;
	}

	/**
	* 节点浏览示例
	*
	* @param v
	*/
	public void nodeClick() {
		
	    /**test code for time and distance begin***/
	    int allTime = 0;
	    int distance = 0;
	    int allTimeSum = 0;
	    int distanceSum = 0;
	    if (route != null) {
	    	for (int j = 0; j < route.getAllStep().size(); j++) {
	        	TransitRouteLine.TransitStep allStep = (TransitStep) route.getAllStep().get(j);
				allTime = allStep.getDuration();			
				allTimeSum += allTime;
				distance = allStep.getDistance();
				distanceSum += distance;
			}
	        address[0] = allTimeSum;
	        address[1] = distanceSum;
		}
//		    Toast.makeText(getApplicationContext(), String.valueOf(address[0]), Toast.LENGTH_SHORT).show();
	    /**test code for time and distance end***/
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
	
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
//		    Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
		    //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
		    //result.getSuggestAddrInfo()
		    return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			Log.i("回调函数执行~~~~~~~!!!!!", "execute ");
		    route = result.getRouteLines().get(0);
		}
	}

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		
	}
}
