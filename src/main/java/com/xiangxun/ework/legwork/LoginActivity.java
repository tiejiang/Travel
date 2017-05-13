package com.xiangxun.ework.legwork;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.xiangxun.ework.legwork.util.HttpUtil;
import com.xiangxun.ework.legwork.util.Url;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener {
	private EditText userEditText, pwdEditText;
	private Button cancelButton, loginButton, registerButton;
	private String username, pwd;
	private static String jsonStr = "" ;
	private static final int MAX_REQUIRE_TIME = 3;
	private static final int SHOW_HINT_TIME = 500;
	private boolean isConnect;
	private static String LOG_URL = "/x001/userInfs/loginUser";
	private static String REG_URL = "/x001/userInfs/registeredUser";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		//检查网络状态
		ConnectivityManager con=(ConnectivityManager)getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi=con.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		boolean internet=con.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
		if(wifi|internet){
			//执行相关操作
			isConnect = true;
		}else{
			Toast.makeText(getApplicationContext(),  "请检查网络连接", Toast.LENGTH_LONG).show();
			isConnect = false;
		}

		userEditText = (EditText)findViewById(R.id.userEditText);
		pwdEditText = (EditText)findViewById(R.id.pwdEditText);
		cancelButton = (Button)findViewById(R.id.cancelButton);
		loginButton = (Button)findViewById(R.id.loginButton);
		registerButton = (Button)findViewById(R.id.registerButton);

		cancelButton.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		registerButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
			case R.id.loginButton:
//				if (isConnect) {
//					if (validate()) {
//						if (login()) {
//							Toast.makeText(LoginActivity.this, "登录失败！", SHOW_HINT_TIME).show();
//						}else {
//							saveUserMsg(username, pwd);
							//登陆条件符合 进入主界面
							Intent intent = new Intent();
							intent.setClass(LoginActivity.this, DrawerActivity.class);
							startActivity(intent);
//						}
//					}
//				}else {
//					AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setTitle("提醒")
//							.setMessage("请检查是否连接网络")
//							.setPositiveButton("确定", null);
//					builder.setCancelable(false);
//					builder.create()
//							.show();
//				}

				break;

			case R.id.cancelButton:
				Intent mIntent = new Intent();
				mIntent.setClass(LoginActivity.this, DrawerActivity.class);
				finish();
				System.exit(0);
				break;
			case R.id.registerButton:
				//注册的业务逻辑
				LayoutInflater layoutInflater = LayoutInflater.from(LoginActivity.this);
				final View registerView = layoutInflater.inflate(R.layout.register, null);
				showRegister(registerView);
				break;
		}
	}
	//注册的dialog 和 view
	private void showRegister(final View view){
		AlertDialog builder = new AlertDialog.Builder(LoginActivity.this)
				.setView(view)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						EditText username = (EditText)view.findViewById(R.id.username);
						EditText password = (EditText)view.findViewById(R.id.password);
						EditText school = (EditText)view.findViewById(R.id.school);

						String usernameStr = username.getText().toString().trim();
						String passwordStr = password.getText().toString().trim();
						String schoolStr = school.getText().toString().trim();
						//上传注册信息
						postRegisterMsg(usernameStr, passwordStr, schoolStr);
					}
				})
				.setNegativeButton("取消", null)
				.create();
		builder.show();
	}
	// 验证方法  输入是否为空
	private boolean validate(){
		username = userEditText.getText().toString().trim();
		if(username.equals("")){
			Toast.makeText(LoginActivity.this, "用户名称是必填项！", SHOW_HINT_TIME).show();
			return false;
		}
		pwd = pwdEditText.getText().toString().trim();
		if(pwd.equals("")){
			Toast.makeText(LoginActivity.this, "用户密码是必填项!", SHOW_HINT_TIME).show();
			return false;
		}
		return true;
	}
	// 登录
	private boolean login(){
		jsonStr = queryForHttpPost(username, pwd);
		Log.e("json数据:", jsonStr);
		return parseJsonArray(jsonStr);
	}
	// 请求服务端 获得响应
	private String queryForHttpPost(String account, String password){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 添加请求参数
		params.add(new BasicNameValuePair("username", account));
		params.add(new BasicNameValuePair("pwd", password));
		UrlEncodedFormEntity entity1=null;
		try {
			entity1 =  new UrlEncodedFormEntity(params,HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 获得请求对象HttpPost
		HttpPost request = HttpUtil.getHttpPost(Url.PrimitiveUrl + LOG_URL);
		// 设置查询参数
		request.setEntity(entity1);
		// 获得响应结果
		String result = HttpUtil.queryStringForPost(request);

		Log.v("HANDLE_TRAVEL_TEST", "result:" + result);
		return result;
	}
	// 保存用户名密码
	private void saveUserMsg(String username, String pwd){
		String id = username;
		String name = pwd;
		// 共享信息
		SharedPreferences pre = getSharedPreferences("user_msg", MODE_WORLD_WRITEABLE);
		SharedPreferences.Editor editor = pre.edit();
		editor.putString("id", id);
		editor.putString("name", name);
		editor.commit();
	}
	// 解析json数据 (解析数组)
	private boolean parseJsonArray(String json){
		boolean isnull = false;//标识jsonArray是否为空
		String username = "";
		String userpwd = "";
		try {
			JSONArray jsonArray = new JSONObject(json).getJSONArray("dataList");
			if (jsonArray.isNull(0)) {
				isnull = true;
			}else {
				isnull = false;
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = (JSONObject)jsonArray.get(i);
					username = jsonObj.getString("guuser");
					userpwd = jsonObj.getString("gupwd");
					Log.v("TAG Array", "name:" + username + "password:" + userpwd);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return isnull;
	}
	//上传组注册信息
	public static void postRegisterMsg(String usernameStr, String passwordStr, String schoolStr){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		// 添加请求参数
		params.add(new BasicNameValuePair("username", usernameStr));
		params.add(new BasicNameValuePair("pwd", passwordStr));//最多从服务端返回十条评论
		params.add(new BasicNameValuePair("schoolname", schoolStr));
		UrlEncodedFormEntity entity1=null;
		try {
			entity1 =  new UrlEncodedFormEntity(params,HTTP.UTF_8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 获得请求对象HttpPost
		HttpPost request = HttpUtil.getHttpPost(Url.PrimitiveUrl + REG_URL);
		// 设置查询参数
		request.setEntity(entity1);
		// 获得响应结果
		String result = HttpUtil.queryStringForPost(request);

		Log.v("TIEJIANG", "result:" + result);
	}
}