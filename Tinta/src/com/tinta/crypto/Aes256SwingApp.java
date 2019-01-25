/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.crypto;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.io.*;
import java.nio.ByteBuffer;

import java.nio.charset.Charset;


public class Aes256SwingApp{
	
	static final String mEncDataFormat = "UTF-8";
	static final String mEncKeyFormat = "UTF-8";
	static  final String mAppName = "DataCrypt TintaPix(c)";
    static JFrame aWindow = new JFrame(mAppName);
    
    //static int tailOffset; 
    static JTextArea   mTextArea;
   // static JTextField  mFileText;        
    static JButton mBtnFileChoose;
    static JButton mBtnFileSave;
    
    static JList mListFiles;
    static String mPathCurrent = "";
    static int tailOffset = 0;
    static private File path;
    static private Vector<File> files;     
    static private PasswDialog mPasswDlg;
    static private byte[] mData;
        
    
static void setPasswText(char [] text){	
	
	try{
		tintaCrypto.InitCrypto(text);	
	}
	catch( UnsatisfiedLinkError  e ){
		
		UpdateHeader("Error " + e.getMessage());
		
	}
}

static void UpdateHeader(String errorText){
	
	
	aWindow.setTitle(mAppName + errorText);
	
    }
	
   static String GetFileExtFromPath(String path){
        
		String extension = new String();
		int indexDot = path.lastIndexOf(".");
		if(indexDot != -1){
		    extension = path.substring( indexDot + 1, path.length() );
		}
		
		return extension;
    }
    
    
   static  public void Seek(String pathname) throws IOException {
        path = new File(pathname);
        
        if (!path.exists()) {
            throw new IOException("Cannot access " + pathname + ": No such file or directory");
        }
        
        File[] filesTemp; 
        
        if (path.isFile() ) {
            filesTemp = new File[]{path};            
        } else {            
            filesTemp = path.listFiles();         
        }
               
        files = new Vector<File>();              
        for( int i = 0; i < filesTemp.length; i++ ) {
            if(GetFileExtFromPath(filesTemp[i].getPath()).equals("dat")) 
            	files.add(filesTemp[i]);            
        }        
        
    }    
   
   
   
   static private void FileSaveHandle(){
	   
	   Point p = aWindow.getLocation(); 
   		p.x = p.x + aWindow.getSize().width / 2;
   		p.y = p.y + aWindow.getSize().height / 2;
   		mPasswDlg.setModal(true);
   		mPasswDlg.showDialog(p, PasswDialog.action.TO_ENCODE, "Сохранение");
   		
	}
   
   
   static private  void FileSelectHandle(){
		
		
		int indexSelected =  mListFiles.getSelectedIndex();
		if(indexSelected == -1)
		    return;		
		
		File toParse  = files.get(indexSelected);		
		Aes256SwingApp.mPathCurrent = toParse.getPath();
		
		try {				
			Aes256SwingApp.mData = ReadFromFile(Aes256SwingApp.mPathCurrent);
			
		}
		catch( IOException exc ){	
			UpdateHeader(exc.getMessage());
			Aes256SwingApp.mPathCurrent = "";
		}		
   	
	   	Point p = aWindow.getLocation(); 
	   	p.x = p.x + aWindow.getSize().width / 2;
	   	p.y = p.y + aWindow.getSize().height / 2;
	   	mPasswDlg.setModal(true);
	   	mPasswDlg.showDialog(p, PasswDialog.action.TO_DECODE, "Открытие");		  
   }
   
   static private byte[] ReadFromFile(String fileName) throws IOException
   {  
	  if(fileName.length() == 0)
		   return null;   
	   
      File f = new File(fileName);
      
      
      RandomAccessFile raf = new RandomAccessFile(f, "r");

      tailOffset = raf.readInt();
     
      //int encoding = raf.readInt();
      
      int dataLength  =   (int)f.length() - CryptAes256.mOffset;
      //if( (int)f.length() - CryptAes256.offset > CryptAes256.mDataBitSize ){
    	
      //}
      //else{
    //	  dataLength = CryptAes256.mDataBitSize;   	 
      //}
      
      byte[] bytes = new byte[dataLength];      
      
      raf.seek(CryptAes256.mOffset);
      raf.readFully(bytes);//, (int)CryptAes256.offset, dataLength);
      
      try {
         raf.close();         
      }
      catch (IOException e){         
      }
      return bytes;
   }
   
   static private  void WriteToFile(String fileName, int zeros, CryptAes256.DataEncodingFormat encoding  ) throws IOException  {  
	  
	   if(fileName.length() == 0)
		   return;
	   //long reserved = 0;
	   FileOutputStream fos = new FileOutputStream(fileName);
	   byte[] bytes = ByteBuffer.allocate(CryptAes256.mOffset).putInt(zeros).array();
	   fos.write( bytes );
	   
	   //byte[] bytes = ByteBuffer.allocate(4).putInt(encoding.ordinal()).array();	   
	   //fos.write(bytes);
	   //fos.write({0,0,0,0}, off, len);	   
	   //fos.write((new String()).getBytes());
	   fos.write(mData);
	   
	//   tailOffset = 0;
      try  {
    	  fos.close();
    	  mData = null;
    	  Aes256SwingApp.mTextArea.setText("");    	 
    	  UpdateHeader("");    	  
      }
      catch (IOException e){      
    	  UpdateHeader("Error " + e.getMessage());
      }      
   }
   
   
    //static String GetFile
    
    static void FormList(JList list){		
        if(files != null)
    	list.setListData(files);     	
    }
    
    static void EnableControls(boolean bBlockButtons){	
    	
    	Aes256SwingApp.mTextArea.setEnabled(bBlockButtons);
    	Aes256SwingApp.mBtnFileChoose.setEnabled(bBlockButtons);
    	Aes256SwingApp.mBtnFileSave.setEnabled(bBlockButtons);
    	
    }
    
    static void GetData(){    	
    	if( mData == null || mData.length == 0 )
    		return;
    	
    	byte[] dataDecr = tintaCrypto.DencryptData(mData);
    	
    	//dataDecr = Arrays.copyOf(dataDecr, dataDecr.length - tailOffset);
    	dataDecr = Arrays.copyOf( dataDecr, dataDecr.length - tailOffset);
    	
    	if(dataDecr == null)
    		return;
    	
    	String textData = null; 
    	try{
    		textData = new String(dataDecr, mEncDataFormat );
    		tailOffset = 0;
    	}
    	catch(Exception e){
    		
    	}
    	if(textData!=null){
    		Aes256SwingApp.mTextArea.setText(textData);
    		Aes256SwingApp.mTextArea.requestFocus();
    	}
    	
    }
    static void UpdateSelectFile(){
    	int indexSelected =  mListFiles.getSelectedIndex();
		if(indexSelected == -1)
		    return;		
		File toParse  = files.get(indexSelected);		
		mPathCurrent = toParse.getPath();
		UpdateHeader("Работа с файлом: " + Aes256SwingApp.mPathCurrent);
		EnableControls(true);
    	
    }
    static void SaveData() {    	
    	
    	if( Aes256SwingApp.mTextArea.getText().length() == 0 )
    		return;
    	   	
    	    		   		 
    	byte[]  encData =  Aes256SwingApp.mTextArea.getText().getBytes( Charset.forName( mEncDataFormat ) );
    	
    	if( encData == null )
    		return;
    	
    	Aes256SwingApp.mData  = tintaCrypto.EncryptData( encData );
    	
    	try {			
			WriteToFile( Aes256SwingApp.mPathCurrent, tintaCrypto.getAllignedSize(encData.length) - encData.length, CryptAes256.DataEncodingFormat.UTF_8);
		}
		catch(IOException exc){			
		}    	
    }    
   
    public static void main(String[] args) {		
    	
	
	class MouseListHandler extends MouseAdapter {	    
	    public void mouseClicked(MouseEvent ev)  {
			
	    	UpdateSelectFile();
	    }
	    
	}
	
	
	
	class MouseBtnFileSelectHandler extends MouseAdapter {	     
	   
	    public void mouseClicked( MouseEvent ev ) {
	    	FileSelectHandle();
	    	      
	    }
	    
	}	
	
	class MouseBtnSaveToFileHandler extends MouseAdapter {
	    
	    public void mouseClicked( MouseEvent ev ) {	    	
	    	FileSaveHandle();
	    }
	    
	}
		
	String fileDir = new File("").getAbsolutePath();
	try {
	    Seek(fileDir);
	}
	catch(IOException e){
		UpdateHeader(e.getMessage());
	}
	     
	mPasswDlg = new PasswDialog();
	    
    Toolkit theKit = aWindow.getToolkit(); // Get the window toolkit
   Dimension wndSize = theKit.getScreenSize(); // Get screen size   
        // 
    aWindow.setBounds(wndSize.width/4, wndSize.height/4,    wndSize.width/2, wndSize.height/2); // Size        
    aWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    GridBagLayout gridbag = new GridBagLayout(); // Create a layout manager
    GridBagConstraints constraints = new GridBagConstraints();
    aWindow.getContentPane().setLayout(gridbag); // Set the container layout mgr             
      
       //constraints.weightx = constraints.weightx = 5.0;
    constraints.fill = GridBagConstraints.BOTH;        
	constraints.weighty = 0.5;
	constraints.weightx = 1;
    constraints.gridx = 0;
    constraints.gridy = 0;	
    
    
    mTextArea = new JTextArea(20, 20);    
	
	mTextArea.addKeyListener(new KeyListener() {
        
    	public void keyTyped(KeyEvent e) {}
		 
    	public void keyPressed(KeyEvent e) {

    		if ( e.getKeyCode() == KeyEvent.VK_F2 ) {
    			Aes256SwingApp.mListFiles.setSelectedIndex(0);
    			Aes256SwingApp.mListFiles.requestFocus();
    			
    			//FileSaveHandle();
    		}
    		//else if( e.getKeyCode() == KeyEvent.VK_CONTROL ) {
    		//	FileSelectHandle();
    		//}

    	}
    	public void keyReleased(KeyEvent e) {}
    });
	
    
    JScrollPane scrollPane = new JScrollPane(mTextArea);
    
    
    
	
    Font fontWordText = new Font("Serif", Font.PLAIN, 24);
    UIManager.put("Button.font", fontWordText);
	
    //mFileText = new JTextField();
	gridbag.setConstraints(scrollPane, constraints);    
	aWindow.getContentPane().add(scrollPane);
	//mFileText.addMouseListener(new MouseBtnNextWordHandler());		
		
	Font fontFile = new Font("Serif", Font.BOLD, 14);
    UIManager.put("Button.font", fontFile);
    
    constraints.gridy = 1;
	constraints.weighty = 0.01;
	mBtnFileChoose = new JButton("Открыть");
	gridbag.setConstraints(mBtnFileChoose, constraints);	
	aWindow.getContentPane().add(mBtnFileChoose);
	mBtnFileChoose.addMouseListener(new MouseBtnFileSelectHandler());
	
	constraints.gridy = 2;	
	constraints.weighty = 0.01;
	mBtnFileSave = new JButton("Сохранить");
	gridbag.setConstraints(mBtnFileSave, constraints);	
	aWindow.getContentPane().add(mBtnFileSave);
	mBtnFileSave.addMouseListener(new MouseBtnSaveToFileHandler());	
	
	constraints.gridy = 3;	
	mListFiles = new JList();
	FormList(mListFiles);
	gridbag.setConstraints(mListFiles, constraints);	
	aWindow.getContentPane().add(mListFiles);	
        
	mListFiles.addMouseListener( new MouseListHandler() );
	mListFiles.addListSelectionListener( new ListSelectionListener(){
         		
		public void valueChanged( ListSelectionEvent e ) {
			
			UpdateSelectFile();
		}
		
     } );
	
	mListFiles.addKeyListener(new KeyListener() {
        
    	public void keyTyped(KeyEvent e) {}
		 
    	public void keyPressed(KeyEvent e) {

    		if ( e.getKeyCode() == KeyEvent.VK_ALT ) {		
    			FileSaveHandle();
    		}
    		else if( e.getKeyCode() == KeyEvent.VK_CONTROL ) {
    			FileSelectHandle();
    		}

    	}
    	public void keyReleased(KeyEvent e) {}
    });
		
	EnableControls(false);
        aWindow.setVisible(true); // Display the window
     if(Aes256SwingApp.mListFiles.getModel().getSize()> 0){
    	 
    	 Aes256SwingApp.mListFiles.setSelectedIndex(0);
    	 Aes256SwingApp.mListFiles.requestFocus();
    	 //UpdateSelectFile();
     }
    }
}
