/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.tintaLuaApp;

import org.eclipse.swt.widgets.Display;

import com.tinta.common.tintaILogger;
import com.tinta.common.tintaDebug;


public class tintaLuaMain {	
	
	public static void main( String[] args ) {					
		
		Display display = new Display();
		
		tintaLuaConsole console = new tintaLuaConsole( display, false );
		
		tintaILogger log = tintaDebug.getLogger();	
		
		if( log != null )
			log.addOutput(console);
				
		while ( !console.getShell().isDisposed() ) {
			console.onUpdateQueue();
			if ( !display.readAndDispatch() ) {
				display.sleep();
			}
		}
		
		console.release();
		
		console.closeInteract();
		
		display.dispose();
    }		
}

