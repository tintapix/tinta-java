/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

public class tintaStringFactory 
			implements tintaIFactory<String> {
	
	public String getName() {
		return new String("string");
	}
	public String createInstance(){
		return "";
	}
}
