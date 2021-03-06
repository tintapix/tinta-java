/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.tinta.common.tintaControlPos;
import com.tinta.common.tintaGridPack;

public class configSWT  {
    
    
    private Shell shell;
    
    private String dlgText;
    
    private Button mBtnAll;
    
    private Button mBtnOk;
    
    private Text mText;// = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
    
    private final String defaultNumber = "10";
    
    private Integer mMaxWordsAll = 0;   	   
    	   
    protected tintaGridPack packControls = null;    
    
    public configSWT( Shell parent, int style, String header, Point pos, Point size, int maxWord){
    	
    	mMaxWordsAll = maxWord;	
    	
		shell = new Shell(parent, style);
		dlgText = header;
        shell.setText(dlgText);   
        
        shell.setSize(size);
        shell.setLocation(pos);
        int h = shell.getClientArea().height;
        int w = shell.getClientArea().width;
        packControls = new tintaGridPack(w,
        		h, 1, 3);
        
        // lines 3
     	// columns 1
     		
     	packControls.add(0, 0, 1, 1); 		
     	packControls.add(0, 1, 1, 1);
     	packControls.add(0, 2, 1, 1);
        
        mBtnAll = new Button( shell, SWT.NONE );
        mBtnAll.setText( Integer.toString( maxWord ));
        mBtnAll.addMouseListener( new MouseAdapter() {
        
        	@Override
    	    public void mouseDown(MouseEvent e)
    	    {
        		mText.setText(mBtnAll.getText());
        		ApplicationSWT.mMaxWordsSampl = mMaxWordsAll;
    	    }
        } );
        
        mBtnOk = new Button( shell, SWT.NONE );
        mBtnOk.setText( "OK");
       
        mBtnOk.addMouseListener( new MouseAdapter() {	
	    
	    @Override
	    public void mouseDown(MouseEvent e)
	    {
		String text = mText.getText();
		if( text.length() != 0 )
		    ApplicationSWT.mMaxWordsSampl = Integer.parseInt( text );
		
		shell.dispose();
	    }	    
	} );

        
        mText = new Text(shell, SWT.BORDER | SWT.SINGLE);
        mText.setText(defaultNumber);
        mText.addListener(SWT.Verify, new Listener() {
        	
            public void handleEvent(Event e) {
            	
              String string = e.text;
              char[] chars = new char[string.length()];
              string.getChars(0, chars.length, chars, 0);
              
              for (int i = 0; i < chars.length; i++) {
                if (!('0' <= chars[i] && chars[i] <= '9')) {
                  e.doit = false;
                  return;
                }
              }
              
            }
          });
        
              
        
        tintaControlPos posPack = packControls.getPosition(0);
        mText.setSize((int) posPack.mSize.mx, (int) posPack.mSize.my);
        mText.setLocation((int) posPack.mPosition.mx, (int) posPack.mPosition.my);
		posPack = packControls.getPosition(1);
		mBtnAll.setSize((int) posPack.mSize.mx, (int) posPack.mSize.my);
		mBtnAll.setLocation((int) posPack.mPosition.mx, (int) posPack.mPosition.my);
		posPack = packControls.getPosition(2);
		mBtnOk.setSize((int) posPack.mSize.mx, (int) posPack.mSize.my);
		mBtnOk.setLocation((int) posPack.mPosition.mx, (int) posPack.mPosition.my);
    }
    
    
    
    public void showDlg(Display display ){
	 shell.open();	
	 
	 while (!shell.isDisposed()) {
	     if (!display.readAndDispatch()) {
	         display.sleep();
	     }
	 }
    }

}
