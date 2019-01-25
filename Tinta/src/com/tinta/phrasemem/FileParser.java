/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;
import java.util.Vector;

public abstract class FileParser implements IFileParser{
    
    FileParser(){
	mParsed = false;
	mRecordsCount = 0;
	mWordsPairs = new Vector<WordPair>();
	mWordsOffsets = new Vector<WordPairOffset>();
    }
    
    protected boolean mParsed;
    protected String mPath;
    protected int mRecordsCount;
    protected Vector<WordPair> 		 mWordsPairs;
    protected Vector<WordPairOffset>     mWordsOffsets;    
    
    static public final String mSpace = " ";
    static public final String mNewLine = "\n";
    static public final String mQuote = "\"";
    static public final String mDot = ".";
    static public final String mFileExtensionTxt = "txt";
        
    public void ClearData(){
	mRecordsCount = 0;
	mWordsPairs.clear();
	mWordsOffsets.clear();
    }
    public boolean IsParsed(){
	return mParsed;	
    }
    
    public String GetParsedPath(){
	return mParsed == true ? mPath : null;
    }
    public int GetWordCount(){
	return mRecordsCount;
    }
    public final  int ParseFileWords(String path, boolean forcePars) {      
	    
	mPath = path;	
	// preventing not parsed variant
	mParsed = mParsed && !forcePars;
	
	mRecordsCount = ParseFileWords( mPath );
	mParsed = mRecordsCount > 0;
	
	return mRecordsCount;
    }
    
    public final  int ParseFileOffsets(String path, boolean forcePars) {      
	    
	mPath = path;	
	// preventing not parsed variant
	mParsed = mParsed && !forcePars;
	
	mRecordsCount = ParseFileOffsets( mPath );
	mParsed = mRecordsCount > 0;
	
	return mRecordsCount;
    }
}
