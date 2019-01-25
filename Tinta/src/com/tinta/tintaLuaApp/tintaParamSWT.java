package com.tinta.tintaLuaApp;

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


public class tintaParamSWT {
private Shell shell;
    
    private String dlgText;
    
    private Button mBtnCancel;
    
    private Button mBtnOk;
    
    private tintaStringParam mParam;
    
    private Text   mText; // = new Text(shell, SWT.BORDER | SWT.V_SCROLL);
    	   
    protected tintaGridPack packControls = null;    
    
    public tintaParamSWT( Shell parent, int style, String header, Point pos, Point size, tintaStringParam paramOut){
    	
    	mParam = paramOut;
    	
		shell = new Shell( parent, style );
		dlgText = header;
		
		
		
		
        shell.setText(dlgText);   
        
        shell.setSize(size);
        shell.setLocation(pos);
        int h = shell.getClientArea().height;
        int w = shell.getClientArea().width;
        packControls = new tintaGridPack(w, h, 2, 2);
        
        
        
        
        // lines 2
     	// columns 2
     		
     	packControls.add(0, 0, 2, 1); 		
     	packControls.add(0, 1, 1, 1);
     	packControls.add(1, 1, 1, 1);
     	
     	
     	
     	mBtnCancel = new Button( shell, SWT.NONE );
     	mBtnCancel.setText( "Cancel");
     	mBtnCancel.addMouseListener( new MouseAdapter() {
        
        	@Override
    	    public void mouseDown(MouseEvent e)
    	    {
        		mParam.mValue = new String();
        		shell.close();
    	    }
        } );
        
        mBtnOk = new Button( shell, SWT.NONE );
        mBtnOk.setText( "OK");
       
        mBtnOk.addMouseListener( new MouseAdapter() {	
	    
	    @Override
	    public void mouseDown(MouseEvent e)
	    {
	    	mParam.mValue = mText.getText();
		
		
		shell.dispose();
	    }	    
	} );

        
        mText = new Text(shell, SWT.BORDER | SWT.SINGLE);
        
        if(mParam.mValue.length() != 0 )
        	mText.setText(mParam.mValue);
        
        mText.addListener(SWT.Verify, new Listener() {
        	
	            public void handleEvent(Event e) {
	            	
	            if( mParam.getType() == tintaStringParam.ParamType.typeInteger ){
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
	            
	            if( mParam.getType() == tintaStringParam.ParamType.typeReal ){
	            	String string = e.text;
		              char[] chars = new char[string.length()];
		              string.getChars(0, chars.length, chars, 0);
		              String text = mText.getText();
		              
		              // already has '.' or ','
		              int f = text.indexOf(".");
		              if( f == -1 )
		            	  f = text.indexOf(",");
		              
		              for (int i = 0; i < chars.length; i++) {
		                if (!('0' <= chars[i] && chars[i] <= '9' 
		                			|| ( ( mText.getText().length() > 0  ) && f == -1  
		                						&& ( chars[i] == '.' || chars[i] == ',') ) ) )  {
		                  e.doit = false;
		                  return;
		                }
		              }
	            }	            
	            e.doit = true;              
            }
          });              
        
        onResize();
        
        shell.addListener(SWT.Resize, new Listener() { 	
		    @Override
		    public void handleEvent(Event event){	       
		       
		       packControls.setSize( shell.getClientArea().width, shell.getClientArea().height );
		       onResize();		       
		     }
		 });
		
    }
    
    protected void onResize() {
    	 tintaControlPos posPack = packControls.getPosition(0);
         mText.setSize((int) posPack.mSize.mx, (int) posPack.mSize.my);
         mText.setLocation((int) posPack.mPosition.mx, (int) posPack.mPosition.my);
         posPack = packControls.getPosition(1);
 		mBtnOk.setSize((int) posPack.mSize.mx, (int) posPack.mSize.my);
 		mBtnOk.setLocation((int) posPack.mPosition.mx, (int) posPack.mPosition.my);
 		posPack = packControls.getPosition(2);
 		mBtnCancel.setSize((int) posPack.mSize.mx, (int) posPack.mSize.my);
 		mBtnCancel.setLocation((int) posPack.mPosition.mx, (int) posPack.mPosition.my);
	}
        
    public void showDlg(Display display ){
    	
	 shell.open();	 
	 while ( !shell.isDisposed() ) {
	     if ( !display.readAndDispatch() ) {
	         display.sleep();
	     }
	 }
    }
}
