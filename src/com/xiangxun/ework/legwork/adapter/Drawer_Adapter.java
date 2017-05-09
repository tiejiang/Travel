package com.xiangxun.ework.legwork.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.xiangxun.ework.legwork.Nearby;
import com.xiangxun.ework.legwork.RoutineDisplay;
import com.xiangxun.ework.legwork.TripSharing;
import com.xiangxun.ework.legwork.TravelRoutePlanning;
import com.xiangxun.ework.legwork.R;
import com.xiangxun.ework.legwork.model.GetSet;


public class Drawer_Adapter extends BaseAdapter {
	List<GetSet> list;
	LayoutInflater inflater;
	private Context context;
	
	public Drawer_Adapter(Context context,List<GetSet> list){
		this.context=context;
		this.list=list;
		this.inflater=LayoutInflater.from(context);
	}
	public int getCount() {
		return list.size();
	}

	public GetSet getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("CutPasteId")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView==null) {
			convertView=inflater.inflate(R.layout.drawer_item, null);
		}
	//	TextView textView=(TextView) convertView.findViewById(R.id.drawer_item_button);
		Button button=(Button) convertView.findViewById(R.id.drawer_item_button);
		
		button.setText(getItem(position).getName());
		switch (position) {
		case 0:
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context,RoutineDisplay.class);
					context.startActivity(intent);
				}
			});
			break;

		case 1:
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context,TravelRoutePlanning.class);
					context.startActivity(intent);
					
				}
			});
			break;
		case 2:
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context, Nearby.class);
					context.startActivity(intent);
				}
			});
			break;	
		case 3:
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(context,TripSharing.class);
					context.startActivity(intent);
				}
			});
			break;	
		}
		return convertView;
	}
}
