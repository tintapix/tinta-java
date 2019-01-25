/**
 * Copyright (C) 2011 - 2016 Tinta Molygon
 * molygon.com
 * tintapix@gmail.com
 */

package com.tinta.windows;

public class tintaWindow implements tintaIWindow {

	private String 		 mName = new String();
	protected tintaPosition mPos;
	protected tintaPosition mSize;
	protected String mCaption;
	
	
	public void setCaption(String caption){
		mCaption = caption;
	}


	public tintaWindow(String name, tintaPosition pos, tintaPosition size){
	
		mName = name;		
		mPos  = pos;
		mSize = size;
		
	}
	
	public tintaWindow(){
	}
	
	
	public String  getWndName(){
		return mName;
	}
	
	public void  setWndName( String name ){
		mName = name;
	}


	public tintaPosition getWndPos(){
		return mPos;		
	}
	
	public tintaPosition getWndSize(){
		return mSize;	
	}
	
	
	public void setWndPos( tintaPosition pos ){
		mPos = pos;
	}
	
	public void setWndSize(tintaPosition size){
		mSize = size;
	}
	
	public void showWindow(boolean bShow){
	
	}	
}
