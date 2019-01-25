/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.tintaLuaApp;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.custom.StyledText;

import com.tinta.common.*;
import com.tinta.common.tintatreeconfig.*;
import com.tinta.common.tintatreeconfig.molyConfNode.TokenType;
import com.tinta.tintaLuaApp.tintaParamSWT;
import com.tinta.tintaLuaApp.tintaStringParam;
import com.tinta.windows.tintaPosition;
import com.tinta.windows.swt.tintaSWTWindow;

import java.util.Timer;
import java.util.TimerTask;

public abstract class tintaConsoleSWT extends tintaSWTWindow {

	
	private class TextData {
		TextData(){}
		TextData(String text , boolean changed){ 
			this.text = text;
			this.changed = changed;
		}
		String text = new String();
		boolean changed = false;
	};
	
	//private static final String widthName = "width";
	//private static final String heightName = "height";
	private static final String sizeOutName = "size_output";
	private static final String sizeInputName = "size_input";
	private static final String sizeFilesName = "size_files";
	private static final String dateStampName = "date_stamp";
	private static final String consolePosition = "console_position";
	private static final String filesPosition = "files_position";
	private static final String textPosition = "text_position";
	private static final String btnExecutePosition = "btn_execute_position";
	private static final String btnTimerPosition = "btn_timer_position";
	private static final String btnClearPosition = "btn_clear_position";
	private static final String btnSavePosition = "btn_save_position";
	
	private static final String mDefault = new String("Default");
	
	protected String mTimerExecute = new String(); 
	
	private boolean mDateStamp = true;	 
	private int mPrevSelected  = -1;
	
	protected static final  int mWinWidth = 600;
	protected static final int mWinHeight = 600;
	//protected Shell mShell = null;
	protected Button execute = null; // new Button( mShell, SWT.NONE );
	protected Button timer = null; // new Button( mShell, SWT.NONE );
	protected Button clear = null; // new Button( mShell, SWT.NONE );
	protected Button save = null; 
	
	protected List consoleOut = null; // new Text(mShell, SWT.SINGLE |
										// SWT.BORDER);
	protected List savedCommands = null;	
	
	protected StyledText consoleIn = null; // new Text(mShell, SWT.SINGLE |	
										// SWT.BORDER); 
	
	protected tintaGridPack packControls = null;
	protected String mFileEx = new String();  
	protected boolean infoMode = false;
	private ReentrantLock mLockQueue = new ReentrantLock();

	//private Timer mTimer = new Timer(true);
	
	private Map<String,TextData> mFileData = new HashMap<String,TextData>();
	
	private java.util.List<String> mToLog = new java.util.ArrayList<String>(); 

	private molyTreeConfig mConf = new molyTreeConfig( "config.cfg", true );
	
	private void updateFileList() {
		String fileDir = new File("").getAbsolutePath();		
		Vector<File> files = new Vector<File>(); 
		try {		    
		    files =  com.tinta.common.tintaUtilSystem.fillFiles(fileDir, this.mFileEx );
		}
		catch(IOException e){
		}
		if( files != null ){
		    for (File f : files){
		    	String key = f.toString();
		    	// adds only new files
		    	String [] listFiles = this.savedCommands.getItems();
		    	boolean found = false;
		    	for ( String v :  listFiles )
		    	{
		    		if( v.equals(key) ){
		    			found = true;
		    			break;
		    		}
		    	}
		    	if( mFileData.get(key) == null && !found  )
		    		this.savedCommands.add( key ); 		        
		    }
		}
	}
	
	
	protected void onResize() {
		tintaControlPos pos = packControls.getPosition(0);
		consoleOut.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		consoleOut.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		pos = packControls.getPosition(1);
		consoleIn.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		consoleIn.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		pos = packControls.getPosition(2);
		execute.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		execute.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		
		pos = packControls.getPosition(3);
		timer.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		timer.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		
		pos = packControls.getPosition(4);
		clear.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		clear.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		
		pos = packControls.getPosition(5);
		save.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		save.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		
		pos = packControls.getPosition(6);
		savedCommands.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		savedCommands.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
	}

	abstract public void onExecute( String buffer );
	

	public tintaConsoleSWT() {		
	}

	public tintaConsoleSWT( Display shell, boolean infoMode, String fileExtension  ) {
		
		super( "Console", new tintaPosition(300,300), new tintaPosition( mWinWidth, mWinHeight ), shell, -1 );		
		
					
		
		packControls = new tintaGridPack(mWinWidth,
				mWinHeight, 7, 9);
		
		
		
		mFileEx = fileExtension;
		
		int fontInput  = 9;
		int fontOutput = 9;
		int fontFiles  = 9;
		
				    
		try {
			mConf.parse();
			
			molyConfNode node = mConf.getNode(dateStampName);
			if( node != null && node.getType() == TokenType.enBoolean)
				mDateStamp = mConf.getValBoolean(dateStampName);
			
			node = mConf.getNode(sizeOutName);
			if( node != null && node.getType() == TokenType.enNumber)
				fontOutput = (int)mConf.getValDouble(sizeOutName);
			
			node = mConf.getNode(sizeInputName);
			if( node != null && node.getType() == TokenType.enNumber)
				fontInput = (int)mConf.getValDouble(sizeInputName);
			
			node = mConf.getNode(sizeFilesName);
			if( node != null && node.getType() == TokenType.enNumber)
				fontFiles = (int)mConf.getValDouble(sizeFilesName);
			
			
			node = mConf.getNode(sizeInputName);
			if( node != null && node.getType() == TokenType.enNumber)
				fontInput = (int)mConf.getValDouble(sizeInputName);
			
			node = mConf.getNode(sizeFilesName);
			if( node != null && node.getType() == TokenType.enNumber)
				fontFiles = (int)mConf.getValDouble(sizeFilesName);
			
			node = mConf.getNode(consolePosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(consolePosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(consolePosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(consolePosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(consolePosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			node = mConf.getNode(textPosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(textPosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(textPosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(textPosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(textPosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			node = mConf.getNode(btnExecutePosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(btnExecutePosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(btnExecutePosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(btnExecutePosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(btnExecutePosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			node = mConf.getNode(btnTimerPosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(btnTimerPosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(btnTimerPosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(btnTimerPosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(btnTimerPosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			node = mConf.getNode(btnClearPosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(btnClearPosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(btnClearPosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(btnClearPosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(btnClearPosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			node = mConf.getNode(btnSavePosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(btnSavePosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(btnSavePosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(btnSavePosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(btnSavePosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			node = mConf.getNode(filesPosition);
			if( node != null ){
				int xPos = (int)mConf.getValDouble(filesPosition + ".[0]");
				
				int yPos = (int)mConf.getValDouble(filesPosition + ".[1]");
				
				int xCells = (int)mConf.getValDouble(filesPosition + ".[2]");
				
				int yCells = (int)mConf.getValDouble(filesPosition + ".[3]");	
				
				packControls.add(xPos, yPos, xCells, yCells);
				
			}
			
			
			// lines 8
			// columns 3
			// info console
			//packControls.add(0, 0, 7, 3);
			// text edit execute
			//packControls.add(0, 3, 3, 6);
			// button execute
			//packControls.add(3, 3, 1, 1);
			// button timer
			//packControls.add(4, 3, 1, 1);
			// button clear
			//packControls.add(5, 3, 1, 1);
			
			// button save
			//packControls.add(6, 3, 1, 1);
			
			// list commands
			//packControls.add(3, 4, 4, 5);
			
		}
		catch( molyConfigException ex ){
			System.out.print(ex.getMessage());
		}
		
		this.infoMode = infoMode;

		this.execute = new Button(getShell(), SWT.NONE  );
		
		setFontSize(execute,12);	
		
		this.timer = new Button(getShell(), SWT.NONE  );
		
		
		
		setFontSize(timer,12);	 
	    
		this.consoleOut = new List( getShell(), SWT.MULTI  | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.RESIZE );
		
		/*StyledText text = new StyledText(this.getShell(), SWT.BORDER);
		
		StyleRange  style1 = new StyleRange ();
		style1.foreground = this.getDisplay().getSystemColor(SWT.COLOR_RED);
		
		text.setStyleRange(style1);
		*/
		
		
		this.savedCommands = new List( getShell(), SWT.SINGLE | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.RESIZE );
		
		
		mFileData.put(mDefault, new TextData());
		mPrevSelected = 0;
		
		this.savedCommands.addSelectionListener( new SelectionListener() {
		      public void widgetSelected( SelectionEvent event ) {
		    	  
		    	
		    	updateFileList();
		        int[] selectedItems = savedCommands.getSelectionIndices();		        
		        if( selectedItems.length > 0 && mPrevSelected != selectedItems[0] ) { 			        	
		        		        	
		        	String selection = savedCommands.getSelection()[0];
		        	     	
		        	
		        	if( mPrevSelected != -1 ){
		        		String prev = savedCommands.getItem(mPrevSelected);		        	
		        		mFileData.put( prev , new TextData (consoleIn.getText(), false ) );
		        	}
		        	
		        	TextData data = mFileData.get(selection);	   
		        	
		        	if( !selection.equals( mDefault ) && data == null ){
		        		
	        			File f = new File( selection );
	        			if( f.exists() && !f.isDirectory() ){	        				
	        				//tintaUtilSystem.EncodTypes type = tintaUtilSystem.getFileEncoding(selection);
	        				data = new TextData(tintaUtilSystem.tintaReadFile( selection ),false);
		        			if( data != null )
		        				mFileData.put( selection, data );			        					        					        		
	        			}	        				
		        	}	
		        	
        			if( data != null ) {
	        			consoleIn.setText( data.text );
	        			mPrevSelected = selectedItems[0];
	        		}	
		        }		       
		      }
		      public void widgetDefaultSelected(SelectionEvent event) {		      
		      }
		});
				
		this.savedCommands.addMouseListener(new MouseListener() {
	            	 
	            public void mouseUp(MouseEvent e) {
	            }
	 
	            public void mouseDoubleClick(MouseEvent e) {
	                
	            	int[] selectedItems = savedCommands.getSelectionIndices();
	            	String selection = savedCommands.getSelection()[0];
		        	String data = mFileData.get(selection).text;	  
		        	// rewrite 
	            	if( !selection.equals( mDefault ) ){
	            		
	        			File f = new File( selection );
	        			
	        			if( f.exists() && !f.isDirectory() ){
	        				
	        				//tintaUtilSystem.EncodTypes type = tintaUtilSystem.getFileEncoding( selection );
		        			data = tintaUtilSystem.tintaReadFile( selection );
		        			if( data != null )
		        				mFileData.put( selection, new TextData(data,false) );			        					        					        		
	        			}	
	        			
	        			if( data != null ) {
		        			consoleIn.setText( data );
		        			mPrevSelected = selectedItems[0];
		        		}	
		        	}	
	            }
	 
	            public void mouseDown(MouseEvent e) {
	            }
	        });
		
		setFontSize( savedCommands,fontFiles );
		this.savedCommands.add( mDefault );
		
		updateFileList();

		clear = new Button( getShell(), SWT.NONE );
		clear.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				consoleOut.removeAll();
			}
		} );
		
		save = new Button( getShell(), SWT.NONE );		
		save.addMouseListener( new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				
				
				
				int[] selectedItems = savedCommands.getSelectionIndices();
				if (selectedItems.length == 0  )
					return;
				
            	String selection = savedCommands.getSelection()[0];
	        	//String data = mFileData.get(selection);	  
	        	// rewrite 
            	//String toSave = selection;
            	
            	tintaStringParam param = new tintaStringParam(tintaStringParam.ParamType.typeString);
            	if( selection.equals( mDefault ) )
            		param.mValue = "text.lua";
            	else
            		param.mValue = selection;
            	
					tintaParamSWT paramGet = new tintaParamSWT( getShell(), SWT.PRIMARY_MODAL | SWT.RESIZE | SWT.SHEET, "Select file name", 
							new Point(getShell().getLocation().x + getShell().getSize().x/3, getShell().getLocation().y + getShell().getSize().y/3),
							new Point(getShell().getSize().x/2, getShell().getSize().y/5), param ) ;
										
					paramGet.showDlg( getShell().getDisplay() );	
					
            	if( param.mValue.length() > 0 ){
            		
        			File f = new File( param.mValue );
        			
        			if( !f.isDirectory() ){       				
		        	
    		        	mFileData.put( param.mValue , new TextData(consoleIn.getText(), false ) );    		        	
        				
        				ArrayList<String> dataWrite = new ArrayList<String>();        				
        				dataWrite.add( mFileData.get( param.mValue ).text );
        				tintaUtilSystem.tintaWriteFile( param.mValue, tintaUtilSystem.EncodTypes.UTF8, dataWrite ); 
        				
        				updateFileList();
        			}       			
	        	}         	
            	
			}
		} );
		
		save.setText("Save");
		setFontSize(save,12);
		
		setFontSize( consoleOut,fontOutput );
		
		consoleIn = new StyledText(getShell(), SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL );
		consoleIn.addKeyListener( new KeyListener() {		      
			      public void keyPressed( KeyEvent e ) {
			        if( e.keyCode == SWT.F5 )
			        	onExecute(consoleIn.getText());
			      }
			      public void keyReleased( KeyEvent e ) {}
			}
		);
		
		consoleIn.addExtendedModifyListener(new ExtendedModifyListener() {
		      public void modifyText( ExtendedModifyEvent e ) {		    	  
		    	  consoleIn.redraw();
		      }
		    });
		
		consoleIn.addLineStyleListener(new LineStyleListener()
		{
		    public void lineGetStyle( LineStyleEvent e )
		    {
		        //Set the line number
		        e.bulletIndex = consoleIn.getLineAtOffset( e.lineOffset );
		        
		        StyleRange style = new StyleRange();
		        style.metrics = new GlyphMetrics(0, 0, Integer.toString( consoleIn.getLineCount() + 1 ).length() * 14);
		        style.foreground = getDisplay().getSystemColor(SWT.COLOR_GRAY);
		        //Create and set the bullet
		        e.bullet = new Bullet( ST.BULLET_NUMBER,style);
		    }
		});
		consoleOut.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				
				
				// after added
			}
		});		
		
		setFontSize( consoleIn, fontInput );

		execute.setText( "Execute" );
		clear.setText( "Clear" );
		
		setFontSize(clear,12);

		onResize();

		getShell().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				int w = getShell().getClientArea().width;
				int h = getShell().getClientArea().height;				
			
				packControls.setSize( w, h );				
				onResize();
			}
		});

		execute.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				onExecute( consoleIn.getText() );
			}
		});
		
		this.timer.setText("Timer");
		

		if (infoMode) {
			consoleIn.setEnabled(false);
			execute.setEnabled(false);
		}
			
		
		getShell().pack();
		getShell().open();
		
		getShell().addListener(SWT.Close, new Listener() {
		      public void handleEvent( Event event ) {
		        //System.out.println("cose");
		    	 // mConf.setValue(request, val, bForce)
		      }
		    });
	}

	public tintaConsoleSWT(Display shell) {
		this(shell, false,"");
	}

	public void updateQueue(){
		mLockQueue.lock();
		try {
			Iterator<String> it = mToLog.iterator();
	        while (it.hasNext()) {
	        	consoleOut.deselect(consoleOut.getItemCount() - 1);
	        	consoleOut.add(it.next());	        	
	        	consoleOut.select( consoleOut.getItemCount() - 1 );
	        	consoleOut.showSelection();
	        	consoleOut.deselect( consoleOut.getItemCount() - 1 );	        	
	            it.remove();	            
	        }
			
		}
		finally {
			mLockQueue.unlock();
		}
	}
	
	private void addInQueue( String msg ){
		mLockQueue.lock();
		try {
			mToLog.add( msg );
		}
		finally {
			mLockQueue.unlock();
		}
	}
	
	public void logMsg(String msg) {
		
			StringBuffer buf = new StringBuffer();
			
			if( this.mDateStamp == true )
				buf.append( tintaOutLog.createPrefix( tintaILogger.molyMsgLevel.MSG_INFO, true ) );
			
			buf.append( msg );
			//consoleOut.add(buf.toString(), 0);
			addInQueue( buf.toString() );		
	}

	
	//public setPosition();
	
	
}
