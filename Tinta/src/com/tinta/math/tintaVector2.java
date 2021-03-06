/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.math;


public class tintaVector2 {
	
	public 	float mx = 0.f;
	public 	float my = 0.f;	
	public tintaVector2(float x, float y ){
		mx = x;
		my = y;
	}
	public tintaVector2(tintaVector2 rv ){
		mx = rv.mx;
		my = rv.my;
	}
	public tintaVector2( ){
		mx = 0.f;
		my = 0.f;
	}
	public void zero(){
		mx = my = 0.f;
	}
	
	public boolean equal( final tintaVector2 rv ){
		return mx == rv.mx && my == rv.my;
	}
	
	public float length( final tintaVector2 rv ){
		return (float)Math.sqrt( (double)(mx * mx + my * my) );
	}
	
	public tintaVector2 perpendicular(){
        return new tintaVector2 (-my, mx);
    }
    
    float crossProduct( tintaVector2 rkVector ){
        return mx * rkVector.my - my * rkVector.mx;
    }
    
	public tintaVector2 minus(final tintaVector2 rv ){
		return new tintaVector2( mx - rv.mx, my - rv.my );
	}
	public tintaVector2 plus(final tintaVector2 rv ){
		return new tintaVector2( mx + rv.mx, my + rv.my );
	}
	public tintaVector2 mult(final tintaVector2 rv ){
		
		return new tintaVector2( mx * rv.mx, my * rv.my );
		 
	}
	public float dotProduct ( tintaVector2 v1 ) {
        return this.mx*v1.mx + this.my*v1.my;
   }
	public static float distance( final tintaVector2 pt1, final tintaVector2 pt2 )
	{
		float x = pt1.mx - pt2.mx;
		float y = pt1.my - pt2.my;	
		
		return (float)Math.sqrt( x*x + y*y );
	}   
	public tintaVector2 normalize() {
		

	      float length = (float)Math.sqrt( this.mx*this.mx + this.my*this.my );
	      if (length != 0) {
	        mx = this.mx/length;
	        my = this.my/length;
	      }
	      return this;
	   }   
	
	public tintaVector2 mult( float scalar )
	{
		mx *=scalar;
		my *=scalar;
		return this;
	}   
	public static tintaVector2  lerp( final tintaVector2 v1, final tintaVector2 v2, float l ) {

		tintaVector2 rez = new tintaVector2();
		if ( l <= 0.0f ) {
			rez = v1;
		} else if ( l >= 1.0f ) {
			rez = v2;
		} else {
			rez = v1.plus(( v2.minus(v1).mult(l) ));
		}
		return rez;
	}
	
	@Override	
	public int hashCode() {
		int hash = 1;
		final int prime = 31;
		hash = prime * hash + Float.floatToIntBits(mx);
		hash = prime * hash + Float.floatToIntBits(my);		
				
		return hash;		
	} 

}
