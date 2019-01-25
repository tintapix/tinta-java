
/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

public interface tintaIFactory < T > extends tintaINamed {
	T createInstance(); 
}
