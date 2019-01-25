package com.tinta.common;

/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

import java.util.ArrayList;

/*
 * Observer pattern class
 * Example:
 * for( tintaNotify n = obs.getFirst(); n != null; n = obs.getNext() ){
			// call interface to notify
	} 
 * 
 * */
public class tintaObserver<T> {
	
	
	public void add( T observed ){
		mObserved.add(observed);		
	}
	
	public void remove( T observed ){		
		mObserved.remove( observed );		
	}
	
	public T getFirst(){
		
		if( mObserved.size() == 0 )
			return null;
		mIndex = 0;
		return mObserved.get( mIndex );
	}
	
	public T getNext() {
		if( mObserved.size() == 0
					|| ++mIndex >=  mObserved.size() )
			return null;
		
		return mObserved.get( mIndex );
	}
	
	private ArrayList<T> mObserved = new ArrayList<T>();
	
	private int mIndex = -1;
}
