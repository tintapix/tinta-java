/**
 * Copyright (C) 2011 - 2016 Tinta Molygon
 * molygon.com
 * tintapix@gmail.com
 */

package com.tinta.build;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.tinta.common.*;

public class tintaDesctopLog implements tintaILogger {
	
	tintaDesctopLog() {
		try {
			mFileHadler = new FileHandler(tintaBuild.mLogName);
			mLog.addHandler(mFileHadler);
			SimpleFormatter formatter = new SimpleFormatter();
			mFileHadler.setFormatter(formatter);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addOutput(tintaIOnMsg out) {
		
	}
	
	public void removeOutput(tintaIOnMsg out){
		
	}

	private final Logger mLog = Logger.getLogger( tintaBuild.mLogName );
	private FileHandler mFileHadler;

	// java.util.logging.FileHandler.pattern = application_log.txt

	public void logMsg(String msg, molyMsgLevel level) {
		mLog.info(msg);
	}
	
	public void logMsg(String msg ){
		mLog.info(msg);
	}
}
