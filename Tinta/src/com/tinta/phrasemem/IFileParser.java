/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.phrasemem;

import java.util.*; 

public interface IFileParser {
    
   
    public void ClearData();
    public int ParseFileWords(String path);
    public int ParseFileOffsets(String path);
    public int ParseFileWords(String path, boolean forcePars);
    public int ParseFileOffsets(String path, boolean forcePars);
    
    public boolean IsParsed();
    public String GetParsedPath();
    public int GetWordCount();
    public Vector<WordPair> GetWords();
    public void CopyWords(Vector<WordPair> list);
    public Vector<WordPairOffset> GetOffsets();
    

}