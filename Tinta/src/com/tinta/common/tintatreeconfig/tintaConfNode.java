/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common.tintatreeconfig;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import com.tinta.common.tintaBufferIO;

/**
 * Class describes node in the tree
 */
public class tintaConfNode {

	public enum TokenType {
		enNoType, enNumber, enString, enBoolean,
		/* not supported yet */
		// enMappos, // position in file lineBeg,posBeg,lineEnd,posEnd
		enNode // node with nested nodes or values
	};

	public tintaConfNode() {
		mDataType = TokenType.enNoType;
		mbValid = false;
		// bMapMode = false;
		mlevel = -1;
		mBuffValue = new tintaBufferIO();
		chNodesIndices = new ArrayList<tintaConfNode>();
		mParent = null;
	}

	public tintaConfNode(tintaConfNode parent, tintaConfigToken tok) {
		mParent = parent;
		mDataType = tok.mType;
		mbValid = false;
		// bMapMode = mapMode;
		mName = tok.mName;
		mComment = tok.mComment;
		if (parent != null)
			mlevel = parent.mlevel + 1;
		else
			mlevel = 0;
		mBuffValue = new tintaBufferIO();
		chNodesIndices = new ArrayList<tintaConfNode>();

		initData(tok);
	}

	public boolean initData(tintaConfigToken tok) {

		mName = tok.mName;
		mDataType = tok.mType;

		byte[] bytes = null;
		switch (mDataType) {
		case enNumber:
			try {
				double data = Double.parseDouble(tok.mValue);
				bytes = ByteBuffer.allocate(8).putDouble(data).array();
				mbValid = true;
			} catch (NumberFormatException eNum) {
				mbValid = false;
			}
			break;
		case enString: {
			if (tok.mValue.length() >= 2) {
				String value = tok.mValue.substring(1, tok.mValue.length() - 1);
				bytes = value.getBytes();

				mbValid = true;
			}
		}
			break;
		case enBoolean: {
			boolean bVal = false;
			mbValid = false;

			if (tok.mValue.toLowerCase().equals("true") == true) {
				bVal = true;
				mbValid = true;
			} else if (tok.mValue.toLowerCase().equals("false") == true) {
				bVal = false;
				mbValid = true;
			}
			if (mbValid) {
				bytes = new byte[1];
				byte v = 0;
				if (bVal)
					v = 1;
				bytes[0] = v;
			}
		}
			break;
		case enNode: // Node with no value, situation when: "{ {" or
						// "{ namespace{"
			mbValid = true;
			break;
		case enNoType:
			mbValid = false;
			break;
		default:
			mbValid = false;
		}
		if (chNodesIndices.size() > 0)
			chNodesIndices.clear();
		if (bytes != null)
			mBuffValue.AllocateBuffer(bytes, bytes.length);

		return mbValid;
	}

	void addChild(tintaConfNode chld) {

		chNodesIndices.add(chld);
	}

	// returns child quantity on this level
	public int childQuantity() {
		return chNodesIndices.size();
	}

	// returns child on this level by index
	public tintaConfNode getChild(int index) {
		if (index >= chNodesIndices.size())
			return null;

		return chNodesIndices.get(index);
	}

	public void delAllChild() {
		chNodesIndices.clear();
	}

	public void delChild(int index) throws tintaConfigException {
		if (index >= chNodesIndices.size() || index < 0)
			throw new tintaConfigException("Wrong index");

		chNodesIndices.remove(index);
	}

	public void delChild(String name) {

		int pos = 0;
		for (tintaConfNode v : chNodesIndices) {
			if (v.mName.equals(name)) {
				chNodesIndices.remove(pos);
				break;
			}
			pos++;
		}
	}

	public void delChild(tintaConfNode node) {

		int pos = 0;
		for (tintaConfNode v : chNodesIndices) {
			if (v == node) {
				chNodesIndices.remove(pos);
				break;
			}
			pos++;
		}
	}

	// returns child on this level by name
	public tintaConfNode getChild(String name) {
		tintaConfNode r = getByName(name);
		return r;
	}

	public double getValDouble() {
		// size_t size = 0;
		byte[] buffData = mBuffValue.GetBuffer();// findValue(index, name,
													// enBoolean, size );
		double rez = 0;
		if (buffData != null) {
			rez = ByteBuffer.wrap(buffData).getDouble();
		}

		return rez;
	}

	public String getValString() {
		byte[] buffData = mBuffValue.GetBuffer();
		String rez = null;
		if (buffData != null) {
			rez = new String(buffData);
		}
		return rez;
	}

	public boolean getValBoolean() {
		byte[] buffData = mBuffValue.GetBuffer();
		boolean rez = false;
		if (buffData != null && buffData.length >= 1) {
			rez = buffData[0] == 1;
		}
		return rez;
	}

	public boolean setVal(double val) {
		if (mDataType != TokenType.enNumber)
			return false;

		String valStr = new Double(val).toString();

		tintaConfigToken tok = new tintaConfigToken(mlevel, mDataType, mName,
				valStr, 0, new String());
		return initData(tok);
	}

	public boolean setVal(String val) {
		if (mDataType != TokenType.enString)
			return false;

		tintaConfigToken tok = new tintaConfigToken(mlevel, mDataType, mName,
				val, 0, new String());
		return initData(tok);
	}

	public boolean setVal(boolean val) {
		if (mDataType != TokenType.enBoolean)
			return false;
		String valStr = "false";
		if (val)
			valStr = "true";
		tintaConfigToken tok = new tintaConfigToken(mlevel, mDataType, mName,
				valStr, 0, new String());
		return initData(tok);
	}

	public final byte[] getBufferData() {
		return mBuffValue.GetBuffer();
	}

	public int getBufferDataLen() {

		return mBuffValue.GetSize();
	}

	public tintaConfNode getParent() {
		return mParent;
	}

	public TokenType getType() {
		return mDataType;
	}

	public boolean isValid() {
		return mbValid;
	}

	public int getLevel() {
		return mlevel;
	}

	public final String getName() {
		return mName;
	}

	public final String getComment() {
		return mComment;
	}

	// root has null
	protected tintaConfNode mParent = null;

	protected TokenType mDataType;

	protected boolean mbValid;

	protected int mlevel;

	protected String mName = new String();

	protected String mComment = new String();

	public final tintaBufferIO getBuffer() {
		return mBuffValue;
	}

	protected tintaBufferIO mBuffValue;

	// keeping by index
	public final ArrayList<tintaConfNode> getIndices() {
		return chNodesIndices;
	}

	private ArrayList<tintaConfNode> chNodesIndices;
	// by name if it exists
	// tintaConfNodesmap_t chNodesNames;

	public String mNameValue = new String();

	private final tintaConfNode getByName(String name) {
		tintaConfNode r = null;

		for (tintaConfNode v : chNodesIndices) {
			if (v.mName.equals(name))
				return v;

		}
		return null;
	}

}
