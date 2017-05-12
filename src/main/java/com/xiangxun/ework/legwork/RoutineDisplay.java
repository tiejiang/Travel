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
	
	GeoCoder mSearch = null; // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��
	
	
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
		
		// ��ʼ������ģ�飬ע���¼�����
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
			
			Toast.makeText(getApplicationContext(), "����ɾ��������¼", Toast.LENGTH_LONG).show();
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
			//��ȡ��Ӧ��·�����ݷ��͵������
			accessThread1 mythread1 = new accessThread1();   //http����
			mythread1.start();
			
			break;
		case R.id.routeshow:
			//չʾ·�ߵ���ͼ��
			queryDatabase();
			Intent intentToMapDiplay = new Intent();
			intentToMapDiplay.setClass(this, RouteMapDisplay.class);
			startActivity(intentToMapDiplay);
			finish();
			break;
		}
	}
	//ͨ����������������г̵Ĵ������ʱ��
	public void showBuilder(){
		LayoutInflater inflater = LayoutInflater.from(RoutineDisplay.this);
		final View inputView = inflater.inflate(R.layout.start_time_input, null);
		AlertDialog mInputDialog = new AlertDialog.Builder(RoutineDisplay.this)
		.setTitle("������Ԥ�Ƴ���ʱ��")
		.setView(inputView)
		.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				EditText time_input = (EditText)inputView.findViewById(R.id.start_time_input);
				timeInput = time_input.getText().toString();
				//��ȡ��Ӧ��·�����ݷ��͵������
				accessThread1 mythread1 = new accessThread1();   //http����
				mythread1.start();
			}
			
		})
		.setNegativeButton("ȡ��", null)
		.create();
		mInputDialog.show();
	}
	//����json--������Ϣ�Ƿ�ɹ��ķ���ֵ�ж�
	private Handler handler1 = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// ���ص��ַ��� ֱ���Ǹ�����
			// [{"devid":"1234567800","latitude":"29.4963","longitude":"116.189","postime":"2014-06-10 12:13:00"},
			// {"devid":"1234567832","latitude":"29.4943","longitude":"1161.129","postime":"2014-06-11 12:13:00"}]
			if (msg.what == 1) {
				Log.v("zms", "ʹ��httpclient,���ص�json");
				try {
					JSONObject jsonObject = new JSONObject(
							String.valueOf(msg.obj));// "{\"result\":\"0\",\"msg\":\"ok\",\"data\":[{\"username\":\"����\",\"department\":\"�з���\",\"businessid\":1,\"title\":\"test\",\"content\":\"wu\"}]}"
					if("�ɹ�".equals(jsonObject.getString("bizCode")))
					{
						Toast.makeText(getApplicationContext(), "���ͳɹ�", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	//��Ϣ����
	class accessThread1 extends Thread {

		@Override
		public void run() {

			Log.v("zms", "�߳�accessThread��ʼ");
			Message msg1 = handler1.obtainMessage();
			msg1.what = 1;
			
			String startTime = getTimeInput();
			Log.v("HANDLE_TRAVEL_TEST", "startTime:" + startTime);
			
			mCursor.moveToFirst();
			Vector<String> addressString = new Vector<String>();
			while (!mCursor.isAfterLast()) {
//				address = mCursor.getString(Constant.CUR_ADDRESS_NAME);//����
				addressString.add(mCursor.getString(Constant.CUR_ADDRESS_NAME));
				mCursor.moveToNext();
			}
			Log.v("HANDLE_TRAVEL_TEST", "addressString:" + addressString.toString());
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("placeid", "timeaa432me"));//������������û�����ʱ��ķ�ʽ�������ɡ�
			params.add(new BasicNameValuePair("placename", addressString.toString()));
			params.add(new BasicNameValuePair("username", "2014"));
			params.add(new BasicNameValuePair("coordinates", "xxxx21xxxxx"));
			params.add(new BasicNameValuePair("arrivetime", timeInput)); //����ʱ��
			params.add(new BasicNameValuePair("staytime", "2014-11-23"));
			params.add(new BasicNameValuePair("note", ""));
			UrlEncodedFormEntity entity1=null;
			try {
				entity1 =  new UrlEncodedFormEntity(params,HTTP.UTF_8);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			// ����������HttpPost
			HttpPost request = HttpUtil.getHttpPost(Url.PrimitiveUrl+baseUrl);
			// ���ò�ѯ����
			request.setEntity(entity1);
			// �����Ӧ���
			String result= HttpUtil.queryStringForPost(request);
			Log.e("�ύ�ɹ���", "ok!");
			handler1.sendMessage(msg1);
			super.run();
		}
	}
	//�����ݿ��ȡ�ϴ�������˵�����
	public void gainData(){
		mCursor.moveToFirst();
		while (!mCursor.isAfterLast()) {
			String Address = mCursor.getString(Constant.CUR_ADDRESS_NAME);//����
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("address", Address);
			mCursor.moveToNext();
		}
	}
	
	//��ѯ���ݿ�õ�ÿ�����latitude��longitude��װ��arraylist
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
