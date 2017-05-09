package com.xiangxun.ework.legwork;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiangxun.ework.legwork.RoutineDisplay.accessThread1;
import com.xiangxun.ework.legwork.util.HttpUtil;
import com.xiangxun.ework.legwork.util.Url;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class TripSharing extends Activity {
	private ListView mListView;
	private String result = "";
	private SimpleAdapter mSimpleAdapter;
	private static List<HashMap<String, Object>> mList;
	private AccessThread myThread;
	private AccessSearchThread mySearchThread;
	public static String URL = "/x001/addtable/createAddtable"; //加入路线的URL
	public static String URL_JOIN = "/x001/addtable/listAddtable"; //查询所有加入路线的用户ID的URL
	public static String URL_SEARCH = "/x001/routes/listRoute"; //查询所有加入路线的URL
	
	private Handler mHandler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			// 返回的字符串 直接是个数组
			// [{"devid":"1234567800","latitude":"29.4963","longitude":"116.189","postime":"2014-06-10 12:13:00"},
			// {"devid":"1234567832","latitude":"29.4943","longitude":"1161.129","postime":"2014-06-11 12:13:00"}]
			if (msg.what == 1) {
//				Log.v("HANDLE_TRAVEL_TEST", "run handle message");
//				SimpleAdapter mSimpleAdapter = new SimpleAdapter(getApplicationContext(), mList, R.layout.route_list_item,
//						new String[]{"行程", "出发时间"}, new int[]{R.id.route_plan, R.id.start_time});
//				mListView.setAdapter(mSimpleAdapter);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_sharing);
		mListView = (ListView)findViewById(R.id.route_list);
		
		myThread = new AccessThread();   //http请求
		myThread.start();
		
		mySearchThread = new AccessSearchThread();   //http请求
		mySearchThread.start();
		
		show();
		
//		mSimpleAdapter = new SimpleAdapter(getApplicationContext(), mList, R.layout.serach_routine_item
//				, new String[]{"路线ID","路线名称","用户名","地理位置","到达时间","停留时间","创建日期"}
//				, new int[]{R.id.text1, R.id.text1, R.id.text1, R.id.text1, R.id.text1, R.id.text1, R.id.text1,});
//		mListView.setAdapter(mSimpleAdapter);
	}
	//向服务端请求加入
	class AccessThread extends Thread{
		
		@Override
		public void run(){
			synchronized(myThread){
				Message msg1 =mHandler.obtainMessage();
				msg1.what = 1;
				
				// 获得json评论
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// 添加请求参数
				params.add(new BasicNameValuePair("placeid", "timeaa432me"));
				params.add(new BasicNameValuePair("username", "2014"));//最多从服务端返回十条评论
				UrlEncodedFormEntity entity1=null;
				try {
					entity1 =  new UrlEncodedFormEntity(params,HTTP.UTF_8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// 获得请求对象HttpPost
				HttpPost request = HttpUtil.getHttpPost(Url.PrimitiveUrl + URL);
				// 设置查询参数
				request.setEntity(entity1);
				// 获得响应结果
				result = HttpUtil.queryStringForPost(request);
				
				Log.v("HANDLE_TRAVEL_TEST", "result:" + result);
				mHandler.sendEmptyMessage(1);
			}
		}
	}
	//获取服务端的路线信息
	class AccessSearchThread extends Thread{
		
		@Override
		public void run(){
			synchronized(mySearchThread){
				Message msg1 =mHandler.obtainMessage();
				msg1.what = 1;
				
				// 获得json评论
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				// 添加请求参数
				params.add(new BasicNameValuePair("start", "1"));//这个我想是用用户名加时间的方式。来生成。
				params.add(new BasicNameValuePair("retNums", "6"));
				UrlEncodedFormEntity entity1=null;
				try {
					entity1 =  new UrlEncodedFormEntity(params,HTTP.UTF_8);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				// 获得请求对象HttpPost
				HttpPost request = HttpUtil.getHttpPost(Url.PrimitiveUrl + URL_SEARCH);
				// 设置查询参数
				request.setEntity(entity1);
				// 获得响应结果
				result = HttpUtil.queryStringForPost(request);
				
				Log.v("HANDLE_TRAVEL_TEST", "result:" + result);
				parseJsonCommentArray(result);
				mHandler.sendEmptyMessage(1);
			}
		}
	}
	
	// 解析json数据 (解析评论数组)
	public static List<HashMap<String,Object>> parseJsonCommentArray(String json) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONObject(json).getJSONArray("dataList");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObj = null;
			try {
				jsonObj = (JSONObject)jsonArray.get(i);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
			try {
				map.put("路线ID", jsonObj.getString("placeid"));
				map.put("路线名称", jsonObj.getString("placename"));
				map.put("用户名", jsonObj.getString("username"));
				map.put("地理位置", jsonObj.getString("coordinates"));
				map.put("到达时间", jsonObj.getString("arrivetime"));
				map.put("停留时间", jsonObj.getString("staytime"));
				map.put("创建日期", jsonObj.getString("createdate"));
				mList.add(map);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return mList;
	}
	//通过弹出框输入分享行程的打算出发时间
	public void show(){
		AlertDialog mInputDialog = new AlertDialog.Builder(TripSharing.this)
		.setTitle("是否展示路线？")
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mSimpleAdapter = new SimpleAdapter(getApplicationContext(), mList, R.layout.serach_routine_item
						, new String[]{"路线ID","路线名称","用户名","地理位置","到达时间","停留时间","创建日期"}
						, new int[]{R.id.text1, R.id.text2, R.id.text3, R.id.text4, R.id.text5, R.id.text7});
				mListView.setAdapter(mSimpleAdapter);
			}
			
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				finish();
			}
		})
		.create();
		mInputDialog.show();
	}
}
