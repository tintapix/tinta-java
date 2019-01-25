/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;


/*
 * Singleton debug class
 */
public class tintaDebug {
	
	private tintaDebug(){}
	
	/**
	 * Logger lazy instantiation 
	 * @return
	 */
	public static tintaILogger getLogger(){
		if( mLog != null )			 
			return mLog;
		
		// basic logger
		mLog = new tintaOutLog();
		
		return mLog;
	}
	
	public static void setLogger( tintaILogger log ){		
		mLog = log;
	}
	
	public static tintaILogger mLog = null;	
}
