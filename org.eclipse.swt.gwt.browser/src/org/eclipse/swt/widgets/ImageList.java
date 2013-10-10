/*******************************************************************************
 * Copyright (c) 2000, 2005, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/
package org.eclipse.swt.widgets;

import org.eclipse.swt.graphics.Image;

class ImageList {
	long /* int */[] pixbufs;
	int width = -1, height = -1;
	Image[] images;

	public ImageList() {
		images = new Image[4];
		pixbufs = new long /* int */[4];
	}

	public int add(Image image) {
		return 0;
	}

	public void dispose() {
	}

	public Image get(int index) {
		return images[index];
	}

	public int indexOf(Image image) {
		if (image == null)
			return -1;
		for (int index = 0; index < images.length; index++) {
			if (image == images[index])
				return index;
		}
		return -1;
	}

	public boolean isDisposed() {
		return images == null;
	}

	public void put(int index, Image image) {
	}

	public void remove(Image image) {
	}

	public int size() {
		return 0;
	}

}
