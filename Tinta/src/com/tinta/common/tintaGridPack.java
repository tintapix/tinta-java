/**
 * Copyright (C) 2011 - 2019 Mikhail Evdokimov 
 * tintapix.com
 * tintapix@gmail.com
 */

package com.tinta.common;

import java.util.ArrayList;

import com.tinta.common.tintaILogger.molyMsgLevel;

/**
 * Grid pack for GUI controls
 * 
 */
public class tintaGridPack {

	public tintaGridPack() {
	}

	public tintaGridPack(int width, int height, int horCells, int vertCells) {

		mWidth = width;
		mHeight = height;
		setCells(horCells, vertCells);
	}

	public void setCells(int horCells, int vertCells) {
		mwCells = horCells;
		mhCells = vertCells;
		updateCellSize();

	}

	public void setSize(int width, int height) {
		mWidth = width;
		mHeight = height;
		updateCellSize();
	}

	public void add(int horPos, int vertPos, int horToFit, int vertToFit,
			float scale) {

		tintaControlPos poscontrol = new tintaControlPos();
		poscontrol.mPosition.mx = (float) horPos;// * (double)mwSizeCell;
		poscontrol.mPosition.my = (float) vertPos;// * (double)mhSizeCell;
		poscontrol.mSize.mx = (float) horToFit;// * (double)mwSizeCell;
		poscontrol.mSize.my = (float) vertToFit;// * (double)mhSizeCell;
		mControls.add(poscontrol);
	}

	/**	 
	 * Add control data in to the grid
	 * @param horPos horizontal position in grid index from 0
	 * @param vertPos vertical position in grid index from 0
	 * @param horToFit horizontal cell fit in grid
	 * @param vertToFit vertical cell fit in grid
	 */
	public void add(int horPos, int vertPos, int horToFit, int vertToFit) {
		add(horPos, vertPos, horToFit, vertToFit, 1.f);
	}

	public tintaControlPos getPosition( int index ) {

		if ( ( mwCells == 0 && mhCells == 0 ) || mWidth == 0 || mHeight == 0
				|| index >= mControls.size() ) {
			tintaDebug
					.getLogger()
					.logMsg("cells and width/height must be defined first or index out of boundary",
							molyMsgLevel.MSG_ASSERT);
			return new tintaControlPos();
		}

		float xPos = mControls.get(index).mPosition.mx * (float) mwSizeCell;
		float yPos = mControls.get(index).mPosition.my * (float) mhSizeCell;
		float xSize = mControls.get(index).mSize.mx * (float) mwSizeCell;
		float ySize = mControls.get(index).mSize.my * (float) mhSizeCell;
		return new tintaControlPos(xPos, yPos, xSize, ySize);
	}

	private void updateCellSize() {
		mwSizeCell = mWidth / mwCells;
		mhSizeCell = mHeight / mhCells;
	}

	int mwCells = 0;

	int mhCells = 0;

	int mWidth = 0;

	int mHeight = 0;

	int mwSizeCell = 0;

	int mhSizeCell = 0;

	ArrayList<tintaControlPos> mControls = new ArrayList<tintaControlPos>();

}
