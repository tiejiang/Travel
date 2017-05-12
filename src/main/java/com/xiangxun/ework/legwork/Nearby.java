package com.xiangxun.ework.legwork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Nearby extends Activity implements OnClickListener {
	private Button jingdian;
	private Button meishi;
	private Button jiudian;
	private Button gouwu;
	private Button yule;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layer_control);
		
		jingdian = (Button)findViewById(R.id.jingdian);
		meishi = (Button)findViewById(R.id.meishi);
		jiudian = (Button)findViewById(R.id.jiudian);
		gouwu = (Button)findViewById(R.id.gouwu);
		yule = (Button)findViewById(R.id.yule);
		
		jingdian.setOnClickListener(this);
		meishi.setOnClickListener(this);
		jiudian.setOnClickListener(this);
		gouwu.setOnClickListener(this);
		yule.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), NearbyPoiSearch.class);
		switch (v.getId()) {
		case R.id.jingdian:
			intent.putExtra("POI_KEY", "景点");
			startActivity(intent);
			finish(); 
			break;
		case R.id.meishi:
			intent.putExtra("POI_KEY", "美食");
			startActivity(intent);
			finish();
			break;
		case R.id.jiudian:
			intent.putExtra("POI_KEY", "酒店");
			startActivity(intent);
			finish();
			break;
		case R.id.gouwu:
			intent.putExtra("POI_KEY", "购物");
			startActivity(intent);
			finish();
			break;
		case R.id.yule:
			intent.putExtra("POI_KEY", "娱乐");
			startActivity(intent);
			finish();
			break;
		}
	}
}
