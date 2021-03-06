/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

import java.util.Arrays;

/**
 * Buffer wrapper
 * @author Mikhail Evdokimov
 *
 */
public class tintaBufferIO {

	public tintaBufferIO() {
		m_iBufferSize = 0;
	}

	public int GetSize() {
		return m_iBufferSize;
	}

	public final byte[] GetBuffer() {
		return m_pBuffer;
	}

	public byte[] GetBufferEx() {

		return m_pBuffer;
	}

	public byte[] AllocateBuffer(int iSize) {
		if (m_pBuffer != null)
			m_pBuffer = null;

		if (iSize == 0) {
			m_pBuffer = null;
			return null;
		}

		m_pBuffer = new byte[iSize];
		m_iBufferSize = iSize;

		// ::memset( m_pBuffer, 0, iSize );

		return m_pBuffer;
	}

	public byte[] AllocateBuffer(final byte[] pData, int iSize) {
		boolean r = AllocateBuffer(iSize) != null;
		if (!r)
			return null;

		SetData(pData, iSize);

		return m_pBuffer;
	}

	public boolean SetData(final byte[] pData, int iSize) {

		if (m_iBufferSize < iSize)
			return false;

		m_pBuffer = Arrays.copyOf(pData, iSize);
		// mlMemcpy(m_pBuffer, iSize, pData, iSize);

		return true;
	}

	private byte[] m_pBuffer;

	private int m_iBufferSize;
}
