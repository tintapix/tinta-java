/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */


package com.tinta.common.image;

import com.tinta.math.tintaUtilMath;


/**
 * Represents 32 bit float 4 channel pixel
 * @author Mikhail Evdokimov
 *
 */
public class tintaRGBAf {
	
	private static final short channels = 4;
	private final float mData [] =  {0.f, 0.f, 0.f, 0.f}; 

	private int mPacked;
	
	private void updatePacked(){
		mPacked = tintaUtilMath.packColor4( tintaUtilMath.floatToByte(mData[0]), 
				tintaUtilMath.floatToByte(mData[1]), 
					tintaUtilMath.floatToByte(mData[2]),
							tintaUtilMath.floatToByte(mData[3]));
	}
	
	
	public tintaRGBAf(){}
	public tintaRGBAf(  final float data [] ){
		System.arraycopy(data, 0, mData, 0, channels);
		updatePacked();
	}
	
	public tintaRGBAf(  final tintaRGBAf rv ){
		System.arraycopy(rv.mData, 0, mData, 0, channels);		
		mPacked = rv.mPacked;
	}
	
	public tintaRGBAf(  float r, float g, float b, float a ){
		mData[0] = r;
		mData[1] = g;
		mData[2] = b;
		mData[3] = a;
		updatePacked();
	}
	
	
	public tintaRGBAf(  final short data [] ){
		assert data.length <= channels : "array length";
		
		mData[0] = tintaUtilMath.byteToFloat(data[0]);
		mData[1] = tintaUtilMath.byteToFloat(data[1]);
		mData[2] = tintaUtilMath.byteToFloat(data[2]);
		mData[2] = tintaUtilMath.byteToFloat(data[3]);
		mPacked = tintaUtilMath.packColor4(data[0], data[1], data[2], data[3]);
	}
	public tintaRGBAf(  final tintaRGBA rv ){
		mData[0] = tintaUtilMath.byteToFloat(rv.getData()[0]);
		mData[1] = tintaUtilMath.byteToFloat(rv.getData()[1]);
		mData[2] = tintaUtilMath.byteToFloat(rv.getData()[2]);
		mData[2] = tintaUtilMath.byteToFloat(rv.getData()[3]);
		
		mPacked = tintaUtilMath.packColor4(rv.getData()[0], rv.getData()[1], rv.getData()[2], rv.getData()[3]);
	}
	public tintaRGBAf(  short r, short g, short b, short a ){
		mData[0] = tintaUtilMath.byteToFloat(r);
		mData[1] = tintaUtilMath.byteToFloat(g);
		mData[2] = tintaUtilMath.byteToFloat(b);
		mData[2] = tintaUtilMath.byteToFloat(a);
		mPacked = tintaUtilMath.packColor4(r, g, b, a);
	}
	
	
	
	public final float[] getData(){
		return mData;
	}
	
	
	public void setData(int channel, float val){
		assert channel > 0 && channel < channels : "array boundary error";
		mData[channel] = val;
		updatePacked();
	}
	
	public float getData(int channel){
		assert channel > 0 && channel < channels : "array boundary error";
		return mData[channel];
	}
}
