/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.crypto;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JFrame;


public class PasswDialog extends JDialog implements KeyListener {
 
	public enum action {TO_ENCODE, TO_DECODE, DO_NOTHING}; 
		
	
	public PasswDialog(){
		mAction = action.DO_NOTHING;
	}
	private static final long serialVersionUID = -832548326686122133L;
	
	
	static JFrame aWindow = new JFrame();
	static JPasswordField   mPasswText;
	static JPasswordField   mPasswTextRet;
	static JButton mBtnOk;
    static JButton mBtnCancel;
    static String  mText;
    static action  mAction;
    
    void HandleOk(){
    	
    	boolean rez = true;
    	if( mPasswText.getPassword().length > 0 ){
    		
    		if( mAction == action.TO_ENCODE  ){
    			char[] p1 = mPasswText.getPassword();
        		char[] p2 = mPasswTextRet.getPassword();
        		
        		rez = rez && (p1.length == p2.length);
        		        		
        		boolean notEq = false;
        		
        		if( rez ){
	        		for (int i = 0; i < p1.length; i++){
	        			if( p1[i] != p2[i] ){
	        				notEq = true;
	        				break;
	        			}
	        				
	        		}
        		}
        		if( notEq )
        			rez = false;
    		}
    			
    		if( rez ){
	    		//String text = mPasswText.get
    			Aes256SwingApp.setPasswText( mPasswText.getPassword() );
		    	switch(mAction){
		    	
		    	case TO_ENCODE:
		    		Aes256SwingApp.SaveData();
		    		break;
		    	case TO_DECODE:
		    		Aes256SwingApp.GetData();
		    		break;
		    	case DO_NOTHING:
		    		break;  		
		    		
		    	}
		    	
		    	PasswDialog.mPasswText.setText("");
		    	PasswDialog.mPasswTextRet.setText("");
		    	PasswDialog.this.dispose();	
    		}
	    		    	
    	}    	
    }
    
class MouseBtnOkHandler extends MouseAdapter {
	    
	    public void mouseClicked(MouseEvent ev) {	
	    	HandleOk();	    	
	    }
	    
	}


class MouseBtnCancelHandler extends MouseAdapter {
    
    public void mouseClicked(MouseEvent ev) {
    	PasswDialog.mPasswText.setText("");
    	PasswDialog.mPasswTextRet.setText("");
    	PasswDialog.this.dispose();
    }
    
}




public void keyTyped(KeyEvent e) {	       

}
	 
public void keyPressed(KeyEvent e) {

	if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {		
		HandleOk();
	}
	else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
		PasswDialog.mPasswText.setText("");
		PasswDialog.mPasswTextRet.setText("");
    	PasswDialog.this.dispose();
	}

}
public void keyReleased(KeyEvent e) {    
	
}

protected void dialogInit(){
	
		mPasswText = new JPasswordField ("", 32);
		mPasswTextRet = new JPasswordField ("", 32);
		/*TODO ���������� ���� �� 32 ��������*/
		
		mPasswText.addKeyListener(new KeyListener() {
	        
	    	public void keyTyped(KeyEvent e) {}
   		 
	    	public void keyPressed(KeyEvent e) {

	    		if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {		
	    			HandleOk();
	    		}
	    		else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
	    			PasswDialog.mPasswText.setText("");
	    	    	PasswDialog.this.dispose();
	    		}

	    	}
	    	public void keyReleased(KeyEvent e) {}
	    });


    
		mBtnOk = new JButton("Ok");
		mBtnCancel = new JButton("Cancel");	
		super.dialogInit();	 
		
		//addKeyListener(this);
	 
		GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints c = new GridBagConstraints();
	    c.insets.top = 3;
	    c.insets.bottom = 3;
	    JPanel pane = new JPanel(gridbag);
	    pane.setBorder(BorderFactory.createEmptyBorder(10, 20, 5, 20));
	    c.anchor = GridBagConstraints.EAST;
	    

	   
	    c.gridy = 1;
	    gridbag.setConstraints(mPasswText, c);
	    pane.add(mPasswText);
	    
	    c.gridy = 2;
	    gridbag.setConstraints(mPasswTextRet, c);
	    //mPasswText.addActionListener(actionListener);
	    //mPasswText.addKeyListener(keyListener);
	    pane.add(mPasswTextRet);

	    c.gridy = 3;
	    c.gridwidth = GridBagConstraints.REMAINDER;
	    c.anchor = GridBagConstraints.CENTER;
	    
	    JPanel panel = new JPanel();
	    mBtnOk.addMouseListener(new MouseBtnOkHandler());
	  	    
	    //okButton.addKeyListener(keyListener);
	    panel.add(mBtnOk);
	    mBtnCancel.addMouseListener(new MouseBtnCancelHandler());
	    //this.addKeyListener(new KeybordBtnCancelHandler());
	    
	    panel.add(mBtnCancel);
	    gridbag.setConstraints(panel, c);
	    pane.add(panel);

	    getContentPane().add(pane);
	    pack();
	
	
	
}
public void showDialog(Point p, action toDo, String headerText){
	  mAction = toDo;
	  
	  if(mAction == action.TO_ENCODE)
		  mPasswTextRet.setVisible(true);
	  else
		  mPasswTextRet.setVisible(false);
	  
      setLocation(p.x , p.y );
      this.setTitle(headerText); 
    
    setVisible(true);    
 }



	
} // PasswDialog
