/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;

public class WordPairOffset {
    
    
    WordPairOffset(){
	
	mQuest = 0;
	mQuestSize = 0;
	  
	mAnswer = 0;
	mAnswerSize = 0;
    }
    
    WordPairOffset(int quest, int questSize, int answer, int answerSize){
	
	mQuest = quest;
	mQuestSize = questSize;
	  
	mAnswer = answer;
	mAnswerSize = answerSize;
    }


    int mQuest;
    int mQuestSize;
    
    int mAnswer;
    int mAnswerSize;

}
