package com.xiangxun.ework.legwork.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "myData";
	private static final int DATABASE_VERSION = 1;
	//历史消费表的属性值
	public static final String TABLE_NAME = "EXPENDITURE";
	public static final String COL_PLAN_STAY_TIME = "planStayTime";
	public static final String COL_PLAN_ARRIVE_TIME = "planArriveTime";
	public static final String COL_CURRENT_LATITUDE = "currentLatitude";
	public static final String COL_CUTTENT_LONGITUDE = "currentLongitude";
	public static final String COL_CUTTENT_ADDRESS = "currentAddress";
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE "
			+ TABLE_NAME
			+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ COL_PLAN_ARRIVE_TIME + " TEXT, "
			+ COL_PLAN_STAY_TIME + " TEXT, "
			+ COL_CURRENT_LATITUDE + " TEXT, "
			+ COL_CUTTENT_LONGITUDE + " TEXT, "
			+ COL_CUTTENT_ADDRESS + " TEXT"
			+ ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
}
