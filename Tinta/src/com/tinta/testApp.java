package com.tinta;

import com.tinta.common.socket.tintaClientSocket;
import com.tinta.common.socket.tintaServerSocket;
import com.tinta.common.socket.tintaSocketSet;
import com.tinta.common.tintaInteract;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.io.*;

/*
 * Написать программу складывающую 2 целых числа любого размера
 * Реализовать через складывание встолбик двух массивов
 * Для упрощения разрядность(длина) чиел должна совпадать
 * учесть что при складывании цифр разрядность может быть больше - случай когда > 9
 * Использовать функцию Integer.parseInt( Character.toString(arg1.charAt(i)) ); для разбора char 
 * System.arraycopy(rezArray, 0, rezArrayFinal, 1, maxLen); - копирование массива в другой
 *   
 */
public class testApp implements  tintaClientSocket.IReceiveHandler {
	
	
	public void onReceive(final byte[] data)
	{
		for(byte b : data ){
			System.out.print(b);
			System.out.print(" ");
		}
		System.out.print("\n");
	}
	

	public static int DEFAULT_PORT = 8001;
	public static final String SERVER = "localhost";
	public static final int PORT = 5001;
	public static final int TIMEOUT = 15000;
	
	public static final int BUFFER_SIZE  = 32;
	
	
	
	public static void main(String[] args) {
	
		
		Lock lock  = new ReentrantLock();
		Condition condition = lock.newCondition();
		
		Runnable r = new  Runnable ()
		{	
			
			public void run(){
				
				lock.lock();
				
				try {	
				
						condition.await();
						
						System.out.println("awaken");
				}
				catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}				
				finally {
					lock.unlock();
				}
			}
			
		};
		
			Thread t = new Thread(r);
			t.start();			
			Scanner in = new Scanner(System.in);
			
			int action = 0;
			
			while( action!= 2 ){			
			   
			    action = in.nextInt();
			    
			    	 
			    	 
			    if( action == 1 ){
			    	
			    	lock.lock();
				    
				    try {
				    	
			    	
			    		condition.signal();					    		
				    }
			    	catch(Exception e){
			    		System.out.println(e.getMessage());
			    	}			    
				    finally{
						lock.unlock();
					}
			    }
			    
			}
	}
	
		
		//tintaSocketSet set = new tintaSocketSet();
		//set.setGenId(Long.MAX_VALUE - 1 );
		//long next = set.genId();
		
		
		int t =10;
	// Client test
	/*
	tintaSocketSet set = new tintaSocketSet();
	
	int size = 3;
	long[] inds = new long[size];
	
	for( int i = 0; i < size; i++ ){
		inds[i] = set.addConnection(SERVER, DEFAULT_PORT); 
	} 
	
	testReceive1 test1 = new testReceive1();
	testReceiver2 test2 = new testReceiver2();
	testReceiver3 test3 = new testReceiver3();
	
	set.addReceiver(inds[0], test1);
	set.addReceiver(inds[0], test2);
	set.addReceiver(inds[0], test3);
	
	set.addReceiver(inds[1], test1);
	set.addReceiver(inds[1], test2);
	set.addReceiver(inds[1], test3);
	
	set.addReceiver(inds[2], test1);
	set.addReceiver(inds[2], test2);
	set.addReceiver(inds[2], test3);
	
	
	Random rand = new  Random();
	Scanner in = new Scanner(System.in);
	int action = 0;
	while( action!= 2 ){
		
		
	    System.out.print("action: ");
	    action = in.nextInt();
	    
	    if( action == 1 ){
	    	
	    	byte [] randarr = new byte[10];
	    	
	    	rand.nextBytes(randarr);
	    	int id = rand.nextInt(2);
	    	tintaClientSocket sock = set.getSocket(inds[id]);	    	
	    	sock.sendData( randarr );
	    	set.removeConnection(inds[id]);
	    }
	    if ( action == 2 )
	    	break;
	    
	}	
	in.close();
	*/
		/*
		tintaInteract intertact = new tintaInteract();
		
		
		long added = intertact.addClient("localhost", 5001);
		added = intertact.addClient("localhost", 5001);
		
		intertact.addServer( 6001);
			
		long i = intertact.getConnectId(0);
		int type = intertact.getType(i);
		i = intertact.getConnectId(1);
		type = intertact.getType(i);
		i =intertact.getConnectId(2);
		type = intertact.getType(i);
		type = intertact.getType(10);
		*/
		
		
		
/*
	Socket socket1 = null;
	Socket socket2 = null;
	
	try {
		
	socket1 = new Socket(SERVER, PORT);
	//socket1.setSoTimeout(TIMEOUT);
	
	socket2 = new Socket(SERVER, PORT);
	
	
	OutputStream out1 = socket1.getOutputStream();
	

	Writer writer = new OutputStreamWriter(out1);
	writer = new BufferedWriter(writer);
	
	writer.write("assss");
	writer.flush();

	
	while( true ){	
				 
		 byte[] buffAll = new byte[0];
		 
		 byte[] buff = new byte[BUFFER_SIZE];
		 int n = -1;
		 
		 while( ( n = socket1.getInputStream().read(buff,0,BUFFER_SIZE)) > -1 ){
		   int oldLen = buffAll.length;
		   buffAll = new byte[oldLen + n];
		   System.arraycopy(buff, 0, buffAll, oldLen, n);		   
		 }	 
	}	
	
	} catch (IOException ex) {
		
		System.err.println(ex);
		}
	finally { // dispose
			if (socket1 != null) {
				try {
						socket1.close();
					}
				catch (IOException ex) {
				
					}	
			}
			
			if (socket2 != null) {
				try {
						socket2.close();
					}
				catch (IOException ex) {
				
					}	
			}
	*/		
		
	
	
		
//	}
	
}