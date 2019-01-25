package com.tinta.common.lua;

/*
 * Interface for lua parameter(function) in java
 */
public interface tintaILuaParam {

	public String getName();
	
	public String getDescription();
	
	public int call( tintaLua context );

	
}
