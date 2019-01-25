/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

/**
 * Logger interface
 * @author Mikhail Evdokimov
 *
 */
public interface tintaILogger {
	
	
	public interface tintaIOnMsg{
		public void onMsg( String msg);
	}
	
	void logMsg(String msg, molyMsgLevel level );
	
	void logMsg(String msg );
	
	void addOutput(tintaIOnMsg out);
	
	void removeOutput(tintaIOnMsg out);
	
	public enum molyMsgLevel {
		
		MSG_INFO,
		MSG_ASSERT,
		MSG_EXCEPTION

	}
}
