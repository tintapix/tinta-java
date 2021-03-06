package com.tinta.common.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.tinta.build.tintaBuild;
import com.tinta.build.tintaBuild.molyDebugMode;
import com.tinta.common.tintaILogger;
import com.tinta.common.tintaDebug;
import com.tinta.common.tintaILogger.molyMsgLevel;
import com.tinta.common.tintaIdGen;


/**
 * Socket clients set 
 * @author Mikhail Evdokimov
 *
 */
public class tintaSocketSet  implements tintaClientSocket.ICloseClientHandler {
		
	/**
	 * Interface on receive data handler  
	 * @author Mikhail Evdokimov
	 *
	 */
	public interface IReceiveHandler {
		public void onReceive(long recipient, final byte[] data);
	}
		
	
	/**
	 * Interface for clients actions(connec, disconnect)
	 * @author Mikhail Evdokimov
	 *
	 */
	public interface tintaIClientAction{
		
		public void clientAdded( long id, tintaClientSocket client );		
		
		public void clientRemoved( long id );
	}

	public tintaSocketSet(){
	}
		
	/**
	 * From ICloseClientHandler
	 */
	public void onClose( tintaClientSocket client ){
		removeConnection( client );	
		
	}
	
	/**
	 * Set id generator
	 * @param gen - object
	 */
	public void setIdGenerator( tintaIdGen gen ){
		mIdGen = gen;
	}
	
	/**
	 * Add connection to the set
	 * @param socket
	 * @return id for new connection
	 */
	public long addConnection( tintaClientSocket socket ){		
				
		SocketObject obj = new SocketObject(mIdGen.genId(), socket );	
		
		socket.addReceiver( obj );	
		socket.setCloseHanler( this );
		
		mClients.add( obj );
		
		mIterator = mClients.iterator();
		
		tintaILogger log = tintaDebug.getLogger();			
		if( log != null )
			log.logMsg( "Client connected with id: " + obj.mId, molyMsgLevel.MSG_INFO );
		
		for (tintaIClientAction a : mClientHandlers)
			a.clientAdded(obj.mId, obj.mSocket);
		
		return mIdGen.getId();
	}

	
	/**
	 * Construct and add connection to the set
	 * @param host - socket host ip string e.g. "192.168.0.1"
	 * @param port -  e.g. 5001
	 * @return id for new connection
	 */
	public long addConnection( String host, int port ){		
		
		tintaClientSocket socket = new tintaClientSocket(host,port);	
		
		if( socket.closed() ){
			return -1;
		}
		
		long id  = addConnection( socket );
		
		if( id != -1 ){
			if( tintaBuild.mMode == molyDebugMode.DEBUG ){
				tintaILogger log = tintaDebug.getLogger();			
				if( log != null )
					log.logMsg( "Client created with id: " + mIdGen.getId(), molyMsgLevel.MSG_INFO );
			}	
		}
		return id;
	}
	
	/**
	 * Close and remove connection from set 
	 * @param id - connection id
	 * @return true if client was closed and removed
	 */
	public boolean removeConnection( long id ){
				
		for( SocketObject s : mClients ){
			if(s.mId == id ){						
				remove(s);
				return true;
			}
    	}	
		return false;
	}
	
	/**
	 * Close and remove connection from set 
	 * @param client object
	 * @return true if client was closed and removed
	 */
	public boolean removeConnection( tintaClientSocket client ){
		for( SocketObject s : mClients ){
			if(s.mSocket == client ){						
				remove(s);			
				return true;
			}
    	}	
		return false;
	}
	
	/**
	 * Returns client by id 
	 * @param connection - client id
	 * @return
	 */
	public final tintaClientSocket getSocket(long connection){
		for( SocketObject s : mClients ){
			if(s.mId == connection ){						
				return s.mSocket;
			}
    	}	
		return new tintaClientSocket();
	}
	
	/**
	 * Add on receive data handler
	 * @param recipient - client id
	 * @param receiver - handler
	 */
	public void addReceiver(long recipient, tintaSocketSet.IReceiveHandler receiver){
		if(receiver != null){			
			for( SocketObject s : mClients ){
				if(s.mId == recipient ){
					if( !s.mReceivers.contains(receiver) )				
						s.mReceivers.add(receiver);
					
					break;
				}
	    	}			
		}			
	}
	
	/**
	 * Remove on receive data handler
	 * @param recipient - client id
	 * @param receiver - handler
	 */
	public void removeReceiver(long recipient, tintaSocketSet.IReceiveHandler receiver){
		if( receiver != null ){			
			for( SocketObject s : mClients ){
				if( s.mId == recipient ){
					s.mReceivers.remove(receiver);										
					break;
				}
	    	}			
		}
	}
	
	/**
	 * Returns first,next client object id or empty object
	 * @param first
	 * @return
	 */
	public final SocketObject getNextClient(boolean first) {
		if( first )
			mIterator = mClients.iterator();
		if(mIterator.hasNext())
			return mIterator.next();
		return new SocketObject();
	}
		
	/**
	 * Client object
	 * @author Mikhail Evdokimov
	 *
	 */
	public class SocketObject implements tintaClientSocket.IReceiveHandler {
		
		public SocketObject(){			
		}
		
		public SocketObject(long id, tintaClientSocket socket ){	
			mId = id;
			mSocket = socket;			
		}
		
		public void onReceive(final byte[] data){		
							
			for( tintaSocketSet.IReceiveHandler h :  mReceivers ){
				h.onReceive(mId, data);
			}
		}
		public long getId(){
			return mId;
		}
		
		private long mId = -1;
		private tintaClientSocket mSocket = null;
		private List<tintaSocketSet.IReceiveHandler> mReceivers = new ArrayList<tintaSocketSet.IReceiveHandler>(); 
	}
	
	/**
	 * Add client action handler
	 * @param handler
	 */
	public void addClientHandler(tintaIClientAction handler){
		if(handler != null && !mClientHandlers.contains(handler))
			mClientHandlers.add(handler);
	}
	
	/**
	 * Remove client action handler
	 * @param handler
	 */
	public void removeClientHandler(tintaIClientAction handler){
		if(handler != null && mClientHandlers.contains(handler))
			mClientHandlers.remove(handler);
	}
	
	/**
	 * Send data to the client
	 * @param recipient client id or -1(sends to all clients)
	 * @param data
	 * @return true if data was sent
	 */
	public boolean sendData( long recipient , final byte[] data ) {
		
		boolean send = false;
		
		for( SocketObject s : mClients ){
			
			if( recipient == -1 || s.mId == recipient ) {				
				try {
					
					s.mSocket.sendData( data );	
					send = true;
					
					if( recipient != -1 )
						break;
					
				} catch( IOException ex ) {
					tintaILogger log = tintaDebug.getLogger();
					
					if( log != null )
						log.logMsg(ex.getMessage(), molyMsgLevel.MSG_EXCEPTION);
					
				}
			}
    	}
		return send;
	}
	
	/**
	 * Close and remove all clients
	 */
	public void remove() {		
		for( SocketObject s : mClients ){									
				s.mSocket.close();							
    	}	
		mClients.clear();
		mIterator = mClients.iterator();
	}
	
	
	/**
	 * Close and remove client by object
	 * @param obj - client object
	 */
    private void remove(SocketObject obj){
							
		obj.mSocket.close();
		obj.mReceivers.clear();
			
		for (tintaIClientAction a : mClientHandlers)
			a.clientRemoved(obj.mId);
		
		if( tintaBuild.mMode == molyDebugMode.DEBUG ){
			tintaILogger log = tintaDebug.getLogger();			
			if( log != null )
				log.logMsg( "Client  " + obj.mId + " disconnected ", molyMsgLevel.MSG_INFO );
		}
		
		mClients.remove(obj);
		mIterator = mClients.iterator();		
	}
	
    public int clients(){
    	return mClients.size();
    }
    	
    /**
     * Returns true if client presents in the set
     * @param id - client id
     * @return true if client presents in the set
     */
	public boolean hasClient( long id ) {
		for( SocketObject s : mClients ){
			if(s.mId == id ){
				return true;			
				
			}
    	}		
		return false;
	}
	
	private tintaIdGen  mIdGen = new tintaIdGen(0);	
	
	private Vector<SocketObject> mClients = new Vector<SocketObject>();
	
	private Iterator<SocketObject> mIterator = mClients.iterator();
	
	private List<tintaIClientAction> mClientHandlers = new ArrayList<tintaIClientAction>();
	
	
}
