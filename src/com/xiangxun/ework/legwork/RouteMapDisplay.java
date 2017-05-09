
package com.xiangxun.ework.legwork;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.xiangxun.ework.staticarray.ImageForOverlay;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.Toast;

public class RouteMapDisplay extends FragmentActivity implements OnGetRoutePlanResultListener {
	
	private BaiduMap mBaiduMap;
	private int clickMarkerNum = 1;
	private RouteLine route = null;
	private RoutePlanSearch mSearch = null; 
	private int[] address ;
	private InfoWindow mInfoWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.route_display_activity);
		mBaiduMap = ((SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map))).getBaiduMap();
		
		mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        address = new int[2];
		display();
		//覆盖物点击事件
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(final Marker marker) {
				int j = 0;
				if (clickMarkerNum == 1) {
					if (marker == ImageForOverlay.overlayMarker[0]) {
						j = showMarkerWindowMsg(marker, 0);
					}else if (marker == ImageForOverlay.overlayMarker[1]) {
						j = showMarkerWindowMsg(marker ,1);
					}else if (marker == ImageForOverlay.overlayMarker[2]) {
						j = showMarkerWindowMsg(marker, 2);
					}else if (marker == ImageForOverlay.overlayMarker[3]) {
						j = showMarkerWindowMsg(marker ,3);
					}else if (marker == ImageForOverlay.overlayMarker[4]) {
						j = showMarkerWindowMsg(marker ,4);
					}else if (marker == ImageForOverlay.overlayMarker[5]) {
						j = showMarkerWindowMsg(marker ,5);
					}else if (marker == ImageForOverlay.overlayMarker[6]) {
						j = showMarkerWindowMsg(marker ,6);
					}else if (marker == ImageForOverlay.overlayMarker[7]) {
						j = showMarkerWindowMsg(marker ,7);
					}else if (marker == ImageForOverlay.overlayMarker[8]) {
						j = showMarkerWindowMsg(marker ,8);
					}else if (marker == ImageForOverlay.overlayMarker[9]) {
						j = showMarkerWindowMsg(marker ,9);
					}
				}else if (clickMarkerNum == 2) {
					showMarkerwindow(marker, j);
				}
				
				return true;
			}
		});
		
	}
	void display(){
		for (int i = 0; i < RoutineDisplay.dataListFromSQLite.size(); i++) {
			LatLng llArray = new LatLng((Double)RoutineDisplay.dataListFromSQLite.get(i).get("latitude"),
									(Double)RoutineDisplay.dataListFromSQLite.get(i).get("longitude"));
			OverlayOptions ooBArray = new MarkerOptions()
					.position(llArray).icon(ImageForOverlay.mOverlayImage[i])
					.zIndex(5).draggable(true);
			ImageForOverlay.overlayMarker[i] = (Marker) (mBaiduMap.addOverlay(ooBArray));
		}
	}
	//点击地图上的marker显示对应的路程信息
	/**
	 * @param marker 地图覆盖物
	 * @param i 对应的覆盖物序号
	 */
	String timeStr = null;
	public int showMarkerWindowMsg(Marker marker, int i){
		
		String start = (String) RoutineDisplay.dataListFromSQLite.get(i).get("address");
		String end = null;
		if (i - 1 < 0) {
			i = 1;
			end = (String) RoutineDisplay.dataListFromSQLite.get(i-1).get("address");
		}else {
			end = (String) RoutineDisplay.dataListFromSQLite.get(i-1).get("address");
		}
		timeStr = (String)RoutineDisplay.dataListFromSQLite.get(i).get("startTimeActual");
		//重置浏览节点的路线数据
		route = null;
		//设置起终点信息，对于tranist search 来说，城市名无意义
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("厦门", start);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("厦门", end);
		
	    mSearch.transitSearch((new TransitRoutePlanOption())
	            .from(enNode)
	            .city("厦门")
	            .to(stNode));
	    clickMarkerNum = 2;
	    Toast.makeText(this, "再次点击获得数据", Toast.LENGTH_SHORT).show();
		return i;
	}
	
	public void showMarkerwindow(Marker marker, int i){
		/**test code for time and distance begin***/
	    int allTime = 0;
	    int distance = 0;
	    int allTimeSum = 0;
	    int distanceSum = 0;
	    int[] mData = new int[2];
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
	        mData = address;
		}
//		    Toast.makeText(getApplicationContext(), String.valueOf(address[0]), Toast.LENGTH_SHORT).show();
	    /**test code for time and distance end***/
	    Button button = new Button(getApplicationContext());
		final LatLng ll = marker.getPosition();
		OnInfoWindowClickListener listener = null;
		String TimeString = String.valueOf(mData[0]/60);
		String distanceKm = String.valueOf(mData[1]/1000);
		button.setTextColor(android.graphics.Color.BLACK);
		button.setText("公交耗时：" 
				+ TimeString + "分钟"
				+ "\n" 
				+ "停留时间：" 
				+ timeStr + "分钟"
				+ "\n"
				+ "两地距离："
				+ distanceKm + "公里");
		listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {
				
				mBaiduMap.hideInfoWindow();
			}
		};
		
		mInfoWindow = new InfoWindow(button, ll, listener);
		mBaiduMap.showInfoWindow(mInfoWindow);
		clickMarkerNum = 1;
	}
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetTransitRouteResult(TransitRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
