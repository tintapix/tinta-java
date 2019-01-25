/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;



public class ControlPosition {
    
    public ControlPosition(){
	mSize = -1;
	mPosition = -1;
    }
    
    public ControlPosition(double size, double position){
    	mSize = size;
    	mPosition = position;
    }
    public double mSize;
    public double mPosition;
    
}
