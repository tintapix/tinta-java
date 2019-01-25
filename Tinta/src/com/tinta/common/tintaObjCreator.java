/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

import java.util.Map;
import java.util.TreeMap;

public class tintaObjCreator< TFactory, TObject> {
	protected Map<String, TFactory> mFactories = new TreeMap <String, TFactory>();
	
	
	public boolean registerFactory( TFactory objFact ){

        if ( objFact == null )
            return false;
        String name = ((tintaINamed) objFact).getName();
        
        TFactory o = mFactories.get( name );
        if( o != null )
        	return false;
        
        mFactories.put( name, objFact );
        return true;
    }
	
	public TObject createObj( String name, Class<TObject> param){
		TFactory f  = mFactories.get( name );
        //CoreAssert( it != mObjectsFact.end(),"it != mObjectsFact.end()" );
        if ( f == null )
            return null;
        try {
        	return param.newInstance();
        }
        catch( Exception e ){
        	return null;
        }
               
    }

}
