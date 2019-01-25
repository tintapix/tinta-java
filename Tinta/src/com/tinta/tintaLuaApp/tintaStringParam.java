package com.tinta.tintaLuaApp;

public class tintaStringParam {

	public static enum ParamType{
		typeInteger,
		typeString,
		typeReal,
	}
	
	public tintaStringParam( ParamType type) {
		mType = type;
	}
	
	public tintaStringParam() {		
	}
	
	void setType(ParamType type ){
		mType = type;
	}
	
	ParamType getType(){
		return mType ;
	}
	private ParamType mType = ParamType.typeString;
	public String mValue = new String();
}
