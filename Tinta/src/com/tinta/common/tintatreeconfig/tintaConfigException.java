/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common.tintatreeconfig;

import com.tinta.common.tintaDebug;
import com.tinta.common.tintaILogger;

public class tintaConfigException extends Exception {
	public tintaConfigException(String text){
		super( text );
		if( tintaDebug.getLogger()!= null )
			tintaDebug.getLogger().logMsg(text,tintaILogger.molyMsgLevel.MSG_EXCEPTION);
		
	}
}
