/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */


package com.tinta.common;

public class tintaExtreme<T> {
	
	public tintaExtreme(  ){}
	
	public tintaExtreme( tintaExtreme<T> rVal ){
		mXMin = rVal.mXMin;
		mXMax = rVal.mXMax;
		mYMin = rVal.mYMin;
		mYMax = rVal.mYMax;
	}

	public tintaExtreme( T xMin, T xMax, T yMin, T yMax )
	{
		mXMin = xMin;
		mXMax = xMax;
		mYMin = yMin;
		mYMax = yMax;
	}
	
	@Override
	public boolean equals(Object other){
	    if (other instanceof tintaExtreme<?>){
	        if ( ((tintaExtreme<?>)other).mXMin.equals(mXMin) 
	        		&& ((tintaExtreme<?>)other).mXMax.equals(mXMax)
	        			&& ((tintaExtreme<?>)other).mYMin.equals(mYMin) 
	        				&& ((tintaExtreme<?>)other).mYMax.equals(mYMax)){
	        	
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override	
	public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((mXMin == null) ? 0 : mXMin.hashCode());
	    result = prime * result + ((mXMax == null) ? 0 : mXMax.hashCode());
	    result = prime * result + ((mYMin == null) ? 0 : mYMin.hashCode());
	    result = prime * result + ((mYMax == null) ? 0 : mYMax.hashCode());
	    
	    return result;
	}

	protected T mXMin;
	protected T mXMax;
	protected T mYMin;		
	protected T mYMax;
}
