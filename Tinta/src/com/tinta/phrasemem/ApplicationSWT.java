/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.tinta.build.tintaBuild;
import com.tinta.common.tintaControlPos;
import com.tinta.common.tintaDebug;
import com.tinta.common.tintaILogger;
import com.tinta.common.tintaException;
import com.tinta.common.tintaGridPack;



public class ApplicationSWT {
    
    enum MsgType{
	Error, 
	Message,
	Warning
	
    }
    
    static tintaBuild mBuild;
    
    static final String mAppName = "MyWords";
    
    static final String mFileText = "Open";
    static final String mLastText = "Last phrase";    
    static final String mExtension = "txt";
    
    static final String btnComFontName = "Arial";
    static final int btnFontSize = 14;
    static final int btnComFontSize = 10;
    	   final String mStrBtnLast = "Last phrase";
    	   final String mStrBtnOpen = "OpenFile";
    	   
    //static final String mConfigName = "config.ini";
    static final String mLogName = "exceptions.log";
        	   
    private int mWinHeight = 400;
    private int mWinWidth  = 400;
    
   // private int mMaxWords = 100;
    static Integer mMaxWordsSampl = 10;   
    
    //private boolean mIsCyclic = true;
    
    protected tintaGridPack packControls = null;
    
    public  Shell mShell;          
    private Button mQuest;
    private Button mAnswer;
    private Button mFile;    
    private configSWT mCfg;    
    List  mListFiles;
    
    TxtWordFileParser mParser  = new TxtWordFileParser();
    
    private String mPathCurrent = new String();
    
    private Vector<File> mFiles;
    
    Vector<WordPair> parsedPairs = new Vector<WordPair>();
    WordPair wordToSolve = new WordPair();  
    
    Vector<WordPair> parsedPairsFinal = new Vector<WordPair>();
    private Random mRand;
    private int mOldIndex = -1;
    
    void updateMsgList(MsgType msgType, String msgText){
	
	String prefix = new String();
	
	switch(msgType){
	    case Error:
		prefix = " Error: "; 
		break;
		
	    case Warning:
		prefix = " Warning: "; 
		break;
			    	
	    case Message:
		prefix = " "; 
		break;		
	};
	
	mShell.setText(mAppName + prefix + msgText);
	
    }
    
    private void fillList(){
	String fileDir = new File("").getAbsolutePath();
	try {
	    //mAbsPath = new File(fileDir);
	    mFiles =  com.tinta.common.tintaUtilSystem.fillFiles(fileDir, mExtension);
	}
	catch(IOException e){
	    updateMsgList(MsgType.Error, e.getMessage());
	}
	
	
	if( mFiles != null ){
	    for (File f : mFiles){
		mListFiles.add( f.toString() ); 
	        
	    }
	}
    }
   
    private int randUniqInt( int min, int max , Vector<Integer> list){
	
	int rez = -1;
	
	if(min < 0 || max <= 0 || min >= max || list.size()-1 != max )
	    return rez;
	
	rez = mRand.nextInt(max) + min;
	mRand.setSeed(mRand.nextInt());
	
	int cycle = 0; 
	while( list.elementAt(rez) == 1){
	    if( cycle == list.size() )
		break;
	    if( rez == list.size() - 1 ){
		rez = 0;		
	    }
	    else
		rez++;
	    
	    cycle++;
	}
		
	return rez;
    }
    private void parseFile(String PathToFile) {	 
	
	 //mParser.ParseFile(PathToFile, true);	 
	 
	 mParser.ParseFileWords(PathToFile, true);
	 
	 parsedPairs = mParser.GetWords();    
		 
	// getting data config
	//mMaxWords = mConfigData.getMaxWords();
		    
		
	//mMaxWordsSampl = 10; //mConfigData.getMaxWordsSampl();
	
	
	
	//if( mMaxWordsSampl > mMaxWords )
	//    mMaxWordsSampl = mMaxWords;
	    
		
	
		
	 if( parsedPairs.isEmpty() ) {
	     blockControls( true );
	     updateMsgList(MsgType.Error , " No one phrase was found on path " + PathToFile );
	 }
	 else {
	     blockControls( false );
	     updateMsgList(MsgType.Message, " Path " + PathToFile + ", phrases parsed: " + parsedPairs.size());	      
	 }     
		 
    }
    
    private void blockControls(boolean bBlockButtons){
	
	mQuest.setEnabled(!bBlockButtons);
	mAnswer.setEnabled(!bBlockButtons);
	
   	
    }
    private void onResize(){
	
   
    	tintaControlPos pos = packControls.getPosition(0);
		
    	mQuest.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
    	mQuest.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		pos = packControls.getPosition(1);
		
		mAnswer.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		mAnswer.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		pos = packControls.getPosition(2);
		mFile.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
		mFile.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
		
		
		
	
	 	pos = packControls.getPosition(3);
    	mListFiles.setSize((int) pos.mSize.mx, (int) pos.mSize.my);
    	mListFiles.setLocation((int) pos.mPosition.mx, (int) pos.mPosition.my);
    }
    
    public ApplicationSWT(Shell parent, int style) throws tintaException {
	
	    mBuild = new tintaBuild();
	    
	    tintaDebug.getLogger().logMsg("Begin executing", tintaILogger.molyMsgLevel.MSG_INFO);    	
		mRand = new Random();
		mRand.setSeed( System.currentTimeMillis() );
		mShell = parent; //new Shell(parent,style);
		
		
		
		mShell.setSize(mWinWidth, mWinHeight);
		
		mShell.setLocation(300, 300);
		//FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		//mShell.setLayout(fillLayout);
		
		packControls = new tintaGridPack(mWinWidth,
				mWinHeight, 1, 6);
		
		mShell.setText(mAppName);
		
		mQuest = new Button( mShell, SWT.NONE );
		mQuest.setText(new String());
		
		mQuest.setFont(new Font(parent.getDisplay(), btnComFontName, btnFontSize, SWT.BOLD ));
		
		
		mAnswer = new Button( mShell, SWT.NONE );
		mAnswer.setFont(new Font(parent.getDisplay(), btnComFontName, btnFontSize, SWT.BOLD ));
		mAnswer.setText( new String() );
		
		
		mFile = new Button( mShell, SWT.NONE );
		mFile.setFont(new Font(parent.getDisplay(), btnComFontName, btnComFontSize , SWT.NORMAL));
		mFile.setText(mStrBtnOpen);
		
		
	
		
		
		mListFiles = new List( mShell, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL );
		mListFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		mListFiles.setFont(new Font(parent.getDisplay(), btnComFontName, btnComFontSize , SWT.NORMAL));
		
		
		packControls.add(0, 0, 1, 2);		
		packControls.add(0, 2, 1, 2);
		packControls.add(0, 4, 1, 1);
		packControls.add(0, 5, 1, 1);
		
		
		
		onResize();
		fillList();	
		
		parent.addListener(SWT.Resize, new Listener() { 	
		    @Override
		    public void handleEvent(Event event){
		       	       
		       mWinHeight = mShell.getClientArea().height;
		       mWinWidth =  mShell.getClientArea().width;	
		       packControls.setSize( mWinWidth, mWinHeight );
		       onResize();	
		       
		     }
		 });	
		
		mQuest.addMouseListener( new MouseAdapter() {	
		    
		    @Override
		    public void mouseDown(MouseEvent e)
		    {
			
				int max = parsedPairsFinal.size() - 1;
				if( max == 0 )
				    return;
				
				Random rn = new Random();		
				int index = rn.nextInt(max);
				
				if( mOldIndex ==  index){
				    
				    if(index == max)
					index = 0;
				    else
					index++;
				    
				}		
				mOldIndex = index;		
				//int pos = mWordsIdices.get(index);
				
				wordToSolve =  parsedPairsFinal.get(index);
				mQuest.setText(wordToSolve.mFirstWord);	
				mAnswer.setText(new String());
		    }	    
		} );
		
		mAnswer.addMouseListener( new MouseAdapter() {	
		    
		    @Override
		    public void mouseDown(MouseEvent e)
		    {
			if(mAnswer.getText().length() == 0 )
			    mAnswer.setText(wordToSolve.mSecondWord);
			else
			    mAnswer.setText(new String());
		    }	    
		} );
		
		
		
		mFile.addMouseListener( new MouseAdapter() {
		    
		    @Override
		    public void mouseDown(MouseEvent e)
		    {
			
			parseFile(mPathCurrent);
			if(parsedPairs.size() == 0)
			    return;
			
			//Integer val = new Integer(0);
			mCfg = new configSWT(mShell, SWT.PRIMARY_MODAL | SWT.SHEET, "Select maximum words", 
										mShell.getLocation(), new Point(mShell.getSize().x, mShell.getSize().y/3), parsedPairs.size() );
			mCfg.showDlg(mShell.getDisplay());
			
			if( mMaxWordsSampl >= parsedPairs.size() )
			    mMaxWordsSampl = parsedPairs.size();
			
			updateMsgList(MsgType.Message, " Path " + mPathCurrent + ", phrases parsed: " + parsedPairs.size() + " in list words: " + mMaxWordsSampl);
			
			parsedPairsFinal.clear();
			mOldIndex = -1;
			
			Vector<Integer> listUniq = new Vector<Integer> ();
			listUniq.setSize(parsedPairs.size());
			Collections.fill(listUniq, 0);
			for( int i = 0; i < mMaxWordsSampl; i++) {
			    int v = randUniqInt( 0, listUniq.size() - 1, listUniq);
			    if(v == -1)
				break;
			    listUniq.setElementAt(1, v);
			    
			    parsedPairsFinal.add(parsedPairs.get(v));
			}	
			parsedPairs.clear();
			
		    }	    
		} );
		
		mListFiles.addSelectionListener(new SelectionListener() {
		      
		    @Override
		    public void widgetSelected( SelectionEvent arg0 ) {
			
			int [] indexSelected =  mListFiles.getSelectionIndices();
			if( indexSelected.length == 0 )
			    return;
			
			
			File toParse  = mFiles.get( indexSelected[0] );		
			mPathCurrent = toParse.getPath();
			
			updateMsgList(MsgType.Message, " Path selected " + mPathCurrent);	
			
		    }
		    @Override
		    public void widgetDefaultSelected(SelectionEvent arg0) {			
		    }
		});
		blockControls(true);
		
		mShell.pack();
		        
		mShell.open();
    }


    public static void main( String[] args ) {
	
        Display display = new Display();
        Shell shell = new Shell(display);
        
       
      try {	       
	  new ApplicationSWT( shell,  SWT.CLOSE | SWT.RESIZE);
      } catch( tintaException e ){
	  
	  ByteArrayOutputStream out = new ByteArrayOutputStream();
	    e.printStackTrace(new PrintStream(out));
	    System.out.println(new String(out.toByteArray()));
	  
      }
        
        while (!shell.isDisposed())
        {
    	if (!display.readAndDispatch())
    	    display.sleep();
        }
        display.dispose();
    }
    
    
 
	
}