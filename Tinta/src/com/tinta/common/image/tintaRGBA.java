/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 * 
 */


package com.tinta.common.image;

import com.tinta.math.tintaUtilMath;

/**
 * Represents 16 bit integer 4 channel pixel 
 * @author Mikhail Evdokimov
 *
 */
public class tintaRGBA {
	
	private static final short channels = 4;
	
	private final short mData [] =  {0, 0, 0, 0};
	
	private int mPacked;	
	
	private void updatePacked(){
		mPacked = tintaUtilMath.packColor4(mData[0], mData[1], mData[2], mData[3]);
	}
	
	/**
	 * Constructor
	 */
	public tintaRGBA(){}
	
	/**
	 * Constructor
	 */
	public tintaRGBA(  final short data [] ){
		System.arraycopy(data, 0, mData, 0, channels);		
		updatePacked();
	}
	
	/**
	 * Copy constructor
	 */
	public tintaRGBA(  final tintaRGBA rv ){
		System.arraycopy(rv.mData, 0, mData, 0, channels);		
		mPacked = rv.mPacked;
	}
	
	
	public tintaRGBA(  short r, short g, short b, short a ){
		mData[0] = r;
		mData[1] = g;
		mData[2] = b;
		mData[3] = a;
	}
	
	
	public tintaRGBA(  final float data [] ){
		assert data.length <= channels : "array length";
		
		mData[0] = tintaUtilMath.floatToByte(data[0]);
		mData[1] = tintaUtilMath.floatToByte(data[1]);
		mData[2] = tintaUtilMath.floatToByte(data[2]);
		mData[2] = tintaUtilMath.floatToByte(data[3]);
		updatePacked();
	}
	public tintaRGBA(  final tintaRGBAf rv ){
		mData[0] = tintaUtilMath.floatToByte(rv.getData()[0]);
		mData[1] = tintaUtilMath.floatToByte(rv.getData()[1]);
		mData[2] = tintaUtilMath.floatToByte(rv.getData()[2]);
		mData[2] = tintaUtilMath.floatToByte(rv.getData()[3]);
		updatePacked();
	}
	public tintaRGBA(  float r, float g, float b, float a ){
		mData[0] = tintaUtilMath.floatToByte(r);
		mData[1] = tintaUtilMath.floatToByte(g);
		mData[2] = tintaUtilMath.floatToByte(b);
		mData[2] = tintaUtilMath.floatToByte(a);
		updatePacked();
	}
	
	
	
	public final short[] getData(){
		return mData;
	}

	public void setData(int channel, short val){
		assert channel > 0 && channel < channels  + 1 : "array boundary error";
		mData[channel] = val;
		updatePacked();
	}
	
	public int hashCode() {
		return this.mPacked;
	} 
	
	public short getData(int channel){
		assert channel > 0 && channel < channels  + 1 : "array boundary error";
		return mData[channel];
	}
}
