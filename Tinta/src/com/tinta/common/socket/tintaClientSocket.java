package com.tinta.common.socket;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.tinta.common.tintaILogger;
import com.tinta.common.tintaILogger.molyMsgLevel;
import com.tinta.common.tintaDebug;


/**
 * Socket client class( remote clients connecting to the local host(server) and local clients connecting to the remote hosts)
 * @author Mikhail Evdokimov
 *
 */
public class tintaClientSocket {
	
	
	
	public static final int BUFFER_SIZE  = 1024;
	
	public interface IReceiveHandler{
		public void onReceive(final byte[] data);
	}
	
	protected interface ICloseClientHandler{
		public void onClose( tintaClientSocket client );
	}
	 
	public void setCloseHanler(ICloseClientHandler closeHandler){
		mCloseHandler = closeHandler;
	}
	
	public tintaClientSocket( String host , int port ){		
		
		create(host, port);
		
	}
	
	public tintaClientSocket( Socket socket ){	
		
		create(socket);
	}
	
	public tintaClientSocket(){
		mSocket = new Socket();
	}
	
	/**
	 * Add on receive data handler
	 * @param receiver
	 */
	public void addReceiver(IReceiveHandler receiver){
		if( receiver != null && !mReceivers.contains(receiver) ){			
			mReceivers.add( receiver );
		}
	}
	
	
	/**
	 * Remove on receive data handler
	 * @param receiver
	 */
	public void removeReceiver(IReceiveHandler receiver){
		mReceivers.remove(receiver);
	}	

	public void close( ){
		
		mReceivers.clear();
		mActive = false;
		
		try {
			if( mSocket != null && !mSocket.isClosed() ){									
				mSocket.close();
			}
		}catch ( IOException ex ) {
			
			tintaILogger log = tintaDebug.getLogger();
			
			if( log != null )
				log.logMsg(ex.getMessage(), molyMsgLevel.MSG_EXCEPTION);
		}		
	}
	
	
	/**
	 * Construct from socket object
	 * @param receiver
	 */	
	public void create( Socket socket ){		
		
		if( mSocket != null && !mSocket.isClosed() )
			close( );		
		
		mActive = true;
		
		mSocket = socket;
		Runnable reader = new Runnable()
		{		
			// start thread for client 
			public void run()
			{				
				while( mActive ){					
									 
					 byte[] buff = new byte[BUFFER_SIZE];					 
					 byte[] buffAll = new byte[0];	
					 
					 try {
						 
						 int n = -1;
						 while( ( n = mSocket.getInputStream().read( buff, 0, BUFFER_SIZE )) > -1 ){
														
						   int oldLen = buffAll.length;
						   buffAll = new byte[oldLen + n];						   
						   System.arraycopy( buff, 0, buffAll, oldLen, n );
						   
						   if( mSocket.getInputStream().available() == 0 ){
							   
							   for( IReceiveHandler rh : mReceivers)
								   rh.onReceive(buffAll);
							   
							   buffAll = new byte[0];	
						   }						   						   					   
						 }						 
						 close();
						 if( mCloseHandler != null )
							 mCloseHandler.onClose(tintaClientSocket.this);
					 }
					 catch(IOException ex){
						 if( mActive ){
							 tintaILogger log = tintaDebug.getLogger();
							
							 if( log != null )
								log.logMsg(ex.getMessage(), molyMsgLevel.MSG_EXCEPTION);
							 
							 close();
							 
						 }
					 }					
				}				
			}
		};	
		mReader = new Thread(reader);
		mReader.start();	
		
	}
	/**
	 * Construct from parameters
	 * @param host -  host string e.g. "192.168.0.1"
	 * @param port - e.g. 5001
	 */
	public void create( String host , int port ) {
		
		try {			
			
			Socket socket = new Socket(host, port);
			create( socket );	
			mHost = host;
			mPort = port;
			
		} catch ( IOException ex ) {
			
			tintaILogger log = tintaDebug.getLogger();
			
			if( log != null )
				log.logMsg( ex.getMessage(), molyMsgLevel.MSG_EXCEPTION );			
			
		}		
	}
	
	/**
	 * Send data from this client
	 * @param array -  data 
	 * @throws IOException
	 */
	public void sendData(byte[] array)  throws IOException{
		try {
			if( mSocket != null &&  mSocket.isConnected() ) {
				mSocket.getOutputStream().write(array);
				mSocket.getOutputStream().flush();
			}
		}
		catch ( IOException ex ) {
			tintaILogger log = tintaDebug.getLogger();
			
			if( log != null )
				log.logMsg(ex.getMessage(), molyMsgLevel.MSG_EXCEPTION);
			
			throw new IOException(ex);
		}
	}
	
	/**
	 * Is client socket connected 
	 * @return
	 */
	public boolean connected(){
		return mSocket != null && mSocket.isConnected();
	}
	
	
	/**
	 * Is client socket closed 
	 * @return
	 */
	public boolean closed(){
		return mSocket == null || mSocket.isClosed();
	}
		
	public String getHost(){
		return mHost;
	}
	
	public int getPort(){
		return mPort;
	}

	private String mHost = new String();
	
	private int mPort = -1;
	
	private Socket mSocket = null;
		
	private boolean mActive = false;
	
	private Thread mReader = null;
	
	// On close socket client callback 
	private ICloseClientHandler mCloseHandler = null;
	
	// receive listeners
	private List<IReceiveHandler> mReceivers = new ArrayList<IReceiveHandler>();
}

