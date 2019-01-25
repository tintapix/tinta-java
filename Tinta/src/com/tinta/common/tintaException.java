/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

/**
 * * Common class for tinta exceptions
 */
public class tintaException extends Exception {

	static final long serialVersionUID = -3387516993124229948L;

	tintaException(String msg) {
		super(msg);
	}

	void printMsg() {
		tintaILogger l = tintaDebug.getLogger();
		if (l != null)
			l.logMsg(super.getLocalizedMessage(),
					tintaILogger.molyMsgLevel.MSG_EXCEPTION);
		else
			System.out.print(super.getLocalizedMessage());
		super.getLocalizedMessage();
	}
}
