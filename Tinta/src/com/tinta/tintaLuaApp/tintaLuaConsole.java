package com.tinta.tintaLuaApp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import com.tinta.common.tintaILogger;
import com.tinta.common.lua.tintaILuaParam;
import com.tinta.common.lua.tintaLua;
import com.tinta.common.socket.tintaSocketSet;
import com.tinta.common.tintaInteract;
import org.eclipse.swt.widgets.Display;

public class tintaLuaConsole        extends tintaConsoleSWT 
									implements tintaILogger.tintaIOnMsg,
									tintaSocketSet.IReceiveHandler,
									tintaInteract.tintaIInteractAction,
									tintaIExecuted {
	
	private static final String helpText = "----help---\nCore functions:\njava.msg(\"text\",1000,true)\njava.rand(min,max)\n"
			+ "java.srand(seed)\njava.randf()\n"
			+ "java.call(\"java_function_name\", param1, param2, ...)\n----\n"
			+ "Hot keys:\nF5 - execute\n----";
	
	
	private static String findFirstWord( String src ){	
		
		int posLast = src.indexOf(" ");
		if( posLast > -1 )
			return src.substring(0, src.indexOf(" "));
		
		return src;
	}
	
	protected class TimerExecute extends TimerTask  {
		
		public TimerExecute(){			
		}
		
		public TimerExecute( Timer  timer, String data, int period , int count  ){			
			mTimer = timer;
			mData = data;			
			mPeriod = period;
			mCount = count;
		}
		
		public void run()
		{			
			try {				
				mTimersLock.lock();
				
				if( mCount > 0 )
					mCount--;
			
				onExecute( mData );		
			}
			finally{
				mTimersLock.unlock();
			}
		}
		
		private Timer  mTimer = null;
		
		private String mData = new String();
		
		private int    mCount = -1;
		
		private int    mPeriod = 0;
	}
	
	public class ScriptData {
		
		public ScriptData(){			
		}	
		
		public ScriptData(String buffer, byte [] data  ){
			mBuffer = buffer;			
			mData = data;
		}
		
		public  ScriptData(String buffer ){
			mBuffer = buffer;			
			mData = null;
		}
		
		String mBuffer = new String();	
		
		byte [] mData = null;
	}
	
	public void onExecute(String buffer){		
		executeLua( buffer );		
	}		
		
	public void release() {
		
		clearTimers();
		mluaExecutor.release();
		//mLuaThread.interrupt();
		mOnReceive.clear();		
		mOnRemoteConnect.clear();

		
	}
	
	public void closeInteract() {
		if( mInteract != null ){
			mInteract.close();
		}
	}
	
	public void interactAdded( long id ){		
		
		mInteract.addReciveHandler(id, this);
		
		tintaInteract.CONNECTION_TYPE type = mInteract.getType(id);
		
		// someone connected to the server and server has or not handler
		if( type == tintaInteract.CONNECTION_TYPE.CLIENT_REMOTE ){
			long server = mInteract.getServerId(id);
			
			String handler =  mOnReceive.get( server );
			if( handler != null )
				mOnReceive.put(id, handler); // remote client has the handler now
			
			// if server has handler on connect
			String onAdded = mOnRemoteConnect.get( server );
			if( onAdded != null ){							
				executeLua( new ScriptData( onAdded )  );				
			}			
		}
	}
	
	public void interactRemoved( long id ){		
		
		mInteract.removeReciveHandler(id, this);	
		mOnReceive.remove( id );		
		mOnRemoteConnect.remove( id );
		
		Iterator<Map.Entry<String, Integer>> it = mNames.entrySet().iterator();
		
		    while ( it.hasNext() ) {
		    	
		    	Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>)it.next();
		        
		        if( pair.getValue() == id ){
		        	it.remove(); 
		        	break;
		        }		        
		    }
	}
	
	private void clearTimers(){
		try {			
			mTimersLock.lock();
			Iterator<Map.Entry<Long,TimerExecute>> it = mTimers.entrySet().iterator();
			
		    while ( it.hasNext() ) {		    	
		    	
		    	Map.Entry<Long,TimerExecute> pair = (Map.Entry<Long,TimerExecute>)it.next();
		    	pair.getValue().mTimer.cancel();
		    	
		        it.remove(); 	        		        
		    }
		    
			//mTimers.clear();
		}
		finally {
			mTimersLock.unlock();
		}		
	}
	public void onFinish() {
		
		String err =  mLua.error();
		
		if( err != null ){			    
			onMsg( err );
		}
		
		mIsExeciting.set( false );
		
		try {
			
			mTimersLock.lock();			
		
			Iterator<Map.Entry<Long,TimerExecute>> it = mTimers.entrySet().iterator();
			
		    while ( it.hasNext() ) {
		    	
		    	Map.Entry<Long,TimerExecute> pair = (Map.Entry<Long,TimerExecute>)it.next();
		        
		        if( pair.getValue().mCount == 0 ){
		        	pair.getValue().cancel();
		        	
		        	it.remove(); 
		        	break;
		        }		        
		    }
		}
		finally {
			mTimersLock.unlock();
		}
		
		if ( mToExecute.size() > 0 ){
			executeLua( mToExecute.poll() );		
		}
	}
	
	public void onMsg( String msg ){
		try {
			mMsgLock.lock();
			super.logMsg(msg);
		}
		finally {
			mMsgLock.unlock();
		}
	}
	
	public void onUpdateQueue(){
		super.updateQueue();
	}
	
	public tintaLuaConsole( Display shell , boolean infoMode ){
		super( shell, infoMode, "lua" );
		
		mSys.put( "help", new tintaISysCommand() {
			public boolean execute( String [] params ){
				
				String[] parts = helpText.split("\n");
				for(String str : parts )
					onMsg( str );
				
				onMsg( "Java functions:" );
				
				Map<String, tintaILuaParam> javaFunc = mLua.getParams();
				
				javaFunc = mLua.getParams();			
				
				for ( Map.Entry<String, tintaILuaParam> entry : javaFunc.entrySet() )
					onMsg( entry.getValue().getDescription() );
				
				return true;
			}
		}		
		);
		
		mLua.addMsgReceiver( this );	
		mLua.init();	
		mLua.setInteract( mInteract );
		mInteract.addInteractHandler(this);
		mLuaThread.start();
		mluaExecutor.setExecutor( mLua, this );
		
		mLua.registerParam( new tintaILuaParam() {
			
			public String getName(){return "socket.onreceive";}
			
			public String getDescription(){return "socket.onreceive( connection_id, \"buffer or script_name.lua\"), addes handler on data receiving from the socket";};
			
			public int call( tintaLua context ){
				
				if( context.stackSize() != 3 ){	
					
					mLua.setError( "socket.onreceive. Wrong argumets qauntity" );
					return -1;
				}
				
				int idConnect = mLua.getInt( 2 );					
				String toExecute = mLua.getString( 3 );
				
				if( mInteract.hasConnection(idConnect) ){				
					mOnReceive.put((long)idConnect, toExecute);
				}
				else {
					mLua.setError( "socket.onreceive. Wrong connection id: " + idConnect );
					return -1;
				}
				
				return 0;
			}
		}		
		);		
		
		
		mLua.registerParam( new tintaILuaParam() {
			
			public String getName(){return "socket.name";}
			
			public String getDescription(){return "socket.name( name_or_nil, connection_id  ), binds connection id to the specified name and returns id, if id == nil returns id by name";};
			
			public int call( tintaLua context ){
				
				if( context.stackSize() != 2 && context.stackSize() != 3 ){	
					mLua.setError( "socket.name. Wrong argumets qauntity" );
					return -1;
				}				
				
				String name = mLua.getString( 2 );
				
				int idConnect = mLua.getInt( 3 );	
								
					
					if( context.stackSize() == 3 ){
						if( mInteract.hasConnection(idConnect) )
							mNames.put( name, idConnect);
						else{
							mLua.setError( "socket.name. Wrong connection id: "+ idConnect );
							return -1;
						}
					}
					else 
						idConnect =  mNames.get(name);
					
					mLua.pushVal(idConnect);
					
					return 1;			
				
			}
		}		
		);	
		
		
		mLua.registerParam( new tintaILuaParam() {
			
			public String getName(){return "test";}
			
			public String getDescription(){return "socket.name( name_or_nil, connection_id  ), binds connection id to the specified name and returns id, if id == nil returns id by name";};
			
			public int call( tintaLua context ){
				
				
					
					mLua.pushVal(0.23);
					mLua.pushVal(0.456);
					mLua.pushVal(0.1);
					mLua.pushVal(0.189);
					mLua.pushVal(0.9999);
					
					return 5;			
				
			}
		}		
		);	
		
		mLua.registerParam( new tintaILuaParam() {
			
			public String getName(){ return "execute"; }
			
			public String getDescription(){return "execute( data ). Executes lus script buffer or file, if data has file extension";};
			
			public int call( tintaLua context ){
				
				if( context.stackSize() != 2 ){					
					mLua.setError( "execute. Wrong argumets qauntity" );
					return -1;
				}				
				String data = mLua.getString( 2 );
				
				executeLua(data);
					
				return 0;			
				
			}
		}		
		);	
		
		mLua.registerParam( new tintaILuaParam() {
			
			public String getName(){ return "addtimer"; }
			
			public String getDescription(){return "addtimer( data, period, count = -1 ). Adds timer with period and execution count( unlimited times count = 0 ). Data may be a buffer or a file. Returns timer id";};
			
			public int call( tintaLua context ){
				
				if( context.stackSize() != 3 && context.stackSize() != 4 ){
					
					mLua.setError( "addtimer. Wrong argumets qauntity" );
					return -1;
				}			
				
				int count = -1;
				
				if( context.stackSize() == 4 ){
					count = mLua.getInt( 4 );	
				}
				int period =  mLua.getInt( 3 );
				if( period < 1 ){
					
					mLua.setError( "addtimer. Wrong timer period: " + period  );
					return -1;
				}
				
				String data = mLua.getString( 2 );				
				if( data.length() == 0 ){
					mLua.setError( "addtimer. Wrong data size" );
					return -1;
				}
				
				long id = mInteract.getIdGen().genId();
				
				try {		
					
					mTimersLock.lock();
					
					Timer timer = new Timer(true);
					
					TimerExecute execute = new TimerExecute(timer, data, period , count );
					
					timer.schedule( execute , period, period );
				
					mTimers.put( id, execute );		
				
				}
				finally {					
					mTimersLock.unlock();					
				}
				
				mLua.pushVal(id);
				
				return 1;			
				
			}
		}		
		);
		
		
		
		mLua.registerParam( new tintaILuaParam() {
			
			public String getName(){ return "removetimer"; }
			
			public String getDescription(){return "removetimer( timer_id ). Removes timer by id. If id == null or -1 removes all timers";};
			
			public int call( tintaLua context ){
				
				int id = -1;
				
				if( context.stackSize() == 2 ){
					id = mLua.getInt( 2 );				
				}		
					
				try {		
					
					mTimersLock.lock();
					
					if( id != -1 ){
						
						TimerExecute t = mTimers.get(id);
						
						if( t == null ){							
							mLua.setError( "removetimer. Wrong timer id: " + id );			
						}
						else {
							t.mTimer.cancel();
							mTimers.remove( id );
						}				
						
					}
					else
						clearTimers();
				}
				finally {					
					mTimersLock.unlock();					
				}				
				return 0;				
			}
		}		
		);
		
		onMsg( "Type \'help\' for more information" );		
	}		
	
	public void onReceive(long recipient, final byte[] data){
		
		String toExecute = mOnReceive.get(recipient);
		
		if( toExecute != null ){
			executeLua( new ScriptData( toExecute, data )  );
		}
	}
	
	private void executeLua( String text ){	
		executeLua( new ScriptData( text, null ) );
	}
		
	private void executeLua( ScriptData script ){	
		
		if( script.mBuffer.length() == 0 )
			return;
		
		String first =  findFirstWord(script.mBuffer);
		
		tintaISysCommand sys = mSys.get(first);
		
		if( sys != null ){
			
			String[] params = script.mBuffer.split(" ");
			sys.execute( params );
			
			return;
		}	
		
		
		if( !mIsExeciting.get() ){
			
			if( script.mBuffer.length() > 0 ){
				
				boolean isFile = script.mBuffer.length() > 4 
									&& ( script.mBuffer.substring( script.mBuffer.length() - 4 ).equals( ".lua" ) );
				mIsExeciting.set( true );
				
				mluaExecutor.execute( script, isFile );
					
			}
		} else {
			
			if( mToExecute.size() <= maxTaskInQueue )
					mToExecute.add( script );
		}
	}
	
	tintaLuaThread mluaExecutor = new tintaLuaThread();
	
	private Thread mLuaThread = new Thread(mluaExecutor);
	
	//private final Condition condition;
	
	private tintaLua mLua = new tintaLua();
	
	private int maxTaskInQueue = 10;
	
	private Queue <ScriptData> mToExecute = new LinkedList<ScriptData>();
	
	private tintaInteract mInteract =  new tintaInteract();
	
	private Map<Long,String> mOnReceive = new HashMap<Long,String>();
	
	private Map<Long,String> mOnRemoteConnect = new HashMap<Long,String>();
	
	private AtomicBoolean mIsExeciting = new AtomicBoolean(false);
	
	private Map<String, Integer> mNames = new HashMap<String,Integer>();  
	
	private Map<String,tintaISysCommand> mSys = new HashMap<String,tintaISysCommand>();
	
	private ReentrantLock mMsgLock = new ReentrantLock();
	
	private ReentrantLock mTimersLock = new ReentrantLock();
	
	private HashMap<Long,TimerExecute> mTimers = new HashMap<Long,TimerExecute>();
	
}
