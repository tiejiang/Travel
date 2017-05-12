package com.xiangxun.ework.staticarray;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.xiangxun.ework.legwork.R;

public class ImageForOverlay {
	static BitmapDescriptor bdAA = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marka);
	static BitmapDescriptor bdBB = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markb);
	static BitmapDescriptor bdCC = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markc);
	static BitmapDescriptor bdDD = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markd);
	static BitmapDescriptor bdEE = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marke);
	static BitmapDescriptor bdFF = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markf);
	static BitmapDescriptor bdGG = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markg);
	static BitmapDescriptor bdHH = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markh);
	static BitmapDescriptor bdII = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_marki);
	static BitmapDescriptor bdJJ = BitmapDescriptorFactory
			.fromResource(R.drawable.icon_markj);
	
	static Marker mMarkerA;
	static Marker mMarkerB;
	static Marker mMarkerC;
	static Marker mMarkerD;
	static Marker mMarkerE;
	static Marker mMarkerF;
	static Marker mMarkerG;
	static Marker mMarkerH;
	static Marker mMarkerI;
	static Marker mMarkerJ;
	
	public static BitmapDescriptor[] mOverlayImage = {
		bdAA, bdBB, bdCC, bdDD, bdEE, bdFF, bdGG, bdHH, bdII, bdJJ
		
	};
	public static  Marker[] overlayMarker = {
		mMarkerA, mMarkerB, mMarkerC, mMarkerD, mMarkerE, mMarkerF, mMarkerG, mMarkerH, mMarkerI, mMarkerJ
		
	};
}
