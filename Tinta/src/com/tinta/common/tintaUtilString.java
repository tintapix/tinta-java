/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;



import com.tinta.math.tintaUtilMath;

/**
 * Util static class for java strings
 * @author Mikhail Evdokimov
 *
 */
public class tintaUtilString {

	
	static final String digitsSymbols = "1234567890";
	static final String alphabetSymbols = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmonpqrstuvwxyz";
	static final String specialSymbols = " ~!@#$%^&*()_+-={}:\"|<>?/,.'][�;`\\";
	static final String fullSymbols = digitsSymbols + alphabetSymbols + specialSymbols;
	
	
	static public String randomString( int length , final String src ){
		int lengthArr = src.length();
		
		if( length == 0 || lengthArr == 0 )
			return "";
			
		StringBuffer buff = new StringBuffer();
		buff.setLength(length);
		
		for ( int i = 0; i < length; i++ ){
			buff.setCharAt(i, src.charAt(tintaUtilMath.rand(0,lengthArr - 1)) );
		}
		return buff.toString();
	}
	
	static public String randomStringFull( int length  ){
		return randomString( length , fullSymbols );
	}
	
	static public String randomStringDigit( int length  ){
		return randomString( length , digitsSymbols );
	}
	
	static public String randomStringAlphabet( int length  ){
		return randomString( length , alphabetSymbols );
	}
	
	/**
	 * Returns file extension - text after '.' or empty string
	 * @param path - string to parse
	 * @return
	 */
	static public String getFileExtension( String path ){
		//String extension = new String();
	
		int i = path.lastIndexOf('.');
		if (i > 0) {
		    return path.substring(i + 1);
		}
		return  "";
	}
	
	
		
	/**
     * Removes last part in the string after delimiter
     * @param path - path to trim
     * @return
     */	
	static public String removePathLeaf( String path , String delimiter ){
			
		String tempPath = path;
		int posSlash = tempPath.lastIndexOf(delimiter);
		if(posSlash > -1){
			tempPath = tempPath.substring(0, posSlash);
			return  tempPath;
		}
		return path;
	}

	
	
	
	
}
