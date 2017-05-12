package com.xiangxun.ework.legwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.transition.Visibility;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.navisdk.comapi.mapcontrol.MapParams.Const;
import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.xiangxun.ework.legwork.adapter.Drawer_Adapter;
import com.xiangxun.ework.legwork.constant.Constant;
import com.xiangxun.ework.legwork.model.GetSet;
import com.xiangxun.ework.legwork.provider.SaveData;
import com.xiangxun.ework.legwork.util.RoutePlan;
import com.xiangxun.ework.legwork.util.TransitRoutePlan;
import com.xiangxun.ework.staticarray.ImageForOverlay;

@SuppressLint("NewApi")
@SuppressWarnings("deprecation")
public class DrawerActivity extends Activity implements
		OnGetRoutePlanResultListener,
		OnClickListener,
		OnGetGeoCoderResultListener{
	private SlidingDrawer mDrawer;
	private ImageButton imbg;
	private List<GetSet> list;
	ListView listview;
	//路线规划相关
	private Button mBtnPre = null;//上一个节点
	private Button mBtnNext = null;//下一个节点
	private int nodeIndex = -1;//节点索引,供浏览节点时使用
	/**
	 *路线数据结构的基类,表示一条路线，路线可能包括：路线规划中的换乘/驾车/步行路线
	 *此类为路线数据结构的基类，一般关注其子类对象即可，无需直接生成该类对象
	 * */
	private RouteLine route = null;
	private OverlayManager routeOverlay = null;
	private boolean useDefaultIcon = false;
	private TextView popupText = null;//泡泡view
	//搜索相关 （路径规划搜索接口）
	private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用

	//指定商户的地理参数
	private static final double LATITUDE = 24.603258f;
	private static final double LONGITUDE = 118.085267f;
	//用户所在位置经纬度
	public static double latitude = 0;
	public static double longitude = 0;
	// 定位相关
	LocationClient mLocClient;
	public MyLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// UI相关
	boolean isFirstLoc = true;// 是否首次定位
	private String startStr = "厦门北站";
	private String endStr = "鼓浪屿";

	private EditText editSt;
	private EditText editEn;
	private ImageButton sliding;
	private static String addressName = "未知地址";
	/*************覆盖物相关***************/
	private Button btnfinish;
	//	private Button btnAdd;
	private Marker mMarkerAAA;

	// 初始化全局 bitmap 信息，不用时及时 recycle
	BitmapDescriptor bdAAA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_gcoding);
	BitmapDescriptor bdA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	BitmapDescriptor bdB = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markb);
	BitmapDescriptor bdC = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markc);
	BitmapDescriptor bdD = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markd);
	/*************覆盖物相关***************/

	/*********************地址搜索***********************************/

	GeoCoder mGeoSearch = null; // 搜索模块，也可去掉地图模块独立使用


	/*********************地址搜索***********************************/

	private static final String LTAG = RoutineDisplay.class.getSimpleName();

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				//待修改为 text显示
				Log.e(LTAG, "key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Log.e(LTAG, "网络出错");
			}
		}
	}

	private SDKReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i("oncreate main", "oncreate!");
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.drawer_main);
		mCurrentMode = LocationMode.NORMAL;

		mBtnPre = (Button) findViewById(R.id.pre);
		mBtnNext = (Button) findViewById(R.id.next);
		//初始设置两个button为不可见
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);

		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		// 地图初始化
		mMapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mMapView.getMap();
		//初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(this);

		/*************覆盖物相关***************/
//        btnAdd = (Button)findViewById(R.id.btnAdd);
		btnfinish = (Button)findViewById(R.id.btnfinish);
		sliding = (ImageButton)findViewById(R.id.sliding);

//        btnAdd.setOnClickListener(this);
		btnfinish.setOnClickListener(this);
		sliding.setOnClickListener(this);
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		mBaiduMap.setMapStatus(msu);
		initOverlay();
		/*************覆盖物相关***************/

		/*************地址搜索***************/
		// 初始化搜索模块，注册事件监听
		mGeoSearch = GeoCoder.newInstance();
		mGeoSearch.setOnGetGeoCodeResultListener(this);

		/*************地址搜索***************/
		//开启定位图层
		mBaiduMap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		imbg=(ImageButton)findViewById(R.id.handle);
		mDrawer=(SlidingDrawer)findViewById(R.id.slidingdrawer);
		list=new ArrayList<GetSet>();

		/***********任意两点的路径规划搜索相关*************/
		// 处理搜索按钮响应
		editSt = (EditText) findViewById(R.id.start);
		editEn = (EditText) findViewById(R.id.end);

		/***********任意两点的路径规划搜索相关*************/

		//抽屉的处理
		addSlidingElement();
	}
	//抽屉的处理
	private void addSlidingElement(){
		//临时赋值，到时用动态的
		GetSet a=new GetSet();
		GetSet b=new GetSet();
		GetSet c=new GetSet();
		GetSet d=new GetSet();
		a.setName("行程展示");
		b.setName("行程规划");
		c.setName("附近");
		d.setName("加入");
		list.add(a);
		list.add(b);
		list.add(c);
		list.add(d);
		mDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener()
		{
			public void onDrawerOpened() {

			}
		});

		mDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener(){

			public void onDrawerClosed() {

			}

		});

		mDrawer.setOnDrawerScrollListener(new SlidingDrawer.OnDrawerScrollListener(){

			ListView listv=(ListView) findViewById(R.id.listv);

			@Override
			public void onScrollEnded() {

				listv.setAdapter(new Drawer_Adapter(DrawerActivity.this,list));

			}

			@Override
			public void onScrollStarted() {
				// TODO Auto-generated method stub

			}

		});
	}

	//覆盖物相关初始化
	public void initOverlay(){
		// add marker overlay
		LatLng llA = new LatLng(24.603258, 118.085267);
		OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdAAA)
				.zIndex(9).draggable(true);
		mMarkerAAA = (Marker) (mBaiduMap.addOverlay(ooA));
		mBaiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			public void onMarkerDrag(Marker marker) {
			}
			public void onMarkerDragEnd(Marker marker) {
				LayoutInflater layoutInflater = LayoutInflater.from(DrawerActivity.this);
				final View messageView = layoutInflater.inflate(R.layout.message_show, null);
				showMessageView(messageView);
				Toast.makeText(
						DrawerActivity.this,
						"当前位置坐标：" + marker.getPosition().latitude + ", "
								+ marker.getPosition().longitude,
						Toast.LENGTH_LONG).show();
			}
			public void onMarkerDragStart(Marker marker) {

			}
		});
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				// TODO Auto-generated method stub
				addressName = arg0.getName();
				Toast.makeText(getApplicationContext(), addressName, Toast.LENGTH_SHORT).show();
				// 给所选定的点添加覆盖物
				LatLng llB = new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
				OverlayOptions ooB = new MarkerOptions()
						.position(llB).icon(bdAAA)
						.zIndex(5).draggable(true);
				mMarkerAAA = (Marker) (mBaiduMap.addOverlay(ooB));

				//实例化弹出框的view并显示
				LayoutInflater layoutInflater = LayoutInflater.from(DrawerActivity.this);
				final View messageView = layoutInflater.inflate(R.layout.message_show, null);
				showMessageView(messageView);
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				// 给所选定的点添加覆盖物

			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {

			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "地图长按事件测试", Toast.LENGTH_LONG).show();
				LayoutInflater layoutInflater = LayoutInflater.from(DrawerActivity.this);
				final View messageView = layoutInflater.inflate(R.layout.map_long_click, null);

				AlertDialog builder = new AlertDialog.Builder(DrawerActivity.this)
						.setTitle("请输入地址：")
						.setView(messageView)
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								EditText city = (EditText)messageView.findViewById(R.id.city);
								EditText address = (EditText)messageView.findViewById(R.id.geocodekey);
								SearchButtonProcess(city, address);
							}
						})
						.setNegativeButton("取消", null)
						.create();
				builder.show();
			}
		});
	}

	/**
	 * 发起搜索
	 *
	 * @param v
	 */
	public void SearchButtonProcess(EditText editCity, EditText editText) {
		// Geo搜索
		mGeoSearch.geocode(new GeoCodeOption().city(
				editCity.getText().toString()).address(
				editText.getText().toString()));
	}
	//“添加节点”、“完成添加”按钮的点击事件
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnfinish:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("提醒")
						.setMessage("清理地图痕迹？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								mBaiduMap.clear();
							}})
						.setNegativeButton("取消", null);
				builder.setCancelable(true);
				builder.create()
						.show();
				break;
			case R.id.sliding:
				if (mDrawer.isShown()) {
					mDrawer.animateOpen();
				}
				else {
					mDrawer.animateClose();
				}
				break;
		}
	}
	// 点击/拖拽结束的弹出对话框
	private void showMessageView(final View view){
		AlertDialog builder = new AlertDialog.Builder(DrawerActivity.this)
				.setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText planTime = (EditText)view.findViewById(R.id.username);
						EditText actualTime = (EditText)view.findViewById(R.id.name);

						String planTimeStr = planTime.getText().toString().trim();
						String actualTimeStr = actualTime.getText().toString().trim();

						double mLatitude = mMarkerAAA.getPosition().latitude;
						double mLongitude = mMarkerAAA.getPosition().longitude;

						String latitude = String.valueOf(mLatitude);
						String longitude = String.valueOf(mLongitude);
						if (Validate(planTime, actualTime)) {
							//存入数据库
							new SaveData().saveTravelPlanData(getApplicationContext(),
									planTimeStr, actualTimeStr,
									latitude, longitude,
									addressName);
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 删除地图上生成的当前图标
						mMarkerAAA.remove();
					}
				})
				.create();
		builder.show();
	}
	// 验证方法  输入是否为空
	private boolean Validate(EditText userEditText, EditText pwdEditText){
		String username = userEditText.getText().toString().trim();
		if(username.equals("")){
			Toast.makeText(getApplicationContext(), "计划到达是必填项！此地点未保存！", Toast.LENGTH_LONG).show();
			return false;
		}
		String pwd = pwdEditText.getText().toString().trim();
		if(pwd.equals("")){
			Toast.makeText(getApplicationContext(), "计划停留时间是必填项!此地点未保存！", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			// map view 销毁后不再处理新接收的位置
			if (arg0 == null || mMapView == null){
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(arg0.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100)
					.latitude(arg0.getLatitude())
					.longitude(arg0.getLongitude())
					.build();
			mBaiduMap.setMyLocationData(locData);

			latitude = arg0.getLatitude();
			longitude = arg0.getLongitude();
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(LATITUDE, LONGITUDE);
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);//设置地图新中心点
				mBaiduMap.animateMapStatus(u);//以动画方式更新地图状态，动画耗时 300 ms
			}
		}
	}
	/**
	 * 发起路线规划搜索示例
	 *
	 * @param v
	 */
	public void SearchButtonProcess(View v) {

		//重置浏览节点的路线数据
		route = null;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		mBaiduMap.clear();//清空地图所有的 Overlay 覆盖物以及 InfoWindow
		/**
		 * PlanNode
		 * 路径规划中的出行节点信息,出行节点包括：起点，终点，途经点.
		 * 出行节点信息可以通过两种方式确定： 1： 给定出行节点经纬度坐标 2： 给定出行节点地名和城市名
		 *
		 * 设置起终点信息，对于tranist search 来说，城市名无意义
		 * */
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("厦门", editSt.getText().toString());
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("厦门", editEn.getText().toString());

		// 实际使用中请对起点终点城市进行正确的设定 (驾车、公交、步行)
		if (v.getId() == R.id.drive) {
			mSearch.drivingSearch((new DrivingRoutePlanOption())
					.from(stNode)
					.to(enNode));
		}else if (v.getId() == R.id.transit) {
			Toast.makeText(this, "公交路线计算", Toast.LENGTH_SHORT).show();
			mSearch.transitSearch((new TransitRoutePlanOption())
					.from(stNode)
					.city("厦门")
					.to(enNode));
		} else if (v.getId() == R.id.walk) {
			mSearch.walkingSearch((new WalkingRoutePlanOption())
					.from(stNode)
					.to(enNode));

		}
	}
	/**
	 * 节点浏览示例
	 *
	 * @param v
	 */
	public void nodeClick(View v) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1 && v.getId() == R.id.pre) {
			return;
		}
		//设置节点索引
		if (v.getId() == R.id.next) {
			if (nodeIndex < route.getAllStep().size() - 1) {
				nodeIndex++;
			} else {
				return;
			}
		} else if (v.getId() == R.id.pre) {
			if (nodeIndex > 0) {
				nodeIndex--;
			} else {
				return;
			}
		}
		//获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof WalkingRouteLine.WalkingStep) {
			nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
			nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep) {
			nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
			nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		}

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		//移动节点至中心
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		// show popup
		popupText = new TextView(DrawerActivity.this);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaiduMap.showInfoWindow(new InfoWindow(popupText, nodeLocation, null));
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	//步行路线
	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DrawerActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			//起终点或途经点地址有岐义，通过以下接口获取建议查询信息
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			routeOverlay = overlay;
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}
	//公交路线
	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {

		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DrawerActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//        	Toast.makeText(this, "进入回调函数了", Toast.LENGTH_SHORT).show();
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
		}
	}

	//驾车路线
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DrawerActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			nodeIndex = -1;
			mBtnPre.setVisibility(View.VISIBLE);
			mBtnNext.setVisibility(View.VISIBLE);
			route = result.getRouteLines().get(0);
			DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
			routeOverlay = overlay;
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result.getRouteLines().get(0));
			overlay.addToMap();
			overlay.zoomToSpan();
		}
	}

	/**
	 * 定制RouteOverly
	 * */
	//驾车的overlay
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}
		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}
		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}
	//步行的overlay
	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}
		@Override
		public BitmapDescriptor getStartMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}
		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (!useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
				return null;
			}
			return null;
		}
	}
	//公交的overlay
	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			if (!useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
			}
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			if (useDefaultIcon) {
				return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
			}
			return null;
		}
	}
	/**
	 * 清除所有Overlay
	 *
	 * @param view
	 */
	public void clearOverlay(View view) {
		mBaiduMap.clear();
	}
	/**
	 * 重新添加Overlay
	 *
	 * @param view
	 */
	public void resetOverlay(View view) {
		clearOverlay(null);
		initOverlay();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}
	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}
	@Override
	protected void onDestroy() {
		// 回收 bitmap 资源
		bdA.recycle();
		bdB.recycle();
		bdC.recycle();
		bdD.recycle();
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mGeoSearch.destroy();
		mMapView.onDestroy();
		mSearch.destroy();
		mMapView = null;
		// 取消监听 SDK 广播
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DrawerActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(bdAAA));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("纬度：%f 经度：%f",
				result.getLocation().latitude, result.getLocation().longitude);
		Toast.makeText(DrawerActivity.this, strInfo, Toast.LENGTH_LONG).show();
	}
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
		{
			if((System.currentTimeMillis()-exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
			{
				Toast.makeText(getApplicationContext(), "再按一次退出程序",Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			}
			else
			{
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}