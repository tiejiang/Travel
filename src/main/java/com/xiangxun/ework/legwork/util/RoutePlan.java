package com.xiangxun.ework.legwork.util;

import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.xiangxun.ework.legwork.DrawerActivity;

public class RoutePlan implements OnGetRoutePlanResultListener {

    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    private int[] address ;

//    public RoutePlan() {
//    	address = new int[2];
//        mSearch = RoutePlanSearch.newInstance();
//    }

    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */
    public int[] SearchButtonProcess(String start, String end) {
        //重置浏览节点的路线数据
        address = new int[2];
        mSearch = RoutePlanSearch.newInstance();
        route = null;
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("厦门", start);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("厦门", end);
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);

        // 实际使用中请对起点终点城市进行正确的设定
        mSearch.transitSearch((new TransitRoutePlanOption())
                .from(stNode)
                .city("厦门")
                .to(enNode));

//        /**test code for time and distance begin***/
//        int allTime = 0;
//        int distance = 0;
//        int allTimeSum = 0;
//        int distanceSum = 0;
//        if (route != null) {
//        	for (int j = 0; j < route.getAllStep().size(); j++) {
//            	TransitRouteLine.TransitStep allStep = (TransitStep) route.getAllStep().get(j);
//    			allTime = allStep.getDuration();
//    			allTimeSum += allTime;
//    			distance = allStep.getDistance();
//    			distanceSum += distance;
//    		}
//            address[0] = allTimeSum;
//            address[1] = distanceSum;
//		}
//        /**test code for time and distance end***/
//        }
        return address;
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {
        // TODO Auto-generated method stub
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
        }else {
            return;
        }
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult arg0) {
        // TODO Auto-generated method stub

    }


}