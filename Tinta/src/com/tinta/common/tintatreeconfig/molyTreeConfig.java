/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common.tintatreeconfig;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.tinta.build.tintaBuild;
import com.tinta.common.tintaUtilSystem;
import com.tinta.common.tintaUtilSystem.EncodTypes;
import com.tinta.common.tintatreeconfig.molyConfNode.TokenType;


public class molyTreeConfig {

	private class RequestPair {
		protected RequestPair(int pos_, String name_) {
			pos = pos_;
			name = name_;
		}

		public int pos = -1;
		public String name = new String();
	}

	private class LastPosition {
		int pos = -1;
	}

	public molyTreeConfig( String path, boolean saveComments ) {

		if (path.length() > 0)
			mPath = path;

		mErrorStack = new ArrayList<String>();

		mBuffer = new ArrayList<String>();

		tokens = new ArrayList<molyConfigToken>();

		tokensDebug = new ArrayList<String>();

		mRootNode = new molyConfNode();

		bSaveComments = saveComments;
	}

	public molyTreeConfig() {
		this("", true);
	}

	public boolean nodeExists(String request) throws molyConfigException {

		ArrayList<RequestPair> requests = fillReqests(request);

		if (requests.size() == 0 || requests.get(requests.size() - 1).pos == -2) {
			throw new molyConfigException(
					"Wrong request format. \"*\" not allowed");
		}
		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);
		return rezNode != null;

	}

	public void parse(String path) throws molyConfigException {

		boolean r = true;
		if (path.length() > 0) {
			mPath = path;
			// mbmapMode = mapMode; // updating
		}
		if (mPath.length() == 0) {

			throw new molyConfigException("Exception. Wrong path to parse");
		}

		tokens.clear();
		tokensDebug.clear();
		mErrorStack.clear();
		mBuffer.clear();
		mRootNode = new molyConfNode(null, new molyConfigToken(0,
				molyConfNode.TokenType.enNode, "", "", 0,	""));

		// parsing
		// first pass: removing all spaces, removing all comments, testing for
		// the correct brace closing {{ }}
		// String finalBuff;

		if (mBuffer.size() == 0)
			r = readFile(mPath); // readUTF8Text( mPath, mBuffer);

		int linePos = 0;
		int globPos = 0;
		int globValPos = 0;
		// size_t linePosBefore= 0; // updates after value inserted
		int mode = 0;

		int level = 0;
		StringBuffer curToken = new StringBuffer();

		// saving '{' '}' ','
		char lastSpecSymbol = '%'; // first symbol
		char lastCommonSymbol = ' '; // symbol

		while (linePos < mBuffer.size() && r) {

			int iPosCur = 0;
			int iBeforeCur = 0;
			String lineText = mBuffer.get(linePos);

			if (linePos == 0
					&& (mEncType == tintaUtilSystem.EncodTypes.UTF8 || mEncType == tintaUtilSystem.EncodTypes.UTF16)) { // first
																														// symbol
																														// in
																														// file
																														// utf8/16
																														// with
																														// BOM
				iPosCur = 1;
			}
			
			if ((mode & enModeComment) != 0 ) {
                mode &= ~enModeComment;
            }

			while (iPosCur < lineText.length() && r) {

				if ( (mode ^ enModeComment) != 0 ) {
					// comment detected not in the string parameter: val =
					// " \\ no comment "
					if (((mode & enModeStringParam) == 0)
							&& (mode & enModeLongComment) == 0
							&& iPosCur != iBeforeCur
							&& lineText.charAt(iPosCur) == '/'
							&& lineText.charAt(iBeforeCur) == '/') { // comment
																		// was
																		// found
						mode |= enModeComment;
					}
					
					if ( (mode & enModeLongComment) != 0 ) {

						if( lineText.charAt(iPosCur) == '/'
								&& lineText.charAt(iBeforeCur) == '*') { // comment
                            mode &= ~enModeLongComment;
                        }
                    }
					else {

						
						if ( (mode ^ enModeStringParam) != 0                              
                                && iPosCur != iBeforeCur
                                && lineText.charAt(iPosCur) == '*'
                                && lineText.charAt(iBeforeCur) == '/') { // comment was found					
                                mode |= enModeLongComment;
                                
                        }
						else {
							
							if (lineText.charAt(iPosCur) == '"') {
								// if `\"` - comment not finished
								if ((mode & enModeStringParam) != 0
										&& lineText.charAt(iBeforeCur) != '\\') {
									mode &= ~enModeStringParam;
								} else {
									// set comment mode
									mode |= enModeStringParam;
								}
							}
		
							// ignoring some symbols or passing them in the text block
							if ((mode & enModeStringParam) != 0) {
								curToken.append(lineText.charAt(iPosCur));
								lastCommonSymbol = ' ';
		
							} else if (lineText.charAt(iPosCur) != ' '
									&& lineText.charAt(iPosCur) != '/'
									&& lineText.charAt(iPosCur) != '\\'
									&& lineText.charAt(iPosCur) != '\n'
									&& lineText.charAt(iPosCur) != '\t') {
								ArrayList<Character> permited = new ArrayList<Character>();
		
								if (lineText.charAt(iPosCur) == '=') { // `=`
		
									if (curToken.length() == 0) {
										r = false;
										ifErrorAdd(r, (int) linePos + 1,
												"Wrong name token before '='");
										break;
									}
		
									// trying add new name token but old value is empty(
									// no delimiter found: ',' '{' '}' )
									if (tokens.size() > 0
											&& tokens.get(tokens.size() - 1).mValue == ""
											&& tokens.get(tokens.size() - 1).mType != molyConfNode.TokenType.enNode) {
										r = false;
										// Molygon::StringUtil::StrStreamType msg;
										// msg << _M("Wrong value token before token ");
										// msg << curToken;
										StringBuffer msg = new StringBuffer();
										msg.append("Wrong value token before token ");
										msg.append(curToken);
		
										ifErrorAdd(r, (int) linePos + 1, msg.toString());
										break;
									}
		
									int f = curToken.indexOf("."); // "." not allowed in
																	// names
									if (f != -1) {
										r = false;
										// Molygon::StringUtil::StrStreamType msg;
										// msg <<
										// _M("Forbidden symbol in name token: ");
										// msg << lineText.at( iPosCur );
										StringBuffer msg = new StringBuffer();
										msg.append("Forbidden symbol in name token: ");
										msg.append(lineText.charAt(iPosCur));
										ifErrorAdd(r, (int) linePos + 1, msg.toString());
										break;
									}
									// saving name token
									// savedComment
									tokens.add(new molyConfigToken(level,
											molyConfNode.TokenType.enNoType, curToken
													.toString(), "", 0,
													""));
		
									// setComment(tokens.get(tokens.size() - 1 ));
									lastSpecSymbol = lineText.charAt(iPosCur);
									curToken = new StringBuffer(); // clear();
								} else if (lineText.charAt(iPosCur) == '}'
										|| lineText.charAt(iPosCur) == ',') { // `}` `,`
																				// trying
																				// save
																				// value
									boolean needDecr = false;
									permited.clear();
									permited.add('}'); // allows such expressions:
														// 'val1=1000},' or
														// 'val1=1000}}'
									if (lineText.charAt(iPosCur) == '}') {
										needDecr = true;
										if (curToken.length() == 0)
											permited.add(','); // allows such
																// expressions:
																// 'val1=1000,}'
									}
		
									// before '}' we must have value token
									molyConfNode.TokenType type = molyConfNode.TokenType.enNoType;
									type = validateValue(curToken.toString());
		
									if (curToken.length() != 0) { // allow empty tokens
																	// '{};
										// charStack_t::const_iterator it =
										// std::find(permited.begin(), permited.end(),
										// lastSpecSymbol );
										boolean found = permited
												.contains(lastSpecSymbol);
		
										if (type == molyConfNode.TokenType.enNoType
												&& !found) {
											r = false;
											// Molygon::StringUtil::StrStreamType msg;
											// msg << _M("Wrong value token before ");
											// msg << lineText.at( iPosCur );
											StringBuffer msg = new StringBuffer();
											msg.append("Wrong value token before ");
											msg.append(lineText.charAt(iPosCur));
											ifErrorAdd(r, (int) linePos + 1,
													msg.toString());
											break;
										}
		
										// +1 - selecting symbol before
										updateValueToken(tokens, level, type,
												curToken.toString(), globValPos + 1);
									}
		
									if (needDecr)
										level--;
									curToken = new StringBuffer();
									globValPos = 0;
									lastSpecSymbol = lineText.charAt(iPosCur);
								} else if (lineText.charAt(iPosCur) == '{') { // `{`
									permited.clear();
									permited.add(',');
									permited.add('{');// allows such expressions '{{'
									permited.add('%');// ignore ',' and first brace
		
									// charStack_t::const_iterator it = std::find(
									// permited.begin(), permited.end(), lastSpecSymbol
									// );
									boolean found = permited.contains(lastSpecSymbol);
									if (!found) {
										r = false;
										ifErrorAdd(r, (int) linePos + 1,
												"Wrong token before '{'");
										break;
									}
		
									int f = curToken.indexOf("."); // "." not allowed in
																	// names
									if (f != -1) {
										r = false;
										// Molygon::StringUtil::StrStreamType msg;
										// msg <<
										// _M("Forbidden symbol in name token: ");
										// msg << lineText.at( iPosCur );
										StringBuffer msg = new StringBuffer();
										msg.append("Forbidden symbol in name token: ");
										msg.append(lineText.charAt(iPosCur));
										ifErrorAdd(r, (int) linePos + 1, msg.toString());
										break;
									}
		
									// saving name of the namespace if we have it
									// 'namespace{'
									tokens.add(new molyConfigToken(level,
											molyConfNode.TokenType.enNode, curToken
													.toString(), "", 0,
													""));
		
									// setComment(tokens.get(tokens.size() - 1 ));
									curToken = new StringBuffer();
									globValPos = 0;
		
									// increasing levels depth
									level++;
									lastSpecSymbol = lineText.charAt(iPosCur);
								} else { // token data
									permited.clear();
									permited.add('{');
									permited.add('=');
									permited.add(',');
									permited.add('%');
									// charStack_t::const_iterator it = std::find(
									// permited.begin(), permited.end(), lastSpecSymbol
									// );
									boolean found = permited.contains(lastSpecSymbol);
									// preventing breaking tokens names
									if ( lastCommonSymbol != ' '
											&& curToken.length() > 0 ) {
										r = false;
										// Molygon::StringUtil::StrStreamType msg;
										// msg << _M("No delimeter after token ");
										// msg << curToken;
										StringBuffer msg = new StringBuffer();
										msg.append("No delimeter after token ");
										msg.append(curToken);
										ifErrorAdd(r, (int) linePos + 1, msg.toString());
										break;
									}
		
									if ( !found ) {
										r = false;
										ifErrorAdd(r, (int) linePos + 1,
												"No delimeter before token");
										break;
									} else {
										globValPos = globPos;
										curToken.append(lineText.charAt(iPosCur));
										lastCommonSymbol = ' ';
									}
								}
							} else { // if any token began forming we casting error
										// saving additional symbol
								lastCommonSymbol = lineText.charAt(iPosCur);
							}
						}
					}
				}
				iBeforeCur = iPosCur;
				iPosCur++;
				globPos++;
			}
			lastCommonSymbol = ' ';
			// new line for sring values in map mode not allowed in this version
			if ((mode & enModeStringParam) != 0) { // && mbmapMode) {
				r = false;
				// Molygon::StringUtil::StrStreamType msg;
				// msg <<
				// _M("In map mode new line symbol is not allowed in this version");

				ifErrorAdd(r, (int) linePos + 1,
						"In map mode new line symbol is not allowed in this version");
				break;
			}
			linePos++;
			// #if CORE_PLATFORM == CORE_PLATFORM_WIN32
			// globPos += 2;

			// #else
			globPos++;
			// #endif
		}
		// in the end trying to find not tokenized value
		if (curToken.length() != 0 && r) {
			// charStack_t permited;

			ArrayList<Character> permited = new ArrayList<Character>();
			// permited.clear();
			permited.add(','); // allows such expressions: ',100'
			permited.add('='); // allows such expressions: ',v1=100'

			// before '}' we must have value token
			molyConfNode.TokenType type = molyConfNode.TokenType.enNoType;
			type = validateValue(curToken.toString());

			// charStack_t::const_iterator it = std::find(permited.begin(),
			// permited.end(), lastSpecSymbol );
			boolean found = permited.contains(lastSpecSymbol);
			if (type == molyConfNode.TokenType.enNoType && !found) {
				r = false;
				// Molygon::StringUtil::StrStreamType msg;
				// msg << _M("Wrong value token before ");
				// msg << linePos;
				StringBuffer msg = new StringBuffer();
				msg.append("Wrong value token before ");
				msg.append(linePos);

				ifErrorAdd(r, (int) linePos + 1, msg.toString());

			}
			updateValueToken(tokens, level, type, curToken.toString(),
					globValPos + 1);
		}

		if (r) {
			
			if ((mode & enModeLongComment) != 0 ) {
				r = false;
				ifErrorAdd(r, -1, "Not all opened braces '{' are closed");
			}
			
			if (level > 0) {
				r = false;
				ifErrorAdd(r, -1, "Not all opened braces '{' are closed");
			} else if (level < 0) {
				r = false;
				ifErrorAdd(r, -1, "Too many closed '}' braces");

			}
		}

		if (!r)
			tokens.clear();

		if (r) {
			// tokens_stack_t::const_iterator rit = tokens.begin();
			molyConfNode curParentNode = mRootNode;
			int curLenvel = 0;
			// for( ;rit!= tokens.end() && r ; rit++){
			for (molyConfigToken tok : tokens) {

				// Molygon::StringUtil::StrStreamType msg;
				StringBuffer msg = new StringBuffer();

				curParentNode = addNode(curParentNode, tok, curLenvel);
				if (curParentNode == null) {
					r = false;

					StringBuffer errmsg = new StringBuffer();
					errmsg.append("Error while adding token name: ");
					errmsg.append(tok.mName);
					errmsg.append(" value: ");
					errmsg.append(tok.mValue);
					errmsg.append(" level: ");
					errmsg.append(tok.mlevel);

					ifErrorAdd(r, -1, msg.toString());
				}

				curLenvel = tok.mlevel;

				if ( tintaBuild.mMode == tintaBuild.molyDebugMode.DEBUG) {

					for (int i = -1; i < curLenvel; i++)
						msg.append("-");

					msg.append("Name: ");
					msg.append(tok.mName);
					msg.append(" Type: ");
					molyConfNode.TokenType type = tok.mType;
					switch (type) {
					case enNoType:
						msg.append(" NoType ");
						break;
					case enNumber:
						msg.append(" Number ");
						break;
					case enString:
						msg.append(" String ");
						break;
					case enBoolean:
						msg.append(" Boolean ");
						break;
					case enNode:
						msg.append(" enNode ");
						break;
					}
					msg.append(" Value: ");
					msg.append(tok.mValue);
					tokensDebug.add(msg.toString());
				}
			}
		}

		if (!r) {

			// destroying tree
			if (mRootNode != null) {
				mRootNode = null;
			}

			StringBuffer buff = new StringBuffer();
			buff.append("Parse errors: \n");
			for (String msg : mErrorStack) {
				buff.append(msg);
				buff.append("\n");
			}
			mErrorStack.clear();
			throw new molyConfigException(buff.toString());

		}
	}

	public void parse() throws molyConfigException {
		this.parse(mPath);
	}

	public ArrayList<String> fillLinesFromNodes(final molyConfNode dataNode,
			boolean last) {

		ArrayList<String> nodes = new ArrayList<String>();
		if (dataNode == null)
			return nodes;

		int childs = dataNode.childQuantity();
		if (dataNode.childQuantity() == 0) { // leaf
			nodes.add(createSaveLine(dataNode, ","));

			return nodes;
		} else {

			if (dataNode.mParent != null) { // ignore for root
				nodes.add(createSaveLine(dataNode, ""));

				// Molygon::StringUtil::StrStreamType rez;
				StringBuffer buff = new StringBuffer();

				buff.append("{");
				nodes.add(buff.toString());
			}

			for (int i = 0; i < childs; i++) {
				fillLinesFromNodes(dataNode.getChild(i), nodes,
						i == (childs - 1));
			}
			if (dataNode.mParent != null) {// ignore for root
				// Molygon::StringUtil::StrStreamType rez;

				StringBuffer buff = new StringBuffer();

				// int lev = dataNode.mlevel - 1; //first offset ignored
				// for(int t = 0; t < lev;t++ )
				// buff.append("\t");
				// rez << _M("\t");

				buff.append("}");
				buff.append(","); // rez << _M(',');
				nodes.add(buff.toString());

			}
		}
		return nodes;
	}

	public boolean hasValues() {
		return mRootNode != null && mRootNode.childQuantity() > 0;
	}

	public boolean save() throws molyConfigException {
		return save(mPath);
	}

	public boolean save(String path) throws molyConfigException {

		if (mRootNode == null) {
			// StringStreamBasic msg;
			String msg = "Exception. Empty data.";

			throw new molyConfigException(msg);
		}

		ArrayList<String> toSave = fillLinesFromNodes(mRootNode, false);

		boolean r = false;
		r = tintaUtilSystem.tintaWriteFile(path, EncodTypes.UTF8, toSave);

		if (!r) {
			// StringStreamBasic msg;
			StringBuffer buff = new StringBuffer();
			buff.append("Exception. Writing file: ");
			buff.append(path);
			buff.append(" error.");

			throw new molyConfigException(buff.toString());
		}

		return r;
	}

	public ArrayList<String> getTokensDebug() {
		return tokensDebug;
	}

	public int getValQuantity(final String request) {

		ArrayList<RequestPair> requests = new ArrayList<RequestPair>();

		requests = fillReqests(request);
		if (requests.size() == 0) {
			return 0;
		}

		if (mRootNode == null)
			return 0;
		// stream_out<<_M("!!!!!! mRootNode == NULL ");

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);
		if (rezNode != null)
			return rezNode.childQuantity();

		return 0;
	}

	/*
	 * Returns Child node by name
	 */
	public molyConfNode getChild(String request, molyConfNode from) {

		molyConfNode parent = from != null ? from : mRootNode;

		if (parent == null)
			return null;

		return parent.getChild(request);
	}

	private String createComment(String text) {
		StringBuffer buffComm = new StringBuffer();
		buffComm.append(" //");
		buffComm.append(text);
		return buffComm.toString();
	}

	private String createError(String request, ArrayList<RequestPair> requests,
			LastPosition last) {

		StringBuffer buffer = new StringBuffer();

		if (last.pos > -1) {
			buffer.append("Wrong request part: ");
			RequestPair pos = requests.get(last.pos - 1);
			if (pos.name.length() != 0)
				buffer.append(requests.get(last.pos - 1).name);
			else {
				buffer.append("[");
				buffer.append(requests.get(last.pos - 1).pos);
				buffer.append("]");
			}

			buffer.append(" in request: ");
			buffer.append(request);
		} else
			buffer.append("Root node was not found ");
		return buffer.toString();
	}

	/*
	 * Deletes node with all child nodes
	 */
	public void delNode(molyConfNode node) throws molyConfigException {
		molyConfNode parent = node.getParent();
		if (parent != null) {
			parent.delChild(node);
		}
	}

	/*
	 * Deletes all child nodes
	 */
	public void delChild(molyConfNode parent, int pos)
			throws molyConfigException {
		if (parent == null)
			throw new molyConfigException("Parent node is null");

		parent.delChild(pos);
	}

	/*
	 * Deletes all child nodes
	 */
	public void delChild(molyConfNode parent, String name)
			throws molyConfigException {
		if (parent == null)
			throw new molyConfigException("Parent node is null");

		parent.delChild(name);
	}

	/*
	 * Deletes all child nodes
	 */
	public void delAllChild(molyConfNode parent) throws molyConfigException {

		if (parent == null)
			throw new molyConfigException("Parent node is null");

		parent.delAllChild();
	}

	/*
	 * bForce - if true creates or replaces value of any type
	 */
	public boolean setValue(String request, boolean val, boolean bForce)
			throws molyConfigException {
		ArrayList<RequestPair> requests = fillReqests(request);

		if (requests.size() == 0) {
			throw new molyConfigException("Wrong request format");
		}

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);

		if (rezNode != null) { // last node in request- value found
			if (rezNode.mDataType != TokenType.enBoolean && !bForce)
				throw new molyConfigException("Value is not a Boolean");
			if (bForce)
				rezNode.mDataType = TokenType.enBoolean;
			return rezNode.setVal(val);
		}
		if (bForce) {

			molyConfNode from = mRootNode;
			molyConfNode before = from;
			int level = -1;
			for (RequestPair pair : requests) {
				level++;
				from = findNode(pair, from);
				boolean lastReq = level == (requests.size() - 1);
				if (from != null && !lastReq
						&& from.mDataType != molyConfNode.TokenType.enNode) {
					StringBuffer err = new StringBuffer();
					err.append("Can`t add leaf to the leaf: ");
					if (pair.name.length() != 0)
						err.append(pair.name);
					else
						err.append(pair.pos);

					throw new molyConfigException(err.toString());
				}

				if (from == null) {
					// int level, molyConfNode.TokenType type, String name,
					// String val, int pos, String comment
					molyConfNode.TokenType tokenType = molyConfNode.TokenType.enNode;
					String value = "";
					if (lastReq) { // last - leaf
						tokenType = molyConfNode.TokenType.enBoolean;
						if (val == true)
							value = "true";
						else
							value = "false";
					}
					molyConfigToken token = new molyConfigToken(level,
							tokenType, pair.name, value, 0, "");
					molyConfNode newNode = new molyConfNode(before, token);
					from = newNode;
					before.addChild(newNode);
				}
				before = from;
			}
			return true;
		}
		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	public boolean setValue(String request, String val, boolean bForce)
			throws molyConfigException {
		ArrayList<RequestPair> requests = fillReqests(request);

		if (requests.size() == 0) {
			throw new molyConfigException("Wrong request format");
		}

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);

		if (rezNode != null) { // last node in request- value found
			if (rezNode.mDataType != TokenType.enString && !bForce)
				throw new molyConfigException("Value is not a String");

			if (bForce)
				rezNode.mDataType = TokenType.enString;
			// add '"' as the String value token
			StringBuffer buff = new StringBuffer();
			buff.append("\"");
			buff.append(val);
			buff.append("\"");
			return rezNode.setVal(buff.toString());
		}
		if (bForce) {

			molyConfNode from = mRootNode;
			molyConfNode before = from;
			int level = -1;
			for (RequestPair pair : requests) {
				level++;
				from = findNode(pair, from);
				boolean lastReq = level == (requests.size() - 1);
				if (from != null && !lastReq
						&& from.mDataType != molyConfNode.TokenType.enNode) {
					StringBuffer err = new StringBuffer();
					err.append("Can`t add leaf to the leaf: ");
					if (pair.name.length() != 0)
						err.append(pair.name);
					else
						err.append(pair.pos);

					throw new molyConfigException(err.toString());
				}

				if (from == null) {
					molyConfNode.TokenType tokenType = molyConfNode.TokenType.enNode;

					StringBuffer value = new StringBuffer();
					if (lastReq) { // last - leaf
						tokenType = molyConfNode.TokenType.enString;
						value.append("\"");
						value.append(val);
						value.append("\"");

					}
					molyConfigToken token = new molyConfigToken(level,
							tokenType, pair.name, value.toString(), 0, "");
					molyConfNode newNode = new molyConfNode(before, token);
					from = newNode;
					before.addChild(newNode);
				}
				before = from;
			}
			return true;
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	public boolean setValue(String request, double val, boolean bForce)
			throws molyConfigException {

		ArrayList<RequestPair> requests = fillReqests(request);

		if (requests.size() == 0) {
			throw new molyConfigException("Wrong request format");
		}

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);

		if (rezNode != null) { // last node in request- value found
			if (rezNode.mDataType != TokenType.enNumber && !bForce)
				throw new molyConfigException("Value is not a Number");
			if (bForce)
				rezNode.mDataType = TokenType.enString;
			return rezNode.setVal(val);
		}
		if (bForce) {

			molyConfNode from = mRootNode;
			molyConfNode before = from;
			int level = -1;
			for (RequestPair pair : requests) {
				level++;
				from = findNode(pair, from);
				boolean lastReq = level == (requests.size() - 1);
				if (from != null && !lastReq
						&& from.mDataType != molyConfNode.TokenType.enNode) {
					StringBuffer err = new StringBuffer();
					err.append("Can`t add leaf to the leaf: ");
					if (pair.name.length() != 0)
						err.append(pair.name);
					else
						err.append(pair.pos);

					throw new molyConfigException(err.toString());
				}

				if (from == null) {
					molyConfNode.TokenType tokenType = molyConfNode.TokenType.enNode;

					String value = "";
					if (lastReq) { // last - leaf
						tokenType = molyConfNode.TokenType.enNumber;
						value = Double.toString(val);
					}
					molyConfigToken token = new molyConfigToken(level,
							tokenType, pair.name, value, 0, "");
					molyConfNode newNode = new molyConfNode(before, token);
					from = newNode;
					before.addChild(newNode);
				}
				before = from;
			}
			return true;
		}
		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	public boolean getValBoolean(String request) throws molyConfigException {
		// requestChain requests;
		ArrayList<RequestPair> requests = fillReqests(request);
		if (requests.size() == 0) {
			// throw ConfigError( "Wrong request format" );
			throw new molyConfigException("Wrong request format");
		}
		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);
		if (rezNode != null) { // last node in request- value found

			if (rezNode.mDataType != TokenType.enBoolean)
				throw new molyConfigException("Value is not a Boolean");

			return rezNode.getValBoolean();
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());
	}

	public String getValString(String request) throws molyConfigException {

		ArrayList<RequestPair> requests = fillReqests(request);
		if (requests.size() == 0) {

			throw new molyConfigException("Wrong request format");
		}
		LastPosition last = new LastPosition();

		molyConfNode rezNode = findNode(requests, mRootNode, last);
		if (rezNode != null) {

			if (rezNode.mDataType != TokenType.enString)
				throw new molyConfigException("Value is not a String");

			return rezNode.getValString();
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	public double getValDouble(String request) throws molyConfigException {

		ArrayList<RequestPair> requests = fillReqests(request);
		if (requests.size() == 0) {

			throw new molyConfigException("Wrong request format");
		}
		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);
		if (rezNode != null) {
			if (rezNode.mDataType != TokenType.enNumber)
				throw new molyConfigException("Value is not a Number");

			return rezNode.getValDouble();
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	/*
	 * adds values to the end
	 */
	public void addValuesString(final ArrayList<String> vals,
			molyConfNode parent) throws molyConfigException {

		if (parent == null)
			throw new molyConfigException("Parent node is null");

		for (String v : vals) {
			StringBuffer data = new StringBuffer();
			data.append("\"");
			data.append(v);
			data.append("\"");
			molyConfigToken token = new molyConfigToken(parent.mlevel + 1,
					TokenType.enString, "", data.toString(), 0,
					"");

			molyConfNode newNode = new molyConfNode(parent, token);

			parent.addChild(newNode);
		}
	}

	/*
	 * adds values to the end
	 */
	public void addValuesDouble(final ArrayList<Double> vals,
			molyConfNode parent) throws molyConfigException {
		if (parent == null)
			throw new molyConfigException("Parent node is null");

		for (Double v : vals) {

			molyConfigToken token = new molyConfigToken(parent.mlevel + 1,
					TokenType.enNumber, "", v.toString(), 0,
					"");
			molyConfNode newNode = new molyConfNode(parent, token);

			parent.addChild(newNode);
		}
	}

	/*
	 * adds values to the end
	 */
	public void addValuesBoolean(final ArrayList<Boolean> vals,
			molyConfNode parent) throws molyConfigException {
		if (parent == null)
			throw new molyConfigException("Parent node is null");

		for (Boolean v : vals) {

			molyConfigToken token = new molyConfigToken(parent.mlevel + 1,
					TokenType.enBoolean, "", v.toString(), 0,
					"");
			molyConfNode newNode = new molyConfNode(parent, token);

			parent.addChild(newNode);
		}
	}

	public ArrayList<Double> getValsDouble(String request)
			throws molyConfigException {
		return getValsDouble(request, -1);
	}

	public ArrayList<Double> getValsDouble(String request, int maxVals)
			throws molyConfigException {

		ArrayList<Double> rez = new ArrayList<Double>();

		ArrayList<RequestPair> requests = fillReqests(request);

		// -2 means '*' symbol in the request
		if (requests.size() == 0 || requests.get(requests.size() - 1).pos != -2) {

			throw new molyConfigException(
					"Wrong request format: needs \"*\" in the end");
		}

		requests.remove(requests.size() - 1);

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);
		int count = 1;
		if (rezNode != null) {
			ArrayList<molyConfNode> ind = rezNode.getIndices();
			for (molyConfNode nodes : ind) {

				if (nodes != null) {
					if (nodes.mDataType == TokenType.enNumber) {
						rez.add(nodes.getValDouble());
						if (count == maxVals)
							break;
						count++;
					}
				}

			}
			return rez; // rezNode.getValDouble();
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	public ArrayList<String> getValsString(String request)
			throws molyConfigException {
		return getValsString(request, -1);
	}

	public ArrayList<String> getValsString(String request, int maxVals)
			throws molyConfigException {

		ArrayList<String> rez = new ArrayList<String>();

		ArrayList<RequestPair> requests = fillReqests(request);
		// -2 means '*' symbol in the request
		if (requests.size() == 0 || requests.get(requests.size() - 1).pos != -2) {

			throw new molyConfigException(
					"Wrong request format: needs \"*\" in the end");
		}

		requests.remove(requests.size() - 1);

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);

		if (rezNode != null) {
			int count = 1;
			ArrayList<molyConfNode> ind = rezNode.getIndices();
			for (molyConfNode nodes : ind) {
				if (nodes != null) {
					if (nodes.mDataType == TokenType.enString) {
						rez.add(nodes.getValString());
						if (count == maxVals)
							break;
						count++;
					}

				}
			}
			return rez; // rezNode.getValDouble();
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	public ArrayList<Boolean> getValsBoolean(String request)
			throws molyConfigException {
		return getValsBoolean(request, -1);
	}

	public ArrayList<Boolean> getValsBoolean(String request, int maxVals)
			throws molyConfigException {

		ArrayList<Boolean> rez = new ArrayList<Boolean>();

		ArrayList<RequestPair> requests = fillReqests(request);
		// -2 means '*' symbol in the request
		if (requests.size() == 0 || requests.get(requests.size() - 1).pos != -2) {

			throw new molyConfigException(
					"Wrong request format: needs \"*\" in the end");
		}

		requests.remove(requests.size() - 1);

		LastPosition last = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);

		if (rezNode != null) {
			int count = 1;
			ArrayList<molyConfNode> ind = rezNode.getIndices();
			for (molyConfNode nodes : ind) {
				if (nodes != null) {
					if (nodes.mDataType != TokenType.enBoolean) {
						rez.add(nodes.getValBoolean());
						if (count == maxVals)
							break;
						count++;
					}
				}

			}
			return rez; // rezNode.getValDouble();
		}

		String msg = createError(request, requests, last);
		throw new molyConfigException(msg.toString());

	}

	/*
	 * Returns Child node by index
	 */
	public molyConfNode getChild(int index, molyConfNode from) {
		molyConfNode parent = from != null ? from : mRootNode;
		if (parent == null)
			return null;

		return parent.getChild(index);
	}

	/*
	 * Returns Child node by request
	 */
	public molyConfNode getNode(String request, LastPosition last)
			throws molyConfigException {

		// requestChain requests;
		ArrayList<RequestPair> requests = fillReqests(request);
		if (requests.size() == 0) {
			// throw ConfigError( "Wrong request format" );
			throw new molyConfigException("Wrong request format");
		}
		// LastPosition lastPos = new LastPosition();
		molyConfNode rezNode = findNode(requests, mRootNode, last);

		return rezNode;
	}

	/*
	 * Returns Child node by request
	 */
	public molyConfNode getNode(String request) throws molyConfigException {

		LastPosition lastPos = new LastPosition();

		return getNode(request, lastPos);
	}

	/*
	 * Returns child quantity by request
	 */
	public int childQuantity(String request) throws molyConfigException {
		if (request.length() == 0 && mRootNode != null)
			return mRootNode.childQuantity();
		return childQuantity(getNode(request));
	}

	/*
	 * Returns child quantity
	 */
	public int childQuantity(molyConfNode from) {
		molyConfNode parent = from != null ? from : mRootNode;
		if (parent == null)
			return 0;

		return parent.childQuantity();
	}

	public molyConfNode getRoot() {
		return mRootNode;
	}

	private final int enNoIndex = -1;

	private final int enAllvalues = -2;
	// `"`  - opened and not closed - ignoring all inside of the block until closing `"`
	private final int enModeStringParam = 0x01;
	// `//` - ignoring all inside of the block until the end of the line `\n`
	private final int enModeComment = 0x02;
	 // /* Long comment */
	private final int enModeLongComment = 0x04;
	
	// private String mComment = "";

	private boolean readFile(String path) throws molyConfigException {

		boolean r = false;
		if (path.length() > 0) {

			mEncType = tintaUtilSystem.getFileEncoding(path);
			if (mEncType != tintaUtilSystem.EncodTypes.NO_ENCODE)
				r = tintaUtilSystem.tintaReadFile(path, mEncType, mBuffer);
			if (!r) {
				// StringStreamBasic msg;
				// msg << "Exception. Reading file: " <<
				// StringUtil::createUTF8String(path) << " error.";
				StringBuffer buff = new StringBuffer();
				buff.append("Exception. Reading file:");
				buff.append(path);
				buff.append(" error.");
				throw new molyConfigException(buff.toString());
			}
		}
		return r;

	}

	private molyConfNode findNode(ArrayList<RequestPair> requests,
			molyConfNode from, LastPosition last) {
		if (from == null)
			return null;

		molyConfNode rezNode = from;
		int pos = 0;
		for (RequestPair req : requests) {

			if (rezNode == null)
				break;
			if (req.pos > enNoIndex) {
				rezNode = rezNode.getChild(req.pos);
			} else
				rezNode = rezNode.getChild(req.name);
			pos++;
		}

		if (rezNode == null)
			last.pos = pos;

		return rezNode;
	}

	private molyConfNode findNode(RequestPair requests, molyConfNode from) {
		if (from == null)
			return null;
		molyConfNode rezNode = from;

		if (requests.pos > enNoIndex) {
			rezNode = rezNode.getChild(requests.pos);
		} else
			rezNode = rezNode.getChild(requests.name);

		return rezNode;
	}

	private String mPath = "";

	private molyConfNode mRootNode = new molyConfNode();

	tintaUtilSystem.EncodTypes mEncType = tintaUtilSystem.EncodTypes.NO_ENCODE;

	/*
	 * saving positions in the file but not values useful when values are large
	 * parts of the text
	 */
	// boolean mbmapMode;

	private ArrayList<String> mErrorStack = new ArrayList<String>();

	private ArrayList<String> mBuffer = new ArrayList<String>();

	private ArrayList<molyConfigToken> tokens = new ArrayList<molyConfigToken>();

	private ArrayList<String> tokensDebug = new ArrayList<String>();

	public boolean bSaveComments = false;

	// if one of line parameters == -1, the message will not include line prefix
	private void ifErrorAdd(boolean rez, int line, String text) {

		if (!rez) {
			StringBuffer msg = new StringBuffer();

			if (line > -1) {
				msg.append("Error in line: ");
				msg.append(line);
				msg.append(" ");
				msg.append(text);
			} else { // common errors
				msg.append("Error ");
				msg.append(text);

			}
			mErrorStack.add(msg.toString());
		}

	}

	// returns new actual parent node
	private molyConfNode addNode(molyConfNode curParentNode,
			molyConfigToken token, int beforeLevel) {

		if (curParentNode == null)
			return null;

		molyConfNode parent;
		// same level
		if (beforeLevel == token.mlevel) {

			parent = curParentNode;
			molyConfNode newNode = new molyConfNode(parent, token);

			if (!newNode.mbValid) {
				newNode = null;
				parent = null;
			} else
				parent.addChild(newNode);
		} else if (beforeLevel > token.mlevel) { // new node on lower level

			int downTo = beforeLevel - token.mlevel;
			molyConfNode downNode = curParentNode;
			while (downTo > 0 && downNode != null) {
				if (downNode != null)
					downNode = downNode.mParent;
				else
					downNode = null;

				downTo--;
			}

			parent = downNode; // curParentNode ? curParentNode->getParent() :
								// NULL;

			if (parent != null) {

				molyConfNode newNode = new molyConfNode(parent, token);

				if (!newNode.mbValid) {
					newNode = null;
					parent = null;
				} else {
					if (parent != null) {
						parent.addChild(newNode);
					}
				}
			}
		} else { // beforeLevel < token.mlevel: new node on upper level
					// situation "{ {"
			assert (token.mlevel + 1 >= 0);
			int upto = token.mlevel - beforeLevel;

			molyConfNode lastNode = curParentNode;
			while (upto > 0 && lastNode != null) {
				int chlds = lastNode.childQuantity();
				lastNode = lastNode.getChild(chlds - 1); // searching node
				upto--;
			}

			if (lastNode != null
					&& lastNode.mDataType == molyConfNode.TokenType.enNode) { // last
																				// node
																				// must
																				// be
																				// not
																				// leaf
																				// with
																				// value
				parent = lastNode;
				molyConfNode newNode = new molyConfNode(parent, token);

				if (!newNode.mbValid) {
					newNode = null;
					parent = null;
				} else {
					if (parent != null) {
						parent.addChild(newNode);
					}
				}
			} else
				parent = null;

		}
		return parent;
	}

	/*
	 * parsing request string rez - vector with node names or indices quantities
	 * - quantity of values in request part
	 */
	private ArrayList<RequestPair> fillReqests(String req) {

		ArrayList<RequestPair> rez = new ArrayList<RequestPair>();

		StringTokenizer st = new StringTokenizer(req, ".");
		ArrayList<String> r = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			r.add(st.nextToken());
		}

		for (String tok : r) {

			int l = tok.length();
			// trying to find .* - all values on level
			if (l == 1 && tok.charAt(0) == '*') {
				rez.add(new RequestPair(enAllvalues, ""));
				break; // TODO [*] for leafs only
			}

			if (l > 2 && tok.charAt(0) == '[' && tok.charAt(l - 1) == ']') { // number
																				// [0]
																				// three
																				// symbols
																				// minimum

				try {
					// std::string::size_type s;
					// int iVal = std::stoi(req.substr(1, l - 2), &s);

					int iVal = Integer.parseInt(tok.substring(1, l - 1));
					rez.add(new RequestPair(iVal, ""));
				} catch (NumberFormatException e) {

					break;
				}

			} else {
				rez.add(new RequestPair(enNoIndex, tok));
			}
		}

		return rez;
	}

	/*
	 * determine the type of the value : 100, 100.12, .1000, true, false,
	 * "text here", returns enNoType if somth wrong
	 */
	private molyConfNode.TokenType validateValue(String val) {

		if (val.length() > 2 && val.charAt(0) == '"'
				&& val.charAt(val.length() - 1) == '"') {
			return molyConfNode.TokenType.enString;
		}

		if (val.equals("true") || val.equals("false")) {
			return molyConfNode.TokenType.enBoolean;
		}

		try {
			Double.parseDouble(val);
			return molyConfNode.TokenType.enNumber;
		} catch (NumberFormatException e) {

		}
		return molyConfNode.TokenType.enNoType;
	}

	// molyConfNode::TokenType createMapData(size_t linebeg, size_t beg, size_t
	// lineend,size_t end, String &rezData) const;

	private void updateValueToken(ArrayList<molyConfigToken> tokens, int level,
			molyConfNode.TokenType type, String token, int endPos) {
		if (token.length() == 0)
			return;

		int pos = tokens.size() - 1;
		// no value - update
		if (tokens.size() > 0 && tokens.get(pos).mValue.length() == 0
				&& tokens.get(pos).mType != molyConfNode.TokenType.enNode) {

			tokens.get(pos).mType = type;
			tokens.get(pos).mValue = token;
			tokens.get(pos).posEnd = endPos;
			//
			// setComment(tokens.get( pos ));
		} else { // have value - insert
			tokens.add(new molyConfigToken(level, type, "", token,
					endPos, ""));

		}

	}

	private String createSaveLine(molyConfNode dataNode, String terminate) {

		if (dataNode == null)
			return "";

		String name = dataNode.mName;
		StringBuffer msg = new StringBuffer();
		int lev = dataNode.mlevel - 1;// first offset have to be ignored
		for (int t = 0; t < lev; t++)
			msg.append("\t");

		if (dataNode.mDataType == molyConfNode.TokenType.enNode)
			msg.append(name);
		else {

			if (name.length() != 0) {
				msg.append(name);
				msg.append(" = ");
			}

			switch (dataNode.mDataType) {
			case enNumber: {
				double rezVal = dataNode.getValDouble();

				msg.append(rezVal);
			}
				break;
			case enString: {
				String rezVal = dataNode.getValString();
				msg.append("\"");
				msg.append(rezVal);
				msg.append("\"");
			}
				break;
			case enBoolean: {
				boolean rezVal = dataNode.getValBoolean();

				if (rezVal == true)
					msg.append("true");
				else
					msg.append("false");
			}
				break;
			default:
				break;
			}
			// terminate before comment
			if (terminate.length() > 0)
				msg.append(terminate);

			if (dataNode.mComment.length() > 0 && bSaveComments) {
				msg.append(createComment(dataNode.mComment));
			}
		}

		return msg.toString();
	}

	/*
	 * Fills vector with nodes description and data in line for saving
	 */
	private void fillLinesFromNodes(molyConfNode dataNode,
			ArrayList<String> nodes, boolean last) {

		if (dataNode == null)
			return;

		int childs = dataNode.childQuantity();
		if (dataNode.childQuantity() == 0
				&& dataNode.mDataType != molyConfNode.TokenType.enNode) { // leaf
																			// node
			nodes.add(createSaveLine(dataNode, ","));
			return;
		} else {
			if (dataNode.mParent != null) { // ignore for root
				if (dataNode.mName.length() > 0
						|| dataNode.mComment.length() != 0)
					nodes.add(createSaveLine(dataNode, ""));

				StringBuffer buff = new StringBuffer();

				int lev = dataNode.mlevel - 1;
				for (int t = 0; t < lev; t++)
					buff.append("\t");

				buff.append("{");
				// for node add comment
				if (dataNode.mComment.length() > 0 && bSaveComments) {
					buff.append(createComment(dataNode.mComment));
				}

				nodes.add(buff.toString());
			}

			for (int i = 0; i < childs; i++) {
				fillLinesFromNodes(dataNode.getChild(i), nodes,
						i == (childs - 1));
			}
			if (dataNode.mParent != null) {// ignore for root

				StringBuffer buff = new StringBuffer();
				int lev = dataNode.mlevel - 1; // first offset ignored
				for (int t = 0; t < lev; t++)
					buff.append("\t");

				buff.append("}");

				buff.append(",");

				nodes.add(buff.toString());

			}
		}
	}

}