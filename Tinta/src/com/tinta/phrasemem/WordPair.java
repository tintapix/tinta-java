/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;

public final class WordPair {

    WordPair(String first,  String second){
	mFirstWord = first;
	mSecondWord = second;
    }
    
    WordPair(){
	mFirstWord = "";
	mSecondWord = "";
    }
    
    String mFirstWord;
    String mSecondWord;
}
