/**
 * Copyright (C) 2011 - 2016 Tinta Molygon
 * molygon.com
 * tintapix@gmail.com
 */

package com.tinta.windows;

public interface tintaIWindow {

	
	public String  getWndName();

	public tintaPosition getWndPos();	
	
	public tintaPosition getWndSize();	
	
	public void setWndPos(tintaPosition pos);	
	
	public void setWndSize(tintaPosition size);
	
	public void showWindow(boolean bShow);
	
	public void setCaption(String caption);
	
	
}
