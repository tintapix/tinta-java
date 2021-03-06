/**
 * Copyright (C) 2011 - 2016 Tinta Molygon
 * molygon.com
 * tintapix@gmail.com
 */

package com.tinta.build;

import com.tinta.common.tintaDebug;


public class tintaBuild {
	
	public tintaBuild(){		
		tintaDebug.setLogger( new tintaDesctopLog() );
	}	

	public static final String mLogName    = "log.txt";	
	public static final String mConfigName = "molyConfig.mfg";
	
	public static molyDebugMode mMode = molyDebugMode.DEBUG;
	
	
	/*
	 * Build logging variants  
	 */
	public enum molyDebugMode {		
		DEBUG,
		RELEASE_NO_LOG,
		RELEASE_LOG,
		RELEASE_LOG_EXCEPTION,
	}
}
