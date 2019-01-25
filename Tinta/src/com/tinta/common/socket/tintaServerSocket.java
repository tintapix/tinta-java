package com.tinta.common.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;

import com.tinta.build.tintaBuild;
import com.tinta.build.tintaBuild.molyDebugMode;
import com.tinta.common.tintaILogger;
import com.tinta.common.tintaDebug;
import com.tinta.common.tintaILogger.molyMsgLevel;
import com.tinta.common.tintaIdGen;

/**
 * Socket server class
 * @author Mikhail Evdokimov
 *
 */
public class tintaServerSocket implements tintaSocketSet.tintaIClientAction {
	
	
	
	public tintaServerSocket(){
		try {
			mServer = new ServerSocket();
		}
		catch ( IOException ex ){
			
			tintaILogger log = tintaDebug.getLogger();			
			if( log != null )
				log.logMsg( ex.getMessage(), molyMsgLevel.MSG_EXCEPTION );
		}
	}

	
	public tintaServerSocket( int port ){		
		create( port );
		mClients.addClientHandler(this);
	}
	
	/**
	 * Add client actions(on connect, on disconnect) listener
	 * @param h - handler
	 */
	public void addClientHandler(tintaSocketSet.tintaIClientAction h){
		mClients.addClientHandler(h);
	}
	
	/**
	 * Remove client  actions(on connect, on disconnect) listener
	 * @param h - handler
	 */
	public void removeClientHandler(tintaSocketSet.tintaIClientAction h){
		mClients.removeClientHandler(h);
	}
	
	/**
	 * Returns next or first client id or -1    
	 * @param first - if true, returns first client id or -1
	 * @return
	 */
	public long  getNextClient( boolean first){
		return mClients.getNextClient(first).getId();
		
	}
	
	
	/**
	 *  Callback from tintaIClientAction
	 */
	public void clientAdded( long id, tintaClientSocket client ) {
		
		// To all listeners
		for( tintaSocketSet.IReceiveHandler h : mReceivers ){
			addReciveHandler( id, h );
		}
	}
	
	
	/**
	 *  Callback from tintaIClientAction
	 */
	public void clientRemoved( long id ){
		
	}
	
	/**
	 * Add on receive data handler for connected remote client
	 * @param recipient - client id to listen
	 * @param handler
	 */
	public void addReciveHandler( long recipient, tintaSocketSet.IReceiveHandler handler ){
		mClients.addReceiver( recipient, handler );		
	}
	
	
	/**
	 * Remove on receive data handler for connected remote client
	 * @param recipient - client id to listen
	 * @param handler
	 */
	public void removeReciveHandler( long recipient, tintaSocketSet.IReceiveHandler handler ){
		mClients.removeReceiver( recipient, handler );		
	}
	
	/**
	 * Add on receive data handler for server 
	 * @param handler
	 */
	public void addReciveHandler( tintaSocketSet.IReceiveHandler handler ){
		
		tintaSocketSet.SocketObject o = mClients.getNextClient(true);
		
		while( o.getId() != -1 ) {		
			
			mClients.addReceiver( o.getId(), handler );	
			
			o = mClients.getNextClient(false);
		}
		
		if( !mReceivers.contains(handler) ){
			mReceivers.add( handler );
		}
			
	}
	
	
	/**
	 * Remove on receive data handler for server
	 * @param handler
	 */
	public void removeReciveHandler( tintaSocketSet.IReceiveHandler handler ){
		
		tintaSocketSet.SocketObject o = mClients.getNextClient( true );
		
		while( o.getId() != -1 ) {		
			mClients.removeReceiver( o.getId(), handler );			
			o = mClients.getNextClient( false );
		}
		mReceivers.remove(handler);
			
	}
		
	/**
	 * Send data to the remote client
	 * @param recipient - client id
	 * @param data
	 * @return
	 */
	public boolean sendData( long recipient , final byte[] data ) {
		
		if( !closed() )
			return mClients.sendData( recipient, data );
		return false;
	}
	
	
	
	/**
	 * Send data to the all remote client
	 * @param data
	 * @return
	 */
	public boolean sendData( final byte[] data ) {
		
		tintaSocketSet.SocketObject o = mClients.getNextClient( true );
		boolean sent = true;
		
		while( o.getId() != -1 ) {
			sent = sent && mClients.sendData(  o.getId(), data );
			o = mClients.getNextClient( false );
		}
		return sent; 
	}
	
	/**
	 * Set id generator object
	 * @param gen
	 */
	public void setIdGenerator( tintaIdGen gen ){
		mClients.setIdGenerator(gen);
	}
	
	/**
	 * Construct Socket server from parameter
	 * @param port - e.g. 5001
	 */
	public void create( int port ){
		
		mPort = port;
		
		close( );
		try {
			
			mServer = new ServerSocket( port );
			mActive = true;				
			
			Runnable accepct = new Runnable()
			{		
				public void run()
				{				
					while( mActive ){					
										 
						try {
							
							Socket connection = mServer.accept();		
							tintaClientSocket sock = new tintaClientSocket(connection);
							
							long id = mClients.addConnection( sock );
							
							if( id != -1 ){
								
								if( tintaBuild.mMode == molyDebugMode.DEBUG ){
									tintaILogger log = tintaDebug.getLogger();			
									if( log != null )
										log.logMsg( "Client with id " + id + " connected to the server ", molyMsgLevel.MSG_INFO );
								}
								
							}
							
						} catch( SocketException ex ){
							
							
						} catch ( IOException ex ) {
							tintaILogger log = tintaDebug.getLogger();			
							if( log != null )
								log.logMsg( ex.getMessage(), molyMsgLevel.MSG_EXCEPTION );
						}
					}				
				}
			};	
			mAccepter = new Thread(accepct);
			mAccepter.start();			
			
		}
		catch ( IOException ex ){
			
			tintaILogger log = tintaDebug.getLogger();			
			if( log != null )
				log.logMsg( ex.getMessage(), molyMsgLevel.MSG_EXCEPTION );
		}
	}
	
	/**
	 * Plug off client
	 * @param id
	 */
	public void close( long id ){
		mClients.removeConnection(id);
	}
	
	/**
	 * Close Server and disconnect all clients 
	 */
	public void close( ){
		try {
			
			if( !closed() ){
				
				mActive = false;
				
				tintaSocketSet.SocketObject o = mClients.getNextClient( true );			
				
				while( o.getId() != -1 ) {
					close( o.getId() );
					o = mClients.getNextClient( false );
				}
							
				mServer.close();
				
				tintaILogger log = tintaDebug.getLogger();			
				if( log != null )
					log.logMsg( "Server connection closed with port " + mPort );
				
				mPort = -1;					
			}
		}
		catch ( IOException ex ){
			tintaILogger log = tintaDebug.getLogger();			
			if( log != null )
				log.logMsg( ex.getMessage(), molyMsgLevel.MSG_EXCEPTION );
		}
		
	}	

	public boolean closed() {
		return mServer == null || !mActive || mServer.isClosed();
	}
	
	public int clients(){
		return  mClients.clients();
	}
	
	/**
	 * Has server remote client with id
	 * @param id  - client id
	 * @return
	 */
	public boolean hasClient( long id ){
		return mClients.hasClient(id);
	}
	
	private ServerSocket   mServer = null;	
	
	private boolean 	   mActive = false;
	
	private int 		   mPort = -1;
	
	private Thread         mAccepter = null;
	
	private tintaSocketSet mClients = new tintaSocketSet();
	
	// server receiver for all clients new and old
	private HashSet<tintaSocketSet.IReceiveHandler> mReceivers = new HashSet<tintaSocketSet.IReceiveHandler>(); 
}
