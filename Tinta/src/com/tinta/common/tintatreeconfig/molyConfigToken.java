/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common.tintatreeconfig;

/**
 * Token class
 * @author Mikhail Evdokimov
 *
 */
public class molyConfigToken {
	
	public molyConfigToken() {
		mlevel = 0;
		mType = molyConfNode.TokenType.enNoType;
		posEnd = 0;
	}

	public molyConfigToken(int level, molyConfNode.TokenType type, String name,
			String val, int pos, String comment) {
		mlevel = level;
		mType = type;
		mName = name;
		mValue = val;
		posEnd = pos;
		mComment = comment;
		// comment always attaching later
	}

	// multiplicity of the node 0 - first level
	public int mlevel;

	public molyConfNode.TokenType mType;

	public String mName = new String(); // token name like `val1` in exp. val1 = 100

	// global pos of the last symbol in token
	public String mValue = new String(); // token value like `100` in exp. val1 = 100

	// end position in map mode NOT USED
	public int posEnd;

	public String mComment = new String();

}