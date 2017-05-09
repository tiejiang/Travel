package com.xiangxun.ework.legwork;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.xiangxun.ework.legwork.constant.Constant;
import com.xiangxun.ework.legwork.provider.DBHelper;
import com.xiangxun.ework.legwork.util.HttpUtil;
import com.xiangxun.ework.legwork.util.Url;
import com.xiangxun.ework.legwork.util.util;
import com.xiangxun.ework.staticarray.ImageForOverlay;


public class RoutineDisplay extends FragmentActivity implements OnClickListener {
	private Button show;
	private Button share;
	private Button routeshow;
	
	private ListView listview;
	private DBHelper dbHelper;
	private SQLiteDatabase sqlDB;
	private Cursor mCursor;
	private SimpleCursorAdapter mSimpleCursorAdapter;
	
	public String latitudeString = null;
	public String longitudeString = null;
	public String startAddressString = null;
	public String endAddressString = null;
//	public String[] addressString = null;
	
	public float latitude; 
	public float longitude;
	
	public static boolean isDisplayRoute = false;
	public static boolean isSearch = false;
	
	private String timeInput;
	public ArrayList<HashMap<String, Object>> dataListItem = new ArrayList<HashMap<String, Object>>();
	public static ArrayList<HashMap<String, Object>> dataListFromSQLite = new ArrayList<HashMap<String, Object>>();
	
	private String baseUrl = "/x001/routes/createRoute";
	
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.daily_routine_activity);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		
		show = (Button)findViewById(R.id.today);
		share = (Button)findViewById(R.id.confirm);
		routeshow = (Button)findViewById(R.id.routeshow);
		listview = (ListView)findViewById(R.id.listview);
		
		share.setVisibility(View.INVISIBLE);
		routeshow.setVisibility(View.INVISIBLE);
		
		share.setOnClickListener(this);
		show.setOnClickListener(this);
		routeshow.setOnClickListener(this);
		
//		mAddressName = new Vector<String>();
		
		// 初始化搜索模块，注册事件监听
		listview.setOnItemLongClickListener(new onLongClickListener());
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
	public void showRoutinePlanData(){
		dbHelper = new DBHelper(RoutineDisplay.this);
		sqlDB = dbHelper.getWritableDatabase();
		String[] colums = new String[] {"_id", 
				DBHelper.COL_PLAN_STAY_TIME, 
				DBHelper.COL_PLAN_ARRIVE_TIME, 
				DBHelper.COL_CURRENT_LATITUDE, 
				DBHelper.COL_CUTTENT_LONGITUDE,
				DBHelper.COL_CUTTENT_ADDRESS};
		mCursor = sqlDB.query(DBHelper.TABLE_NAME, colums, null, null, null, null, null);
		String[] headers = new String[] {
				DBHelper.COL_PLAN_STAY_TIME, 
				DBHelper.COL_PLAN_ARRIVE_TIME, 
				DBHelper.COL_CURRENT_LATITUDE, 
				DBHelper.COL_CUTTENT_LONGITUDE,
				DBHelper.COL_CUTTENT_ADDRESS};
		mSimpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.daily_routine_item, mCursor, headers, 
				new int[]{R.id.text1, R.id.text2,  R.id.text3, R.id.text4, R.id.text5});
		listview.setAdapter(mSimpleCursorAdapter);
		
	}
	class onLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			
			Toast.makeText(getApplicationContext(), "长按删除此条记录", Toast.LENGTH_LONG).show();
			mCursor.moveToPosition(position);
			String rowId = mCursor.getString(0);
			
			sqlDB.delete(DBHelper.TABLE_NAME, "_id=?", new String[]{rowId});
			mCursor.requery();
			mSimpleCursorAdapter.notifyDataSetChanged();
			return false;
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.today:
			showRoutinePlanData();
			show.setVisibility(View.GONE);
			share.setVisibility(View.VISIBLE);
			routeshow.setVisibility(View.VISIBLE);
			break;
		case R.id.confirm:
			showBuilder();
			//提取相应的路线数据发送到服务端
			accessThread1 mythread1 = new accessThread1();   //http请求
			mythread1.start();
			
			break;
		case R.id.routeshow:
			//展示路线到地图上
			queryDatabase();
			Intent intentToMapDiplay = new Intent();
			intentToMapDiplay.setClass(this, RouteMapDisplay.class);
			startActivity(intentToMapDiplay);
			finish();
			break;
		}
	}
	//通过弹出框输入分享行程的打算出发时间
	public void showBuilder(){
		LayoutInflater inflater = LayoutInflater.from(RoutineDisplay.this);
		final View inputView = inflater.inflate(R.layout.start_time_input, null);
		AlertDialog mInputDialog = new AlertDialog.Builder(RoutineDisplay.this)
		.setTitle("请输入预计出发时间")
		.setView(inputView)
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText time_input = (EditText)inputView.findViewById(R.id.start_time_input);
				timeInput = time_input.getText().toString();
				//提取相应的路线数据发送到服务端
				accessThread1 mythread1 = new accessThread1();   //http请求
				mythread1.start();
			}
			
		})
		.setNegativeButton("取消", null)
		.create();
		mInputDialog.show();
	}
	//解析json--发送信息是否成功的返回值判断
	private Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// 返回的字符串 直接是个数组
			// [{"devid":"1234567800","latitude":"29.4963","longitude":"116.189","postime":"2014-06-10 12:13:00"},
			// {"devid":"1234567832","latitude":"29.4943","longitude":"1161.129","postime":"2014-06-11 12:13:00"}]
			if (msg.what == 1) {
				Log.v("zms", "使用httpclient,返回的json");
				try {
					JSONObject jsonObject = new JSONObject(
							String.valueOf(msg.obj));// "{\"result\":\"0\",\"msg\":\"ok\",\"data\":[{\"username\":\"姓名\",\"department\":\"研发部\",\"businessid\":1,\"title\":\"test\",\"content\":\"wu\"}]}"
					if("成功".equals(jsonObject.getString("bizCode")))
					{
						Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	//消息发送
	class accessThread1 extends Thread {

		@Override
		public void run() {

			Log.v("zms", "线程accessThread开始");
			Message msg1 = handler1.obtainMessage();
			msg1.what = 1;
			
			String startTime = getTimeInput();
			Log.v("HANDLE_TRAVEL_TEST", "startTime:" + startTime);
			
			mCursor.moveToFirst();
			Vector<String> addressString = new Vector<String>();
			while (!mCursor.isAfterLast()) {
//				address = mCursor.getString(Constant.CUR_ADDRESS_NAME);//地名
				addressString.add(mCursor.getString(Constant.CUR_ADDRESS_NAME));
				mCursor.moveToNext();
			}
			Log.v("HANDLE_TRAVEL_TEST", "addressString:" + addressString.toString());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("placeid", "timeaa432me"));//这个我想是用用户名加时间的方式。来生成。
			params.add(new BasicNameValuePair("placename", addressString.toString()));
			params.add(new BasicNameValuePair("username", "2014"));
			params.add(new BasicNameValuePair("coordinates", "xxxx21xxxxx"));
			params.add(new BasicNameValuePair("arrivetime", timeInput)); //出发时间
			params.add(new BasicNameValuePair("staytime", "2014-11-23"));
			params.add(new BasicNameValuePair("note", ""));
			UrlEncodedFormEntity entity1=null;
			try {
				entity1 =  new UrlEncodedFormEntity(params,HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// 获得请求对象HttpPost
			HttpPost request = HttpUtil.getHttpPost(Url.PrimitiveUrl+baseUrl);
			// 设置查询参数
			request.setEntity(entity1);
			// 获得响应结果
			String result= HttpUtil.queryStringForPost(request);
			Log.e("提交成功！", "ok!");
			handler1.sendMessage(msg1);
			super.run();
		}
	}
	//从数据库获取上传到服务端的数据
	public void gainData(){
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			String Address = mCursor.getString(Constant.CUR_ADDRESS_NAME);//地名
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("address", Address);
			mCursor.moveToNext();
		}
	}
	
	//查询数据库得到每个点的latitude、longitude并装入arraylist
	public void queryDatabase(){
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			String latitude = mCursor.getString(Constant.CUR_LATITUDDE);
			String longitude = mCursor.getString(Constant.CUR_LONGITUDE);
			String startTimePlan = mCursor.getString(Constant.CUR_START_TIME_PLAN);
			String startTimeActual = mCursor.getString(Constant.CUR_START_TIME_ACTUAL);
			String currentAddress = mCursor.getString(Constant.CUR_ADDRESS_NAME);
			double latituded = Double.parseDouble(latitude);
			double longituded = Double.parseDouble(longitude);
			
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("latitude", latituded);
			map.put("longitude", longituded);
			map.put("startTimePlan", startTimePlan);
			map.put("startTimeActual", startTimeActual);
			map.put("address", currentAddress);
			dataListFromSQLite.add(map);
			
			mCursor.moveToNext();
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCursor.close();
		sqlDB.close();
		dbHelper.close();
	}
	public String getTimeInput(){
		return timeInput;
	}
	public void setTimeInput(String timeInput) {
		this.timeInput = timeInput;
	}
	
}
