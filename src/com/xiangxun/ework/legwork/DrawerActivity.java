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
	//·�߹滮���
	private Button mBtnPre = null;//��һ���ڵ�
    private Button mBtnNext = null;//��һ���ڵ�
    private int nodeIndex = -1;//�ڵ�����,������ڵ�ʱʹ��
    /**
     *·�����ݽṹ�Ļ���,��ʾһ��·�ߣ�·�߿��ܰ�����·�߹滮�еĻ���/�ݳ�/����·��
	 *����Ϊ·�����ݽṹ�Ļ��࣬һ���ע��������󼴿ɣ�����ֱ�����ɸ������
     * */
    private RouteLine route = null;
    private OverlayManager routeOverlay = null;
    private boolean useDefaultIcon = false;
    private TextView popupText = null;//����view
    //������� ��·���滮�����ӿڣ�
    private RoutePlanSearch mSearch = null;    // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
    
	//ָ���̻��ĵ������
	private static final double LATITUDE = 24.603258f;
	private static final double LONGITUDE = 118.085267f;
	//�û�����λ�þ�γ��
	public static double latitude = 0;
	public static double longitude = 0;
	// ��λ���
	LocationClient mLocClient;
	public MyLocationListener myListener = new MyLocationListener();
	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;

	private MapView mMapView;
	private BaiduMap mBaiduMap;
	// UI���
	boolean isFirstLoc = true;// �Ƿ��״ζ�λ
	private String startStr = "���ű�վ";
	private String endStr = "������";
	
	private EditText editSt;
	private EditText editEn;
	private ImageButton sliding;
	private static String addressName = "δ֪��ַ";
	/*************���������***************/
	private Button btnfinish;
//	private Button btnAdd;
	private Marker mMarkerAAA;
	
	// ��ʼ��ȫ�� bitmap ��Ϣ������ʱ��ʱ recycle
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
	/*************���������***************/
	
	/*********************��ַ����***********************************/
	
	GeoCoder mGeoSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	
	
	/*********************��ַ����***********************************/
	
	private static final String LTAG = RoutineDisplay.class.getSimpleName();
 
	/**
	 * ����㲥�����࣬���� SDK key ��֤�Լ������쳣�㲥
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(LTAG, "action: " + s);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				//���޸�Ϊ text��ʾ
				Log.e(LTAG, "key ��֤����! ���� AndroidManifest.xml �ļ��м�� key ����");
			} else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Log.e(LTAG, "�������");
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
        //��ʼ��������buttonΪ���ɼ�
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        
        // ע�� SDK �㲥������
 		IntentFilter iFilter = new IntentFilter();
 		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
 		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
 		mReceiver = new SDKReceiver();
 		registerReceiver(mReceiver, iFilter);
 		// ��ͼ��ʼ��
		mMapView = (MapView) findViewById(R.id.map);
		mBaiduMap = mMapView.getMap();
        //��ʼ������ģ�飬ע���¼�����
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
        /*************���������***************/
//        btnAdd = (Button)findViewById(R.id.btnAdd);
        btnfinish = (Button)findViewById(R.id.btnfinish);
        sliding = (ImageButton)findViewById(R.id.sliding);
        
//        btnAdd.setOnClickListener(this);
        btnfinish.setOnClickListener(this);
        sliding.setOnClickListener(this);
        MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
        mBaiduMap.setMapStatus(msu);
        initOverlay();
        /*************���������***************/
        
        /*************��ַ����***************/
        // ��ʼ������ģ�飬ע���¼�����
 		mGeoSearch = GeoCoder.newInstance();
 		mGeoSearch.setOnGetGeoCodeResultListener(this);
 		
        /*************��ַ����***************/
		//������λͼ��
		mBaiduMap.setMyLocationEnabled(true);
		// ��λ��ʼ��
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();

		imbg=(ImageButton)findViewById(R.id.handle);
		mDrawer=(SlidingDrawer)findViewById(R.id.slidingdrawer);
		list=new ArrayList<GetSet>();
		
		/***********���������·���滮�������*************/
		// ����������ť��Ӧ
        editSt = (EditText) findViewById(R.id.start);
        editEn = (EditText) findViewById(R.id.end);
		
		/***********���������·���滮�������*************/
		
		//����Ĵ���
		addSlidingElement();
	}
	//����Ĵ���
	private void addSlidingElement(){
		//��ʱ��ֵ����ʱ�ö�̬��
		GetSet a=new GetSet();
		GetSet b=new GetSet();
		GetSet c=new GetSet();
		GetSet d=new GetSet();
		a.setName("�г�չʾ");
		b.setName("�г̹滮");
		c.setName("����");
		d.setName("����");
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
	
	//��������س�ʼ��
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
				"��ǰλ�����꣺" + marker.getPosition().latitude + ", "
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
				// ����ѡ���ĵ���Ӹ�����
				LatLng llB = new LatLng(arg0.getPosition().latitude, arg0.getPosition().longitude);
				OverlayOptions ooB = new MarkerOptions()
						.position(llB).icon(bdAAA)
						.zIndex(5).draggable(true);
				mMarkerAAA = (Marker) (mBaiduMap.addOverlay(ooB));
				
				//ʵ�����������view����ʾ
				LayoutInflater layoutInflater = LayoutInflater.from(DrawerActivity.this);
				final View messageView = layoutInflater.inflate(R.layout.message_show, null);
				showMessageView(messageView);
				return false;
			}
			
			@Override
			public void onMapClick(LatLng arg0) {
				// ����ѡ���ĵ���Ӹ�����
				
			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			
			@Override
			public void onMapLongClick(LatLng arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "��ͼ�����¼�����", Toast.LENGTH_LONG).show();
				LayoutInflater layoutInflater = LayoutInflater.from(DrawerActivity.this);
				final View messageView = layoutInflater.inflate(R.layout.map_long_click, null);
				
				AlertDialog builder = new AlertDialog.Builder(DrawerActivity.this)
				.setTitle("�������ַ��")
				.setView(messageView)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText city = (EditText)messageView.findViewById(R.id.city);
						EditText address = (EditText)messageView.findViewById(R.id.geocodekey);
						SearchButtonProcess(city, address);
					}
				})
				.setNegativeButton("ȡ��", null)
				.create();
				builder.show();
			}
		});
	}
	
	/**
	 * ��������
	 * 
	 * @param v
	 */
	public void SearchButtonProcess(EditText editCity, EditText editText) {
			// Geo����
			mGeoSearch.geocode(new GeoCodeOption().city(
					editCity.getText().toString()).address(
					editText.getText().toString()));
	}
	//����ӽڵ㡱���������ӡ���ť�ĵ���¼�
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.btnfinish:
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("����")
				.setMessage("�����ͼ�ۼ���")
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						mBaiduMap.clear();
					}})
				.setNegativeButton("ȡ��", null);
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
	// ���/��ק�����ĵ����Ի���
	private void showMessageView(final View view){
		AlertDialog builder = new AlertDialog.Builder(DrawerActivity.this)
		.setView(view)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
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
					//�������ݿ�
					new SaveData().saveTravelPlanData(getApplicationContext(), 
							planTimeStr, actualTimeStr, 
							latitude, longitude, 
							addressName);
				}
			}
		})
		.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// ɾ����ͼ�����ɵĵ�ǰͼ��
				mMarkerAAA.remove();
			}
		})
		.create();
		builder.show();
	}
	// ��֤����  �����Ƿ�Ϊ��
	private boolean Validate(EditText userEditText, EditText pwdEditText){
		String username = userEditText.getText().toString().trim();
		if(username.equals("")){
			Toast.makeText(getApplicationContext(), "�ƻ������Ǳ�����˵ص�δ���棡", Toast.LENGTH_LONG).show();
			return false;
		}
		String pwd = pwdEditText.getText().toString().trim();
		if(pwd.equals("")){
			Toast.makeText(getApplicationContext(), "�ƻ�ͣ��ʱ���Ǳ�����!�˵ص�δ���棡", Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}
	/**
	 * ��λSDK��������
	 */
	public class MyLocationListener implements BDLocationListener{

		@Override
		public void onReceiveLocation(BDLocation arg0) {
			// TODO Auto-generated method stub
			// map view ���ٺ��ٴ����½��յ�λ��
			if (arg0 == null || mMapView == null){
				return;
			}
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(arg0.getRadius())
					// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
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
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);//���õ�ͼ�����ĵ�
				mBaiduMap.animateMapStatus(u);//�Զ�����ʽ���µ�ͼ״̬��������ʱ 300 ms
			}
		}
	}
	   /**
     * ����·�߹滮����ʾ��
     *
     * @param v
     */
    public void SearchButtonProcess(View v) {
    	
        //��������ڵ��·������
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaiduMap.clear();//��յ�ͼ���е� Overlay �������Լ� InfoWindow
        /**
         * PlanNode
         * ·���滮�еĳ��нڵ���Ϣ,���нڵ��������㣬�յ㣬;����.
         * ���нڵ���Ϣ����ͨ�����ַ�ʽȷ���� 1�� �������нڵ㾭γ������ 2�� �������нڵ�����ͳ�����
         * 
         * �������յ���Ϣ������tranist search ��˵��������������
         * */
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("����", editSt.getText().toString());
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("����", editEn.getText().toString());

        // ʵ��ʹ�����������յ���н�����ȷ���趨 (�ݳ�������������)
        if (v.getId() == R.id.drive) {
        	mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        }else if (v.getId() == R.id.transit) {
        	Toast.makeText(this, "����·�߼���", Toast.LENGTH_SHORT).show();
        	mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode)
                    .city("����")
                    .to(enNode));
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
            
        }
    }
    /**
     * �ڵ����ʾ��
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
        //���ýڵ�����
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
        //��ȡ�ڽ����Ϣ
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
        //�ƶ��ڵ�������
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
    //����·��
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(DrawerActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
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
    //����·��
    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(DrawerActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
//        	Toast.makeText(this, "����ص�������", Toast.LENGTH_SHORT).show();
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
        }
    }
    
    //�ݳ�·��
    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(DrawerActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
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
     * ����RouteOverly
     * */
    //�ݳ���overlay
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
    //���е�overlay
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
    //������overlay
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
	 * �������Overlay
	 * 
	 * @param view
	 */
	public void clearOverlay(View view) {
		mBaiduMap.clear();
	}
	/**
	 * �������Overlay
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
		// ���� bitmap ��Դ
		bdA.recycle();
		bdB.recycle();
		bdC.recycle();
		bdD.recycle();
		// �˳�ʱ���ٶ�λ
		mLocClient.stop();
		// �رն�λͼ��
		mBaiduMap.setMyLocationEnabled(false);
		mGeoSearch.destroy();
		mMapView.onDestroy();
		mSearch.destroy();
		mMapView = null;
		// ȡ������ SDK �㲥
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DrawerActivity.this, "��Ǹ��δ���ҵ����", Toast.LENGTH_LONG)
					.show();
			return;
		}
		mBaiduMap.addOverlay(new MarkerOptions().position(result.getLocation())
				.icon(bdAAA));
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
				.getLocation()));
		String strInfo = String.format("γ�ȣ�%f ���ȣ�%f",
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
             if((System.currentTimeMillis()-exitTime) > 2000)  //System.currentTimeMillis()���ۺ�ʱ���ã��϶�����2000   
             {  
              Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",Toast.LENGTH_SHORT).show();                                  
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
