/**
 * Copyright (C) 2011 - 2016 Tinta Molygon
 * molygon.com
 * tintapix@gmail.com
 */

package com.tinta.windows.swt;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.tinta.windows.*;

public class tintaSWTWindow extends tintaWindow {

	private Shell mShell = null;
	
	private Display mDisplay = null; 
	
	public Shell getShell() {
		return mShell;
	}
	
	
	
	public final Display getDisplay() {
		return mDisplay;
	}
	public tintaSWTWindow(){		
	}
	
	public tintaSWTWindow(String name , tintaPosition pos, tintaPosition size , Display disp, int style){
		super(name, pos, size);
		
		if(style ==  -1)
			style = SWT.CLOSE | SWT.RESIZE;
		
		mDisplay = 	disp;	
		mShell = new Shell(disp);
		
		mShell.setSize(mSize.mXPos, mSize.mYPos);
		mShell.setLocation(mPos.mXPos, mPos.mYPos);
		//mShell.open();
		
	}
	
	public tintaSWTWindow(String name , tintaPosition pos, tintaPosition size , Shell parWnd, int style){
		super(name, pos, size);
		
		if( style ==  -1 )
			style = SWT.CLOSE | SWT.RESIZE;
		
		mShell = parWnd; //new Shell(parWnd);
		mShell.setSize(mSize.mXPos, mSize.mYPos);
		mShell.setLocation(pos.mXPos, pos.mYPos);
	}
	
	public void showWindow(boolean bShow){
		if(bShow)
			mShell.open();
		else
			mShell.close();
	}
	
	public void setFontSize(Control ctrl, int fontSize){
		Font initialFont = ctrl.getFont();
		
		FontData[] fontData = initialFont.getFontData();
		for ( int i = 0; i < fontData.length; i++ ) {
		      fontData[i].setHeight(fontSize);
		}
		
		Font newFont = new Font( getShell().getDisplay(), fontData );
		ctrl.setFont( newFont );
	}	
	
	public void setCaption(String caption){
		super.setCaption(caption);
		mShell.setText(mCaption);
	}
	
	public void setWndPos( tintaPosition pos ){
		super.setWndPos(pos);
		mShell.setLocation(pos.mXPos, pos.mYPos);
		
	}
	
	public void setWndSize(tintaPosition size){
		super.setWndSize(size);
		mShell.setSize(size.mXPos, size.mYPos);
	}
	
	
}
