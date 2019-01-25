package com.tinta.math;

/**
 * Matrix class
 * @author Mikhail Evdokimov
 *
 */

//	c1  c2  c3
// 	[0] [1] [2]   // [0][1]...[0][n]
// 	[3] [4] [5]   // [1][1]...[1][n]
// 	[6] [7] [8]   // [2][1]...[2][n]
// 	[9] [10] [11] // [3][1]...[3][n]

public class tintaMatrix2 {
	
	
	public tintaMatrix2 (){}


	public tintaMatrix2 (tintaMatrix2 rv){
		
		mfEntry[0] = rv.mfEntry[0];
        mfEntry[1] = rv.mfEntry[1];
        mfEntry[2] = rv.mfEntry[2];
        mfEntry[3] = rv.mfEntry[3];
	}

    
    public tintaMatrix2  (float fM00, float fM01, float fM10, float fM11){    	
    	mfEntry[0] = fM00;
        mfEntry[1] = fM01;
        mfEntry[2] = fM10;
        mfEntry[3] = fM11;
    }    
    
    public void set (float fM00, float fM01, float fM10, float fM11){  
    	
    	mfEntry[0] = fM00;
        mfEntry[1] = fM01;
        mfEntry[2] = fM10;
        mfEntry[3] = fM11;
    }
    
    public void mult ( final tintaMatrix2 rv ){  
    	
    	float fEntry[] = {0.f, 0.f, 0.f, 0.f, 0.f};
    	fEntry[0] = mfEntry[0]*rv.mfEntry[0] + mfEntry[1]*rv.mfEntry[2];
    	fEntry[1] = mfEntry[0]*rv.mfEntry[1] + mfEntry[1]*rv.mfEntry[3];
    	fEntry[2] = mfEntry[2]*rv.mfEntry[0] + mfEntry[3]*rv.mfEntry[2];
    	fEntry[3] = mfEntry[2]*rv.mfEntry[1] + mfEntry[3]*rv.mfEntry[3];
    	set(fEntry[0], fEntry[1], fEntry[2], fEntry[3]);
    }
    
    public void mult ( float scalar ){  
    	
    	mfEntry[0] *= scalar;
        mfEntry[1] *= scalar;
        mfEntry[2] *= scalar;
        mfEntry[3] *= scalar;
    }
    
   
    public void fromAngle ( float fAngle )
    {
        mfEntry[0] = (float)Math.cos((double)fAngle);
        mfEntry[2] = (float)Math.sin((double)fAngle);
        mfEntry[1] = -mfEntry[2];
        mfEntry[3] =  mfEntry[0];
    }
    
    public tintaVector2 getColumn( int i ) {	
    	if( i > 1 || i < 0 )
    		return null;
    	
		return new tintaVector2( mfEntry[i], mfEntry[i + 2] );
	}   
    
    public float determinant(){
    	return mfEntry[0]*mfEntry[3] - mfEntry[1]*mfEntry[2];
    }
    
    
    @Override
	public boolean equals(Object other){
	    if (other instanceof tintaMatrix2){
	        if (  ((tintaMatrix2)other ).mfEntry[0] ==  mfEntry[0] 
	        		&& ((tintaMatrix2)other ).mfEntry[1] ==  mfEntry[1] 
	        				&& ((tintaMatrix2)other ).mfEntry[2] ==  mfEntry[2]
	        						&& ((tintaMatrix2)other ).mfEntry[3] ==  mfEntry[3]){
	        	
	            return true;
	        }
	    }
	    return false;
	}
	
	@Override	
	public int hashCode() {
		
		int hash = 1;
		final int prime = 31;
		hash = prime * hash + Float.floatToIntBits( mfEntry[0] );
		hash = prime * hash + Float.floatToIntBits( mfEntry[1] );	
		hash = prime * hash + Float.floatToIntBits( mfEntry[2] );
		hash = prime * hash + Float.floatToIntBits( mfEntry[3] );
				
		return hash;			 	
	} 
    
	private float mfEntry[] = {0.f, 0.f, 0.f, 0.f, 0.f};
}
