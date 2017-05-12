/**
 * 保存数据到数据库
 * */
package com.xiangxun.ework.legwork.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SaveData {
	private DBHelper dbHelper;
	private SQLiteDatabase sqlDb;
	public void saveTravelPlanData(Context context, String planStayTime,
			String planArriveTime, String currentLatitude, String currentLongitude, String currentAddress){
		dbHelper = new DBHelper(context);
		sqlDb = dbHelper.getReadableDatabase();
		ContentValues cv = new ContentValues(4);
		cv.put(DBHelper.COL_PLAN_ARRIVE_TIME, planStayTime);
		cv.put(DBHelper.COL_PLAN_STAY_TIME, planArriveTime);
		cv.put(DBHelper.COL_CURRENT_LATITUDE, currentLatitude);
		cv.put(DBHelper.COL_CUTTENT_LONGITUDE, currentLongitude);
		cv.put(DBHelper.COL_CUTTENT_ADDRESS, currentAddress);
		sqlDb.insert(DBHelper.TABLE_NAME, null, cv);
	}
}
