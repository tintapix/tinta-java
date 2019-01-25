/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;

import java.io.*;
import java.util.Vector;
import java.util.Collections;


public class TxtWordFileParser extends FileParser {
       
    public int ParseFileWords(String path){	
	if( mParsed )
	    return mRecordsCount;	
	try {
	    
	    
	    ClearData();
	    
	    FileReader fr = new FileReader(path); 
	    BufferedReader text = new BufferedReader(fr);
	    
	     
	    String textBody;	    
	    
	    int indexBegin = 0;
	    int indexEnd = 0;
	   
	    while( (textBody = text.readLine()) != null) {
		 indexBegin = 0;
		 indexEnd = textBody.length() - 1;		
		 String textWords = new String(textBody.getBytes(),"UTF-8");
		 
		 // finding first word
		 indexEnd = textWords.indexOf( mQuote, indexBegin );
		 
		 if(indexEnd == -1)
		     continue;
		 
		 String wordFirst = textWords.substring(indexBegin, indexEnd); //new String(textWords.substring(indexBegin, indexEnd).getBytes(), "UTF-8");
		 
		 
		 indexBegin = indexEnd;
		 
		// System.out.print(wordFirst);
		 
		 indexEnd = textWords.indexOf( mQuote, ++indexBegin );
		 
		 if(indexEnd == -1)		     
		     continue;
		 
		 String wordSecond = textWords.substring(indexBegin, indexEnd); //new String(textWords.substring(indexBegin, indexEnd).getBytes(), "UTF-8");
		 //System.out.print(wordSecond); 
		 mWordsPairs.add(new WordPair(wordFirst, wordSecond));
		 
	    } 	    	    
	    
	    text.close();
	}
	catch(FileNotFoundException e){
	    
	   System.out.print(e.getLocalizedMessage());
	}
	catch(IOException e){
	    System.out.print(e.getLocalizedMessage());	    
	}	
			
	mRecordsCount = mWordsPairs.size();
	mParsed = mRecordsCount > 0;		
	return mRecordsCount;
    }
    
    public int ParseFileOffsets(String path){
	/*
	if( mParsed )
	    return mRecordsCount;	
	try {
	    
	    
	    ClearData();
	    
	    FileReader fr = new FileReader(path); 
	    BufferedReader text = new BufferedReader(fr);
	    
	     
	    //String textBody;	    
	    
	    int indexBegin = 0;
	    int indexEnd   = 0;
	   
	    boolean readModeQuest = true;	    
	    
	    int pos   = 0;
	    int begin = 0;
	    int end   = 0;
	    
	    int c = 0;
	    
	    WordPairOffset offset = new WordPairOffset();
	    while( (c = text.read()) != -1 ) {	
		begin = pos;
		char character = (char) c;
		String stringValueOf = String.valueOf( character );
		
		if( readModeQuest == true && stringValueOf == mQuote ){
		    readModeQuest = false;
		    end = pos - 1;
		    offset.mQuest = begin;
		    offset.mQuestSize = end - begin;
		}
		
		if( readModeQuest == false && stringValueOf == mQuote ){
		    readModeQuest = true;
		    end = pos - 1;
		    
		    offset.mAnswer = begin;
		    offset.mAnswerSize = end - begin;
		    
		    if( offset.mQuest < offset.mAnswer){
			mWordsOffsets.add(offset);
		    }
		    offset = new WordPairOffset();		    
		}
		
		pos++;

	    }  
	    
	    
	    text.close();
	}
	catch(FileNotFoundException e){
	    
	   System.out.print(e.getLocalizedMessage());
	}
	catch(IOException e){
	    System.out.print(e.getLocalizedMessage());	    
	}	
			
	mRecordsCount = mWordsPairs.size();
	mParsed = mRecordsCount > 0;	*/
	
	return mRecordsCount;
	
    }
    public Vector<WordPair> GetWords(){
		
	//list.setSize(mRecordsCount);
	//Collections.copy( list, mWordsPairs);
	return mWordsPairs;
	
    }
    public void CopyWords(Vector<WordPair> list){
	list.setSize(mRecordsCount);
	Collections.copy( list, mWordsPairs);
    }
    
    public Vector<WordPairOffset> GetOffsets() {
	return mWordsOffsets;
    }

}
