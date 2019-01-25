package com.tinta.tintaLuaApp;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.tinta.common.lua.tintaLua;
import com.tinta.tintaLuaApp.tintaLuaConsole.ScriptData;

public class tintaLuaThread implements Runnable {
		
	private tintaLua mLua;
	
	private ScriptData mScript = null;
	
	private boolean mIsFile = false;;
	
	private tintaIExecuted mFinishCallback = null;
	
	public final Lock lock  = new ReentrantLock();
	
	public final Condition condition = lock.newCondition();
	
	private boolean mActive = true; 
	
	public tintaLuaThread() {
		
	}
	
	public void release(){
		mLua = null;
		mActive = false;
		
		lock.lock();		
		try
		{			
			condition.signal();
		}		
		finally{
			lock.unlock();			
		}
		
	}
	public void setExecutor( tintaLua lua, tintaIExecuted onFinish){
		
		mLua = lua;		
		mFinishCallback = onFinish;	
		
	}
	
	public void execute( tintaLuaConsole.ScriptData script, boolean isFile){
		
		mScript = script;
		mIsFile = isFile;		
		
		lock.lock();			
		try
		{			
			condition.signal();
		}		
		finally{
			lock.unlock();				
		}
	}
	
	public tintaLuaThread( tintaLua lua, tintaLuaConsole.ScriptData script, boolean isFile, tintaIExecuted onFinish ) {
		mLua = lua;
		mScript = script;
		mIsFile = isFile;	
		mFinishCallback = onFinish; 
		
	}
	
	public tintaLuaThread( tintaLua lua, tintaIExecuted onFinish ) {
		mLua = lua;		
		mFinishCallback = onFinish;
		
	}
	
	public void run() {
		
		while( mActive ) {
						
			lock.lock();
			
			try
			{
				if( mScript == null ) // no job
					condition.await();			
				
				if( mLua != null && mScript != null 
							&& mScript.mBuffer.length() > 0 ){
							
					if( mScript.mData != null ){
						
						mLua.bindData(mScript.mData);
					}
					
					if( mIsFile ) {
						//System.out.print("lua.executeFile");
						mLua.executeFile( mScript.mBuffer );
					}
					else
						mLua.executeBuffer( mScript.mBuffer );
		    	
					if( mScript.mData != null ){						
						mLua.bindData(null);				
					}
					
					mScript = null;		
					
					if( mFinishCallback != null )
						mFinishCallback.onFinish();
				}			
			}
			catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}
			finally{
				lock.unlock();
				//System.out.print("unlock()");
			}
		}	
			
    }
}
