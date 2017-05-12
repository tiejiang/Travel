package com.xiangxun.ework.legwork;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class TravelRoutePlanning extends Activity implements OnClickListener {
	private Button xingcheng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.travel_route_planning);

		xingcheng = (Button)findViewById(R.id.xingcheng);
		xingcheng.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// 点击“景点规划”后出现“二级菜单”的三个按钮
		Intent intent = new Intent();
		intent.setClass(this, DrawerActivity.class);
		startActivity(intent);
		finish();
	}
}