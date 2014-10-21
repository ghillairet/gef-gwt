/*******************************************************************************
 * Copyright (c) 2000, 2011, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Gerhardt Informatics Kft. - GEFGWT port
 *******************************************************************************/
package org.eclipse.swt.graphics;

//Test comment.

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;

/**
 * Class <code>GC</code> is where all of the drawing capabilities that are
 * supported by SWT are located. Instances are used to draw on either an
 * <code>Image</code>, a <code>Control</code>, or directly on a
 * <code>Display</code>.
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>LEFT_TO_RIGHT, RIGHT_TO_LEFT</dd>
 * </dl>
 * 
 * <p>
 * The SWT drawing coordinate system is the two-dimensional space with the
 * origin (0,0) at the top left corner of the drawing area and with (x,y) values
 * increasing to the right and downward respectively.
 * </p>
 * 
 * <p>
 * The result of drawing on an image that was created with an indexed palette
 * using a color that is not in the palette is platform specific. Some platforms
 * will match to the nearest color while other will draw the color itself. This
 * happens because the allocated image might use a direct palette on platforms
 * that do not support indexed palette.
 * </p>
 * 
 * <p>
 * Application code must explicitly invoke the <code>GC.dispose()</code> method
 * to release the operating system resources managed by each instance when those
 * instances are no longer required. This is <em>particularly</em> important on
 * Windows95 and Windows98 where the operating system has a limited number of
 * device contexts available.
 * </p>
 * 
 * <p>
 * Note: Only one of LEFT_TO_RIGHT and RIGHT_TO_LEFT may be specified.
 * </p>
 * 
 * @see org.eclipse.swt.events.PaintEvent
 * @see <a href="http://www.eclipse.org/swt/snippets/#gc">GC snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Examples:
 *      GraphicsExample, PaintExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
public final class GC extends Resource {
	/**
	 * the handle to the OS device context (Warning: This field is platform
	 * dependent)
	 * <p>
	 * <b>IMPORTANT:</b> This field is <em>not</em> part of the SWT public API.
	 * It is marked public only so that it can be shared within the packages
	 * provided by SWT. It is not available on all platforms and should never be
	 * accessed from application code.
	 * </p>
	 * 
	 * @noreference This field is not intended to be referenced by clients.
	 */
	public long /* int */handle;
	public static boolean CLIP = true;

	Drawable drawable;
	GCData data;
	private int fontSize;
	private Context2d context2d;
	private Color background;
	private Color foreground;
	private Rectangle clipRect;
	private LineAttributes lineAttributes = new LineAttributes(1);
	private Transform transform = new Transform(null);
	private Font font = new Font(null);
	private FillStrokeStyle fillStyle;

	private double alpha = 1.0;
	private int alphaInt = 255;

	private boolean advanced = false;

	private int antialias = 0;

	private boolean xorMode;

	final static int FOREGROUND = 1 << 0;
	final static int BACKGROUND = 1 << 1;
	final static int FONT = 1 << 2;
	final static int LINE_STYLE = 1 << 3;
	final static int LINE_CAP = 1 << 4;
	final static int LINE_JOIN = 1 << 5;
	final static int LINE_WIDTH = 1 << 6;
	final static int LINE_MITERLIMIT = 1 << 7;
	final static int BACKGROUND_BG = 1 << 8;
	final static int DRAW_OFFSET = 1 << 9;
	final static int DRAW = FOREGROUND | LINE_WIDTH | LINE_STYLE | LINE_CAP
			| LINE_JOIN | LINE_MITERLIMIT | DRAW_OFFSET;
	final static int FILL = BACKGROUND;

	static final float[] LINE_DOT = new float[] { 1, 1 };
	static final float[] LINE_DASH = new float[] { 3, 1 };
	static final float[] LINE_DASHDOT = new float[] { 3, 1, 1, 1 };
	static final float[] LINE_DASHDOTDOT = new float[] { 3, 1, 1, 1, 1, 1 };
	static final float[] LINE_DOT_ZERO = new float[] { 3, 3 };
	static final float[] LINE_DASH_ZERO = new float[] { 18, 6 };
	static final float[] LINE_DASHDOT_ZERO = new float[] { 9, 6, 3, 6 };
	static final float[] LINE_DASHDOTDOT_ZERO = new float[] { 9, 3, 3, 3, 3, 3 };

	GC() {
	}

	/**
	 * Constructs a new instance of this class which has been configured to draw
	 * on the specified drawable. Sets the foreground color, background color
	 * and font in the GC to match those in the drawable.
	 * <p>
	 * You must dispose the graphics context when it is no longer required.
	 * </p>
	 * 
	 * @param drawable
	 *            the drawable to draw on
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the drawable is null</li>
	 *                <li>ERROR_NULL_ARGUMENT - if there is no current device</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the drawable is an image
	 *                that is not a bitmap or an icon - if the drawable is an
	 *                image or printer that is already selected into another
	 *                graphics context</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                GC creation</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS if not called from the
	 *                thread that created the drawable</li>
	 *                </ul>
	 */
	public GC(Drawable drawable) {
		// this(drawable, 0);
		if (drawable instanceof GWTDrawable) {
			context2d = ((GWTDrawable) drawable).internal_getContext();
		} else {
			context2d = Canvas.createIfSupported().getContext2d();
		}
		background = new Color(null, 255, 255, 255);
		foreground = new Color(null, 0, 0, 0);
		context2d.setFillStyle("white");
		context2d.setStrokeStyle("black");
		context2d.save();
	}

	/**
	 * Constructs a new instance of this class which has been configured to draw
	 * on the specified drawable. Sets the foreground color, background color
	 * and font in the GC to match those in the drawable.
	 * <p>
	 * You must dispose the graphics context when it is no longer required.
	 * </p>
	 * 
	 * @param drawable
	 *            the drawable to draw on
	 * @param style
	 *            the style of GC to construct
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the drawable is null</li>
	 *                <li>ERROR_NULL_ARGUMENT - if there is no current device</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the drawable is an image
	 *                that is not a bitmap or an icon - if the drawable is an
	 *                image or printer that is already selected into another
	 *                graphics context</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                GC creation</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS if not called from the
	 *                thread that created the drawable</li>
	 *                </ul>
	 * 
	 * @since 2.1.2
	 */
	public GC(Drawable drawable, int style) {
		this(drawable);
	}

	static int checkStyle(int style) {
		if ((style & SWT.LEFT_TO_RIGHT) != 0)
			style &= ~SWT.RIGHT_TO_LEFT;
		return style & (SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT);
	}

	void checkGC(int mask) {
	}

	long /* int */convertRgn(long /* int */rgn, double[] matrix) {
		return rgn;
	}

	/**
	 * Copies a rectangular area of the receiver at the specified position into
	 * the image, which must be of type <code>SWT.BITMAP</code>.
	 * 
	 * @param image
	 *            the image to copy into
	 * @param x
	 *            the x coordinate in the receiver of the area to be copied
	 * @param y
	 *            the y coordinate in the receiver of the area to be copied
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the image is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image is not a bitmap
	 *                or has been disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void copyArea(Image image, int x, int y) {
	}

	/**
	 * Copies a rectangular area of the receiver at the source position onto the
	 * receiver at the destination position.
	 * 
	 * @param srcX
	 *            the x coordinate in the receiver of the area to be copied
	 * @param srcY
	 *            the y coordinate in the receiver of the area to be copied
	 * @param width
	 *            the width of the area to copy
	 * @param height
	 *            the height of the area to copy
	 * @param destX
	 *            the x coordinate in the receiver of the area to copy to
	 * @param destY
	 *            the y coordinate in the receiver of the area to copy to
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void copyArea(int srcX, int srcY, int width, int height, int destX,
			int destY) {
	}

	/**
	 * Copies a rectangular area of the receiver at the source position onto the
	 * receiver at the destination position.
	 * 
	 * @param srcX
	 *            the x coordinate in the receiver of the area to be copied
	 * @param srcY
	 *            the y coordinate in the receiver of the area to be copied
	 * @param width
	 *            the width of the area to copy
	 * @param height
	 *            the height of the area to copy
	 * @param destX
	 *            the x coordinate in the receiver of the area to copy to
	 * @param destY
	 *            the y coordinate in the receiver of the area to copy to
	 * @param paint
	 *            if <code>true</code> paint events will be generated for old
	 *            and obscured areas
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public void copyArea(int srcX, int srcY, int width, int height, int destX,
			int destY, boolean paint) {
	}

	void createLayout() {
	}

	void disposeLayout() {
	}

	void destroy() {
	}

	/**
	 * Draws the outline of a circular or elliptical arc within the specified
	 * rectangular area.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends for
	 * <code>arcAngle</code> degrees, using the current color. Angles are
	 * interpreted such that 0 degrees is at the 3 o'clock position. A positive
	 * value indicates a counter-clockwise rotation while a negative value
	 * indicates a clockwise rotation.
	 * </p>
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin is (
	 * <code>x</code>, <code>y</code>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * </p>
	 * <p>
	 * The resulting arc covers an area <code>width + 1</code> pixels wide by
	 * <code>height + 1</code> pixels tall.
	 * </p>
	 * 
	 * @param x
	 *            the x coordinate of the upper-left corner of the arc to be
	 *            drawn
	 * @param y
	 *            the y coordinate of the upper-left corner of the arc to be
	 *            drawn
	 * @param width
	 *            the width of the arc to be drawn
	 * @param height
	 *            the height of the arc to be drawn
	 * @param startAngle
	 *            the beginning angle
	 * @param arcAngle
	 *            the angular extent of the arc, relative to the start angle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		context2d.save();
		reInitContext2d();
		context2d.translate(x + width / 2.0, y + height / 2.0);
		context2d.scale(1, height / (double) width);
		context2d.beginPath();
		context2d.arc(0, 0, width / 2, -Math.toRadians(startAngle),
				-Math.toRadians(startAngle + arcAngle), true);
		context2d.stroke();
		context2d.restore();
	}

	/**
	 * Draws a rectangle, based on the specified arguments, which has the
	 * appearance of the platform's <em>focus rectangle</em> if the platform
	 * supports such a notion, and otherwise draws a simple rectangle in the
	 * receiver's foreground color.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle
	 * @param y
	 *            the y coordinate of the rectangle
	 * @param width
	 *            the width of the rectangle
	 * @param height
	 *            the height of the rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawRectangle(int, int, int, int)
	 */
	public void drawFocus(int x, int y, int width, int height) {
		drawRectangle(x, y, width, height);
	}

	/**
	 * Draws the given image in the receiver at the specified coordinates.
	 * 
	 * @param image
	 *            the image to draw
	 * @param x
	 *            the x coordinate of where to draw
	 * @param y
	 *            the y coordinate of where to draw
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the image is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image has been
	 *                disposed</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the given coordinates are
	 *                outside the bounds of the image</li>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if no handles are available to
	 *                perform the operation</li>
	 *                </ul>
	 */
	public void drawImage(Image image, int x, int y) {
		if (image.gwtImage == null) {
			System.err.println("drawImage failed: image.gwtImage is null");
			return;
		}
		final ImageElement imageElement = ImageElement.as(image.gwtImage
				.getElement());
		imageElement.setSrc(image.gwtImage.getUrl());
		context2d.save();
		reInitContext2d();
		context2d.drawImage(imageElement, x, y);
		context2d.restore();
	}

	/**
	 * Copies a rectangular area from the source image into a (potentially
	 * different sized) rectangular area in the receiver. If the source and
	 * destination areas are of differing sizes, then the source area will be
	 * stretched or shrunk to fit the destination area as it is copied. The copy
	 * fails if any part of the source rectangle lies outside the bounds of the
	 * source image, or if any of the width or height arguments are negative.
	 * 
	 * @param image
	 *            the source image
	 * @param srcX
	 *            the x coordinate in the source image to copy from
	 * @param srcY
	 *            the y coordinate in the source image to copy from
	 * @param srcWidth
	 *            the width in pixels to copy from the source
	 * @param srcHeight
	 *            the height in pixels to copy from the source
	 * @param destX
	 *            the x coordinate in the destination to copy to
	 * @param destY
	 *            the y coordinate in the destination to copy to
	 * @param destWidth
	 *            the width in pixels of the destination rectangle
	 * @param destHeight
	 *            the height in pixels of the destination rectangle
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the image is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image has been
	 *                disposed</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if any of the width or height
	 *                arguments are negative.
	 *                <li>ERROR_INVALID_ARGUMENT - if the source rectangle is
	 *                not contained within the bounds of the source image</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES - if no handles are available to
	 *                perform the operation</li>
	 *                </ul>
	 */
	public void drawImage(Image image, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight) {
	}

	void drawImage(Image srcImage, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight,
			boolean simple) {
	}

	void drawImage(Image srcImage, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight,
			boolean simple, int imgWidth, int imgHeight) {
	}

	void drawImageAlpha(Image srcImage, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight,
			boolean simple, int imgWidth, int imgHeight) {
	}

	void drawImageMask(Image srcImage, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight,
			boolean simple, int imgWidth, int imgHeight) {
	}

	void drawImageXRender(Image srcImage, int srcX, int srcY, int srcWidth,
			int srcHeight, int destX, int destY, int destWidth, int destHeight,
			boolean simple, int imgWidth, int imgHeight,
			long /* int */maskPixmap, int maskType) {
	}

	long /* int */scale(long /* int */src, int srcX, int srcY, int srcWidth,
			int srcHeight, int destWidth, int destHeight) {
		return 1l;
	}

	/**
	 * Draws a line, using the foreground color, between the points (
	 * <code>x1</code>, <code>y1</code>) and (<code>x2</code>, <code>y2</code>).
	 * 
	 * @param x1
	 *            the first point's x coordinate
	 * @param y1
	 *            the first point's y coordinate
	 * @param x2
	 *            the second point's x coordinate
	 * @param y2
	 *            the second point's y coordinate
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawLine(int x1, int y1, int x2, int y2) {
		context2d.save();
		reInitContext2d();
		double xDouble1 = x1;
		double yDouble1 = y1;
		double xDouble2 = x2;
		double yDouble2 = y2;
		double[] matrix = transform.getMatrix();
		// if it's a vertical or horizontal line
		if (x1 == x2 || y1 == y2) {
			// if the line isn't rotated:
			if (matrix[1] == 0 && matrix[2] == 0) {
				// calculate the real position of the line
				double xReal1 = matrix[0] * x1 + matrix[4];
				double yReal1 = matrix[3] * y1 + matrix[5];
				// if the line width is odd we must translate the
				// coordinates to the middle of the pixel
				if (getLineWidth() % 2 == 1) {
					double dx1 = Math.floor(xReal1) + 0.5;
					double dy1 = Math.floor(yReal1) + 0.5;
					double e;
					e = (dx1 - matrix[4]) / matrix[0] - xDouble1;
					xDouble1 += e;
					xDouble2 += e;
					e = (dy1 - matrix[5]) / matrix[3] - yDouble1;
					yDouble1 += e;
					yDouble2 += e;
				} else {
					// if it's even we must translate it to the top
					// left corner of the pixel
					double dx1 = Math.floor(xReal1);
					double dy1 = Math.floor(yReal1);
					double e;
					e = (dx1 - matrix[4]) / matrix[0] - xDouble1;
					xDouble1 += e;
					xDouble2 += e;
					e = (dy1 - matrix[5]) / matrix[3] - yDouble1;
					yDouble1 += e;
					yDouble2 += e;
				}
			}
		}
		context2d.beginPath();
		context2d.moveTo(xDouble1, yDouble1);
		context2d.lineTo(xDouble2, yDouble2);
		context2d.closePath();
		context2d.stroke();
		context2d.restore();
	}

	/**
	 * Draws the outline of an oval, using the foreground color, within the
	 * specified rectangular area.
	 * <p>
	 * The result is a circle or ellipse that fits within the rectangle
	 * specified by the <code>x</code>, <code>y</code>, <code>width</code>, and
	 * <code>height</code> arguments.
	 * </p>
	 * <p>
	 * The oval covers an area that is <code>width + 1</code> pixels wide and
	 * <code>height + 1</code> pixels tall.
	 * </p>
	 * 
	 * @param x
	 *            the x coordinate of the upper left corner of the oval to be
	 *            drawn
	 * @param y
	 *            the y coordinate of the upper left corner of the oval to be
	 *            drawn
	 * @param width
	 *            the width of the oval to be drawn
	 * @param height
	 *            the height of the oval to be drawn
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawOval(int x, int y, int width, int height) {
		x += 1;
		y += 1;
		width -= 2;
		height -= 2;
		context2d.save();
		reInitContext2d();
		double kappa = 0.5522848;
		double ox = (width / 2) * kappa;
		double oy = (height / 2) * kappa;
		double xe = x + width;
		double ye = y + height;
		double xm = x + width / 2;
		double ym = y + height / 2;
		context2d.beginPath();
		context2d.moveTo(x, ym);
		context2d.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
		context2d.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
		context2d.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
		context2d.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
		context2d.closePath();
		context2d.stroke();
		context2d.restore();
	}

	/**
	 * Draws the path described by the parameter.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param path
	 *            the path to draw
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parameter is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see Path
	 * 
	 * @since 3.1
	 */
	public void drawPath(Path path) {
	}

	/**
	 * Draws a pixel, using the foreground color, at the specified point (
	 * <code>x</code>, <code>y</code>).
	 * <p>
	 * Note that the receiver's line attributes do not affect this operation.
	 * </p>
	 * 
	 * @param x
	 *            the point's x coordinate
	 * @param y
	 *            the point's y coordinate
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.0
	 */
	public void drawPoint(int x, int y) {
		context2d.save();
		reInitContext2d();
		context2d.strokeRect(x, y, 1, 1);
		context2d.restore();
	}

	/**
	 * Draws the closed polygon which is defined by the specified array of
	 * integer coordinates, using the receiver's foreground color. The array
	 * contains alternating x and y values which are considered to represent
	 * points which are the vertices of the polygon. Lines are drawn between
	 * each consecutive pair, and between the first pair and last pair in the
	 * array.
	 * 
	 * @param pointArray
	 *            an array of alternating x and y values which are the vertices
	 *            of the polygon
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT if pointArray is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawPolygon(int[] pointArray) {
		context2d.save();
		reInitContext2d();
		context2d.beginPath();
		context2d.moveTo(pointArray[0], pointArray[1]);
		for (int i = 2; i < pointArray.length; i += 2) {
			context2d.lineTo(pointArray[i], pointArray[i + 1]);
		}
		context2d.closePath();
		context2d.stroke();
		context2d.restore();
	}

	/**
	 * Draws the polyline which is defined by the specified array of integer
	 * coordinates, using the receiver's foreground color. The array contains
	 * alternating x and y values which are considered to represent points which
	 * are the corners of the polyline. Lines are drawn between each consecutive
	 * pair, but not between the first pair and last pair in the array.
	 * 
	 * @param pointArray
	 *            an array of alternating x and y values which are the corners
	 *            of the polyline
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the point array is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawPolyline(int[] pointArray) {
		context2d.save();
		reInitContext2d();
		context2d.beginPath();
		context2d.moveTo(pointArray[0], pointArray[1]);
		for (int i = 2; i < pointArray.length; i += 2) {
			context2d.lineTo(pointArray[i], pointArray[i + 1]);
		}
		context2d.stroke();
		context2d.restore();
	}

	void drawPolyline(long /* int */cairo, int[] pointArray, boolean close) {
	}

	/**
	 * Draws the outline of the rectangle specified by the arguments, using the
	 * receiver's foreground color. The left and right edges of the rectangle
	 * are at <code>x</code> and <code>x + width</code>. The top and bottom
	 * edges are at <code>y</code> and <code>y + height</code>.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle to be drawn
	 * @param y
	 *            the y coordinate of the rectangle to be drawn
	 * @param width
	 *            the width of the rectangle to be drawn
	 * @param height
	 *            the height of the rectangle to be drawn
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawRectangle(int x, int y, int width, int height) {
		if (width < 0) {
			x += width;
			width *= -1;
		}
		if (height < 0) {
			y += height;
			height *= -1;
		}
		context2d.save();
		reInitContext2d();
		float preciseX = x;
		float preciseY = y;
		if (getLineWidth() % 2 == 1) {
			preciseX += 0.5;
			preciseY += 0.5;
			width -= 1;
			height -= 1;
		}
		context2d.strokeRect(preciseX, preciseY, width, height);
		context2d.restore();
	}

	/**
	 * Draws the outline of the specified rectangle, using the receiver's
	 * foreground color. The left and right edges of the rectangle are at
	 * <code>rect.x</code> and <code>rect.x + rect.width</code>. The top and
	 * bottom edges are at <code>rect.y</code> and
	 * <code>rect.y + rect.height</code>.
	 * 
	 * @param rect
	 *            the rectangle to draw
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawRectangle(Rectangle rect) {
		if (rect == null) {
			throw new IllegalArgumentException();
		}
		if (isDisposed()) {
			throw new SWTException(SWT.ERROR_GRAPHIC_DISPOSED);
		}
		drawRectangle(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Draws the outline of the round-cornered rectangle specified by the
	 * arguments, using the receiver's foreground color. The left and right
	 * edges of the rectangle are at <code>x</code> and <code>x + width</code>.
	 * The top and bottom edges are at <code>y</code> and
	 * <code>y + height</code>. The <em>roundness</em> of the corners is
	 * specified by the <code>arcWidth</code> and <code>arcHeight</code>
	 * arguments, which are respectively the width and height of the ellipse
	 * used to draw the corners.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle to be drawn
	 * @param y
	 *            the y coordinate of the rectangle to be drawn
	 * @param width
	 *            the width of the rectangle to be drawn
	 * @param height
	 *            the height of the rectangle to be drawn
	 * @param arcWidth
	 *            the width of the arc
	 * @param arcHeight
	 *            the height of the arc
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawRoundRectangle(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		double kappa = 0.5522848;
		double ox = (arcWidth) * kappa;
		double oy = (arcHeight) * kappa;
		double xe = x + arcWidth * 2;
		double ye = y + arcHeight * 2;
		double xm = x + arcWidth;
		double ym = y + arcHeight;

		context2d.save();
		reInitContext2d();
		context2d.beginPath();
		context2d.moveTo(x + arcWidth, y);
		context2d.lineTo(x + width - arcWidth, y);
		context2d.bezierCurveTo(xm + ox + width - 2 * arcWidth, y, xe + width
				- 2 * arcWidth, ym - oy, xe + width - 2 * arcWidth, ym);
		context2d.lineTo(x + width, y + height - arcHeight);
		context2d.bezierCurveTo(xe + width - 2 * arcWidth, ym + oy + height - 2
				* arcHeight, xm + ox + width - 2 * arcWidth, ye + height - 2
				* arcHeight, xm + width - 2 * arcWidth, ye + height - 2
				* arcHeight);
		context2d.lineTo(x + arcWidth, y + height);
		context2d.bezierCurveTo(xm - ox, ye + height - 2 * arcHeight, x, ym
				+ oy + height - 2 * arcHeight, x, ym + height - 2 * arcHeight);
		context2d.lineTo(x, y + arcHeight);
		context2d.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
		context2d.stroke();
		context2d.restore();
	}

	/**
	 * Draws the given string, using the receiver's current font and foreground
	 * color. No tab expansion or carriage return processing will be performed.
	 * The background of the rectangular area where the string is being drawn
	 * will be filled with the receiver's background color.
	 * 
	 * @param string
	 *            the string to be drawn
	 * @param x
	 *            the x coordinate of the top left corner of the rectangular
	 *            area where the string is to be drawn
	 * @param y
	 *            the y coordinate of the top left corner of the rectangular
	 *            area where the string is to be drawn
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawString(String string, int x, int y) {
		drawString(string, x, y, false);
	}

	/**
	 * Draws the given string, using the receiver's current font and foreground
	 * color. No tab expansion or carriage return processing will be performed.
	 * If <code>isTransparent</code> is <code>true</code>, then the background
	 * of the rectangular area where the string is being drawn will not be
	 * modified, otherwise it will be filled with the receiver's background
	 * color.
	 * 
	 * @param string
	 *            the string to be drawn
	 * @param x
	 *            the x coordinate of the top left corner of the rectangular
	 *            area where the string is to be drawn
	 * @param y
	 *            the y coordinate of the top left corner of the rectangular
	 *            area where the string is to be drawn
	 * @param isTransparent
	 *            if <code>true</code> the background will be transparent,
	 *            otherwise it will be opaque
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawString(String string, int x, int y, boolean isTransparent) {
		drawText(string, x, y, isTransparent ? SWT.DRAW_TRANSPARENT : 0);
	}

	/**
	 * Draws the given string, using the receiver's current font and foreground
	 * color. Tab expansion and carriage return processing are performed. The
	 * background of the rectangular area where the text is being drawn will be
	 * filled with the receiver's background color.
	 * 
	 * @param string
	 *            the string to be drawn
	 * @param x
	 *            the x coordinate of the top left corner of the rectangular
	 *            area where the text is to be drawn
	 * @param y
	 *            the y coordinate of the top left corner of the rectangular
	 *            area where the text is to be drawn
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawText(String string, int x, int y) {
		drawText(string, x, y, SWT.DRAW_DELIMITER | SWT.DRAW_TAB);
	}

	/**
	 * Draws the given string, using the receiver's current font and foreground
	 * color. Tab expansion and carriage return processing are performed. If
	 * <code>isTransparent</code> is <code>true</code>, then the background of
	 * the rectangular area where the text is being drawn will not be modified,
	 * otherwise it will be filled with the receiver's background color.
	 * 
	 * @param string
	 *            the string to be drawn
	 * @param x
	 *            the x coordinate of the top left corner of the rectangular
	 *            area where the text is to be drawn
	 * @param y
	 *            the y coordinate of the top left corner of the rectangular
	 *            area where the text is to be drawn
	 * @param isTransparent
	 *            if <code>true</code> the background will be transparent,
	 *            otherwise it will be opaque
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawText(String string, int x, int y, boolean isTransparent) {
		int flags = SWT.DRAW_DELIMITER | SWT.DRAW_TAB;
		if (isTransparent)
			flags |= SWT.DRAW_TRANSPARENT;
		drawText(string, x, y, flags);
	}

	/**
	 * Draws the given string, using the receiver's current font and foreground
	 * color. Tab expansion, line delimiter and mnemonic processing are
	 * performed according to the specified flags. If <code>flags</code>
	 * includes <code>DRAW_TRANSPARENT</code>, then the background of the
	 * rectangular area where the text is being drawn will not be modified,
	 * otherwise it will be filled with the receiver's background color.
	 * <p>
	 * The parameter <code>flags</code> may be a combination of:
	 * <dl>
	 * <dt><b>DRAW_DELIMITER</b></dt>
	 * <dd>draw multiple lines</dd>
	 * <dt><b>DRAW_TAB</b></dt>
	 * <dd>expand tabs</dd>
	 * <dt><b>DRAW_MNEMONIC</b></dt>
	 * <dd>underline the mnemonic character</dd>
	 * <dt><b>DRAW_TRANSPARENT</b></dt>
	 * <dd>transparent background</dd>
	 * </dl>
	 * </p>
	 * 
	 * @param string
	 *            the string to be drawn
	 * @param x
	 *            the x coordinate of the top left corner of the rectangular
	 *            area where the text is to be drawn
	 * @param y
	 *            the y coordinate of the top left corner of the rectangular
	 *            area where the text is to be drawn
	 * @param flags
	 *            the flags specifying how to process the text
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void drawText(String string, int x, int y, int flags) {
		/**
		 * TODO When transparent is turned off and using mnemonic, it wont
		 * underscore the 1st letter, or does it?
		 */
		double textWidth;
		double mnemonicWidth;
		String mnemonic;
		String[] parts;
		// String[] splitted = context2d.getFont().split(" ");
		// for (String string2 : splitted) {
		// if (string2.endsWith("px")) {
		// fontSize = Integer.parseInt(string2.split("px")[0]);
		// }
		// }
		if (font == null) {
			font = new Font(null, new FontData());
		}
		fontSize = font.getFD().getHeight();
		if ((flags & SWT.DRAW_DELIMITER) != 0) {
			parts = string.split("\n");
			for (String string2 : parts) {
				drawText(string2, x, y, (flags ^ SWT.DRAW_DELIMITER));
				y += fontSize + 1;
			}
		} else if ((flags & SWT.DRAW_TAB) != 0) {
			parts = string.split("\t");
			for (String string2 : parts) {
				drawText(string2, x, y, (flags ^ SWT.DRAW_TAB));
				x = (((int) Math.ceil(x
						+ context2d.measureText(string2).getWidth()) / 100) + 1) * 100;
			}
			// } else if ((flags & SWT.TRANSPARENT) == 0) {
			// textWidth = context2d.measureText(string).getWidth();
			// context2d.fillRect(x - 1, y - fontSize - 1, textWidth + 2,
			// fontSize + 3);
			// drawText(string, x, y, (flags | SWT.TRANSPARENT));
		} else if ((flags & SWT.DRAW_MNEMONIC) != 0) {
			if (string.contains("&")) {
				parts = string.split("&");
				if (parts[0].length() > 0) {
					drawText(parts[0], x, y, (flags ^ SWT.DRAW_MNEMONIC));
					x = (int) Math.ceil(x
							+ context2d.measureText(parts[0]).getWidth());
				}
				for (int j = 1; j < parts.length; j++) {
					int xWidth = (int) Math.ceil(x
							+ context2d.measureText(parts[j]).getWidth());
					char[] charArray = parts[j].toCharArray();
					mnemonic = String.valueOf(charArray[0]);
					parts[j] = "";
					if (charArray.length > 1) {
						for (int i = 1; i < charArray.length; i++) {
							parts[j] = parts[j].concat(String
									.valueOf(charArray[i]));
						}
					}
					mnemonicWidth = context2d.measureText(mnemonic).getWidth();
					drawText(mnemonic, x, y, (flags ^ SWT.DRAW_MNEMONIC));
					int x2 = (int) Math.ceil(x + mnemonicWidth);
					drawText(parts[j], x2, y, (flags ^ SWT.DRAW_MNEMONIC));
					drawLine(x, y + 2, x2, y + 2);
					x = xWidth;
				}
			} else {
				drawText(string, x, y, (flags ^ SWT.DRAW_MNEMONIC));
			}
		} else {
			FontData fd = font.getFD();
			if (foreground == null) {
				setForeground(new Color(null, 0, 0, 0));
			}
			y += fd.getHeight() - 2;
			context2d.save();
			reInitContext2d();
			context2d.setFillStyle(toColorString(foreground));
			context2d.fillText(string, x, y);
			context2d.restore();
		}
	}

	/**
	 * Compares the argument to the receiver, and returns true if they represent
	 * the <em>same</em> object using a class specific comparison.
	 * 
	 * @param object
	 *            the object to compare with this object
	 * @return <code>true</code> if the object is the same as this object and
	 *         <code>false</code> otherwise
	 * 
	 * @see #hashCode
	 */
	public boolean equals(Object object) {
		return false;
	}

	/**
	 * Fills the interior of a circular or elliptical arc within the specified
	 * rectangular area, with the receiver's background color.
	 * <p>
	 * The resulting arc begins at <code>startAngle</code> and extends for
	 * <code>arcAngle</code> degrees, using the current color. Angles are
	 * interpreted such that 0 degrees is at the 3 o'clock position. A positive
	 * value indicates a counter-clockwise rotation while a negative value
	 * indicates a clockwise rotation.
	 * </p>
	 * <p>
	 * The center of the arc is the center of the rectangle whose origin is (
	 * <code>x</code>, <code>y</code>) and whose size is specified by the
	 * <code>width</code> and <code>height</code> arguments.
	 * </p>
	 * <p>
	 * The resulting arc covers an area <code>width + 1</code> pixels wide by
	 * <code>height + 1</code> pixels tall.
	 * </p>
	 * 
	 * @param x
	 *            the x coordinate of the upper-left corner of the arc to be
	 *            filled
	 * @param y
	 *            the y coordinate of the upper-left corner of the arc to be
	 *            filled
	 * @param width
	 *            the width of the arc to be filled
	 * @param height
	 *            the height of the arc to be filled
	 * @param startAngle
	 *            the beginning angle
	 * @param arcAngle
	 *            the angular extent of the arc, relative to the start angle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawArc
	 */
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		context2d.save();
		reInitContext2d();
		context2d.translate(x + width / 2.0, y + height / 2.0);
		context2d.scale(1, height / (double) width);
		context2d.beginPath();
		context2d.arc(0, 0, width / 2, -Math.toRadians(startAngle),
				-Math.toRadians(startAngle + arcAngle), true);
		context2d.fill();
		context2d.restore();
	}

	/**
	 * Fills the interior of the specified rectangle with a gradient sweeping
	 * from left to right or top to bottom progressing from the receiver's
	 * foreground color to its background color.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle to be filled
	 * @param y
	 *            the y coordinate of the rectangle to be filled
	 * @param width
	 *            the width of the rectangle to be filled, may be negative
	 *            (inverts direction of gradient if horizontal)
	 * @param height
	 *            the height of the rectangle to be filled, may be negative
	 *            (inverts direction of gradient if vertical)
	 * @param vertical
	 *            if true sweeps from top to bottom, else sweeps from left to
	 *            right
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawRectangle(int, int, int, int)
	 */
	public void fillGradientRectangle(int x, int y, int width, int height,
			boolean vertical) {
		System.err.println("fillGradientRectangle() draws a normal rectangle");
		fillRectangle(x, y, width, height);
	}

	/**
	 * Fills the interior of an oval, within the specified rectangular area,
	 * with the receiver's background color.
	 * 
	 * @param x
	 *            the x coordinate of the upper left corner of the oval to be
	 *            filled
	 * @param y
	 *            the y coordinate of the upper left corner of the oval to be
	 *            filled
	 * @param width
	 *            the width of the oval to be filled
	 * @param height
	 *            the height of the oval to be filled
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawOval
	 */
	public void fillOval(int x, int y, int width, int height) {
		x += 1;
		y += 1;
		width -= 2;
		height -= 2;
		context2d.save();
		reInitContext2d();
		if (false) {
			context2d.translate(x, y);
			context2d.translate(width / 2.0, height / 2.0);
			context2d.beginPath();
			if (width != height) {
				context2d.scale(1, height / (double) width);
			}
			context2d.arc(0, 0, width / 2.0, 0, Math.PI * 2, true);
		} else {
			double kappa = 0.5522848;
			double ox = (width / 2) * kappa;
			double oy = (height / 2) * kappa;
			double xe = x + width;
			double ye = y + height;
			double xm = x + width / 2;
			double ym = y + height / 2;
			context2d.beginPath();
			context2d.moveTo(x, ym);
			context2d.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
			context2d.bezierCurveTo(xm + ox, y, xe, ym - oy, xe, ym);
			context2d.bezierCurveTo(xe, ym + oy, xm + ox, ye, xm, ye);
			context2d.bezierCurveTo(xm - ox, ye, x, ym + oy, x, ym);
		}
		context2d.closePath();
		context2d.fill();
		context2d.restore();
	}

	/**
	 * Fills the path described by the parameter.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param path
	 *            the path to fill
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parameter is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see Path
	 * 
	 * @since 3.1
	 */
	public void fillPath(Path path) {
	}

	/**
	 * Fills the interior of the closed polygon which is defined by the
	 * specified array of integer coordinates, using the receiver's background
	 * color. The array contains alternating x and y values which are considered
	 * to represent points which are the vertices of the polygon. Lines are
	 * drawn between each consecutive pair, and between the first pair and last
	 * pair in the array.
	 * 
	 * @param pointArray
	 *            an array of alternating x and y values which are the vertices
	 *            of the polygon
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT if pointArray is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawPolygon
	 */
	public void fillPolygon(int[] pointArray) {
		context2d.save();
		reInitContext2d();
		context2d.beginPath();
		context2d.moveTo(pointArray[0], pointArray[1]);
		for (int i = 2; i < pointArray.length; i += 2) {
			context2d.lineTo(pointArray[i], pointArray[i + 1]);
		}
		context2d.closePath();
		context2d.fill();
		context2d.restore();
	}

	/**
	 * Fills the interior of the rectangle specified by the arguments, using the
	 * receiver's background color.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle to be filled
	 * @param y
	 *            the y coordinate of the rectangle to be filled
	 * @param width
	 *            the width of the rectangle to be filled
	 * @param height
	 *            the height of the rectangle to be filled
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawRectangle(int, int, int, int)
	 */
	public void fillRectangle(int x, int y, int width, int height) {
		if (width < 0) {
			x = x + width;
			width = -width;
		}
		if (height < 0) {
			y = y + height;
			height = -height;
		}
		context2d.save();
		reInitContext2d();
		context2d.fillRect(x, y, width, height);
		context2d.restore();
	}

	/**
	 * Fills the interior of the specified rectangle, using the receiver's
	 * background color.
	 * 
	 * @param rect
	 *            the rectangle to be filled
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the rectangle is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawRectangle(int, int, int, int)
	 */
	public void fillRectangle(Rectangle rect) {
		fillRectangle(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Fills the interior of the round-cornered rectangle specified by the
	 * arguments, using the receiver's background color.
	 * 
	 * @param x
	 *            the x coordinate of the rectangle to be filled
	 * @param y
	 *            the y coordinate of the rectangle to be filled
	 * @param width
	 *            the width of the rectangle to be filled
	 * @param height
	 *            the height of the rectangle to be filled
	 * @param arcWidth
	 *            the width of the arc
	 * @param arcHeight
	 *            the height of the arc
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #drawRoundRectangle
	 */
	public void fillRoundRectangle(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		double kappa = 0.5522848;
		double ox = (arcWidth) * kappa;
		double oy = (arcHeight) * kappa;
		double xe = x + arcWidth * 2;
		double ye = y + arcHeight * 2;
		double xm = x + arcWidth;
		double ym = y + arcHeight;

		context2d.save();
		reInitContext2d();
		context2d.beginPath();
		context2d.moveTo(x + arcWidth, y);
		context2d.lineTo(x + width - arcWidth, y);
		context2d.bezierCurveTo(xm + ox + width - 2 * arcWidth, y, xe + width
				- 2 * arcWidth, ym - oy, xe + width - 2 * arcWidth, ym);
		context2d.lineTo(x + width, y + height - arcHeight);
		context2d.bezierCurveTo(xe + width - 2 * arcWidth, ym + oy + height - 2
				* arcHeight, xm + ox + width - 2 * arcWidth, ye + height - 2
				* arcHeight, xm + width - 2 * arcWidth, ye + height - 2
				* arcHeight);
		context2d.lineTo(x + arcWidth, y + height);
		context2d.bezierCurveTo(xm - ox, ye + height - 2 * arcHeight, x, ym
				+ oy + height - 2 * arcHeight, x, ym + height - 2 * arcHeight);
		context2d.lineTo(x, y + arcHeight);
		context2d.bezierCurveTo(x, ym - oy, xm - ox, y, xm, y);
		context2d.fill();
		context2d.restore();
	}

	int fixMnemonic(char[] buffer) {
		return 0;
	}

	/**
	 * Returns the <em>advance width</em> of the specified character in the font
	 * which is currently selected into the receiver.
	 * <p>
	 * The advance width is defined as the horizontal distance the cursor should
	 * move after printing the character in the selected font.
	 * </p>
	 * 
	 * @param ch
	 *            the character to measure
	 * @return the distance in the x direction to move past the character before
	 *         painting the next
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int getAdvanceWidth(char ch) {
		return 0;
	}

	/**
	 * Returns <code>true</code> if receiver is using the operating system's
	 * advanced graphics subsystem. Otherwise, <code>false</code> is returned to
	 * indicate that normal graphics are in use.
	 * <p>
	 * Advanced graphics may not be installed for the operating system. In this
	 * case, <code>false</code> is always returned. Some operating system have
	 * only one graphics subsystem. If this subsystem supports advanced
	 * graphics, then <code>true</code> is always returned. If any graphics
	 * operation such as alpha, antialias, patterns, interpolation, paths,
	 * clipping or transformation has caused the receiver to switch from regular
	 * to advanced graphics mode, <code>true</code> is returned. If the receiver
	 * has been explicitly switched to advanced mode and this mode is supported,
	 * <code>true</code> is returned.
	 * </p>
	 * 
	 * @return the advanced value
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public boolean getAdvanced() {
		return this.advanced;
	}

	/**
	 * Returns the receiver's alpha value. The alpha value is between 0
	 * (transparent) and 255 (opaque).
	 * 
	 * @return the alpha value
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public int getAlpha() {
		return this.alphaInt;
	}

	/**
	 * Returns the receiver's anti-aliasing setting value, which will be one of
	 * <code>SWT.DEFAULT</code>, <code>SWT.OFF</code> or <code>SWT.ON</code>.
	 * Note that this controls anti-aliasing for all <em>non-text drawing</em>
	 * operations.
	 * 
	 * @return the anti-aliasing setting
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getTextAntialias
	 * 
	 * @since 3.1
	 */
	public int getAntialias() {
		return this.antialias;
	}

	/**
	 * Returns the background color.
	 * 
	 * @return the receiver's background color
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Color getBackground() {
		if (background == null) {
			return new Color(null, 255, 255, 255);
		}
		return background;
	}

	/**
	 * Returns the background pattern. The default value is <code>null</code>.
	 * 
	 * @return the receiver's background pattern
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Pattern
	 * 
	 * @since 3.1
	 */
	public Pattern getBackgroundPattern() {
		return null;
	}

	/**
	 * Returns the width of the specified character in the font selected into
	 * the receiver.
	 * <p>
	 * The width is defined as the space taken up by the actual character, not
	 * including the leading and tailing whitespace or overhang.
	 * </p>
	 * 
	 * @param ch
	 *            the character to measure
	 * @return the width of the character
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int getCharWidth(char ch) {
		return 0;
	}

	/**
	 * Returns the bounding rectangle of the receiver's clipping region. If no
	 * clipping region is set, the return value will be a rectangle which covers
	 * the entire bounds of the object the receiver is drawing on.
	 * 
	 * @return the bounding rectangle of the clipping region
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Rectangle getClipping() {
		if (clipRect == null) {
			// System.err.println("GC Uninitalized clipping rectangle,"
			// + " returns (0,0,750,750)");
			// clipRect = new Rectangle(0, 0, 750, 750);
			CanvasElement canvas = context2d.getCanvas();
			// clipRect = new Rectangle(0, 0, canvas.getClientWidth(),
			// canvas.getClientHeight());
			clipRect = new Rectangle(0, 0, 2000, 2000);
		}
		return clipRect;
	}

	/**
	 * Sets the region managed by the argument to the current clipping region of
	 * the receiver.
	 * 
	 * @param region
	 *            the region to fill with the clipping region
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the region is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the region is disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void getClipping(Region region) {
	}

	/**
	 * Returns the receiver's fill rule, which will be one of
	 * <code>SWT.FILL_EVEN_ODD</code> or <code>SWT.FILL_WINDING</code>.
	 * 
	 * @return the receiver's fill rule
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public int getFillRule() {
		return 0;
	}

	/**
	 * Returns the font currently being used by the receiver to draw and measure
	 * text.
	 * 
	 * @return the receiver's font
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Font getFont() {
		if (this.font != null) {
			return this.font;
		}
		return new Font(null);
	}

	/**
	 * Returns a FontMetrics which contains information about the font currently
	 * being used by the receiver to draw and measure text.
	 * 
	 * @return font metrics for the receiver's font
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public FontMetrics getFontMetrics() {
		FontMetrics fm = new FontMetrics();
		fm.averageCharWidth = (int) context2d.measureText("_").getWidth();
		fm.height = (int) font.getFD().height;
		fm.ascent = fm.height - 3;
		fm.descent = 3;
		return fm;
	}

	/**
	 * Returns the receiver's foreground color.
	 * 
	 * @return the color used for drawing foreground things
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Color getForeground() {
		if (foreground == null) {
			return new Color(null, 0, 0, 0);
		}
		return foreground;
	}

	/**
	 * Returns the foreground pattern. The default value is <code>null</code>.
	 * 
	 * @return the receiver's foreground pattern
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Pattern
	 * 
	 * @since 3.1
	 */
	public Pattern getForegroundPattern() {
		return null;
	}

	/**
	 * Returns the GCData.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>GC</code>. It is marked public only so that it can be shared within
	 * the packages provided by SWT. It is not available on all platforms, and
	 * should never be called from application code.
	 * </p>
	 * 
	 * @return the receiver's GCData
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see GCData
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 * 
	 * @since 3.2
	 */
	public GCData getGCData() {
		return null;
	}

	/**
	 * Returns the receiver's interpolation setting, which will be one of
	 * <code>SWT.DEFAULT</code>, <code>SWT.NONE</code>, <code>SWT.LOW</code> or
	 * <code>SWT.HIGH</code>.
	 * 
	 * @return the receiver's interpolation setting
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public int getInterpolation() {
		return 0;
	}

	/**
	 * Returns the receiver's line attributes.
	 * 
	 * @return the line attributes used for drawing lines
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.3
	 */
	public LineAttributes getLineAttributes() {
		if (lineAttributes == null) {
			lineAttributes = new LineAttributes(0);
		}
		return lineAttributes;
	}

	/**
	 * Returns the receiver's line cap style, which will be one of the constants
	 * <code>SWT.CAP_FLAT</code>, <code>SWT.CAP_ROUND</code>, or
	 * <code>SWT.CAP_SQUARE</code>.
	 * 
	 * @return the cap style used for drawing lines
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public int getLineCap() {
		return lineAttributes.cap;
	}

	/**
	 * Returns the receiver's line dash style. The default value is
	 * <code>null</code>.
	 * 
	 * @return the line dash style used for drawing lines
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public int[] getLineDash() {
		return null;
	}

	/**
	 * Returns the receiver's line join style, which will be one of the
	 * constants <code>SWT.JOIN_MITER</code>, <code>SWT.JOIN_ROUND</code>, or
	 * <code>SWT.JOIN_BEVEL</code>.
	 * 
	 * @return the join style used for drawing lines
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public int getLineJoin() {
		return lineAttributes.join;
	}

	/**
	 * Returns the receiver's line style, which will be one of the constants
	 * <code>SWT.LINE_SOLID</code>, <code>SWT.LINE_DASH</code>,
	 * <code>SWT.LINE_DOT</code>, <code>SWT.LINE_DASHDOT</code> or
	 * <code>SWT.LINE_DASHDOTDOT</code>.
	 * 
	 * @return the style used for drawing lines
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int getLineStyle() {
		return lineAttributes.style;
	}

	/**
	 * Returns the width that will be used when drawing lines for all of the
	 * figure drawing operations (that is, <code>drawLine</code>,
	 * <code>drawRectangle</code>, <code>drawPolyline</code>, and so forth.
	 * 
	 * @return the receiver's line width
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public int getLineWidth() {
		return (int) Math.ceil(this.lineAttributes.width);
	}

	/**
	 * Returns the receiver's style information.
	 * <p>
	 * Note that the value which is returned by this method <em>may
	 * not match</em> the value which was provided to the constructor when the
	 * receiver was created. This can occur when the underlying operating system
	 * does not support a particular combination of requested styles.
	 * </p>
	 * 
	 * @return the style bits
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 2.1.2
	 */
	public int getStyle() {
		return 0;
	}

	/**
	 * Returns the receiver's text drawing anti-aliasing setting value, which
	 * will be one of <code>SWT.DEFAULT</code>, <code>SWT.OFF</code> or
	 * <code>SWT.ON</code>. Note that this controls anti-aliasing <em>only</em>
	 * for text drawing operations.
	 * 
	 * @return the anti-aliasing setting
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #getAntialias
	 * 
	 * @since 3.1
	 */
	public int getTextAntialias() {
		return 0;
	}

	/**
	 * Sets the parameter to the transform that is currently being used by the
	 * receiver.
	 * 
	 * @param transform
	 *            the destination to copy the transform into
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parameter is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see Transform
	 * 
	 * @since 3.1
	 */
	public void getTransform(Transform transform) {
	}

	/**
	 * Returns <code>true</code> if this GC is drawing in the mode where the
	 * resulting color in the destination is the <em>exclusive or</em> of the
	 * color values in the source and the destination, and <code>false</code> if
	 * it is drawing in the mode where the destination color is being replaced
	 * with the source color value.
	 * 
	 * @return <code>true</code> true if the receiver is in XOR mode, and false
	 *         otherwise
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public boolean getXORMode() {
		return xorMode;
	}

	/**
	 * Returns an integer hash code for the receiver. Any two objects that
	 * return <code>true</code> when passed to <code>equals</code> must return
	 * the same value for this method.
	 * 
	 * @return the receiver's hash
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #equals
	 */
	public int hashCode() {
		return 0;
	}

	/**
	 * Returns <code>true</code> if the receiver has a clipping region set into
	 * it, and <code>false</code> otherwise. If this method returns false, the
	 * receiver will draw on all available space in the destination. If it
	 * returns true, it will draw only in the area that is covered by the region
	 * that can be accessed with <code>getClipping(region)</code>.
	 * 
	 * @return <code>true</code> if the GC has a clipping region, and
	 *         <code>false</code> otherwise
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public boolean isClipped() {
		return false;
	}

	/**
	 * Returns <code>true</code> if the GC has been disposed, and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the GC. When a GC has been
	 * disposed, it is an error to invoke any other method (except
	 * {@link #dispose()}) using the GC.
	 * 
	 * @return <code>true</code> when the GC is disposed and <code>false</code>
	 *         otherwise
	 */
	public boolean isDisposed() {
		return false;
	}

	/**
	 * Sets the receiver to always use the operating system's advanced graphics
	 * subsystem for all graphics operations if the argument is
	 * <code>true</code>. If the argument is <code>false</code>, the advanced
	 * graphics subsystem is no longer used, advanced graphics state is cleared
	 * and the normal graphics subsystem is used from now on.
	 * <p>
	 * Normally, the advanced graphics subsystem is invoked automatically when
	 * any one of the alpha, antialias, patterns, interpolation, paths, clipping
	 * or transformation operations in the receiver is requested. When the
	 * receiver is switched into advanced mode, the advanced graphics subsystem
	 * performs both advanced and normal graphics operations. Because the two
	 * subsystems are different, their output may differ. Switching to advanced
	 * graphics before any graphics operations are performed ensures that the
	 * output is consistent.
	 * </p>
	 * <p>
	 * Advanced graphics may not be installed for the operating system. In this
	 * case, this operation does nothing. Some operating system have only one
	 * graphics subsystem, so switching from normal to advanced graphics does
	 * nothing. However, switching from advanced to normal graphics will always
	 * clear the advanced graphics state, even for operating systems that have
	 * only one graphics subsystem.
	 * </p>
	 * 
	 * @param advanced
	 *            the new advanced graphics state
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @see #setAlpha
	 * @see #setAntialias
	 * @see #setBackgroundPattern
	 * @see #setClipping(Path)
	 * @see #setForegroundPattern
	 * @see #setLineAttributes
	 * @see #setInterpolation
	 * @see #setTextAntialias
	 * @see #setTransform
	 * @see #getAdvanced
	 * 
	 * @since 3.1
	 */
	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}

	/**
	 * Sets the receiver's alpha value which must be between 0 (transparent) and
	 * 255 (opaque).
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param alpha
	 *            the alpha value
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public void setAlpha(int alpha) {
		this.alphaInt = alpha;
		this.alpha = alpha / 255.0;
		setBackground(this.background);
		setForeground(this.foreground);
	}

	/**
	 * Sets the receiver's anti-aliasing value to the parameter, which must be
	 * one of <code>SWT.DEFAULT</code>, <code>SWT.OFF</code> or
	 * <code>SWT.ON</code>. Note that this controls anti-aliasing for all
	 * <em>non-text drawing</em> operations.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param antialias
	 *            the anti-aliasing setting
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter is not one
	 *                of <code>SWT.DEFAULT</code>, <code>SWT.OFF</code> or
	 *                <code>SWT.ON</code></li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * @see #setTextAntialias
	 * 
	 * @since 3.1
	 */
	public void setAntialias(int antialias) {
		this.antialias = antialias;
	}

	/**
	 * Sets the background color. The background color is used for fill
	 * operations and as the background color when text is drawn.
	 * 
	 * @param color
	 *            the new background color for the receiver
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the color is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the color has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setBackground(Color color) {
		this.background = color;
		context2d.setFillStyle(toColorString(color));
		fillStyle = context2d.getFillStyle();
	}

	/**
	 * Sets the background pattern. The default value is <code>null</code>.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param pattern
	 *            the new background pattern
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see Pattern
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public void setBackgroundPattern(Pattern pattern) {
	}

	private boolean isClipped = false;

	/**
	 * Sets the area of the receiver which can be changed by drawing operations
	 * to the rectangular area specified by the arguments.
	 * 
	 * @param x
	 *            the x coordinate of the clipping rectangle
	 * @param y
	 *            the y coordinate of the clipping rectangle
	 * @param width
	 *            the width of the clipping rectangle
	 * @param height
	 *            the height of the clipping rectangle
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setClipping(int x, int y, int width, int height) {
		clipRect = new Rectangle(x, y, width, height);
	}

	/**
	 * Sets the area of the receiver which can be changed by drawing operations
	 * to the path specified by the argument.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param path
	 *            the clipping path.
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the path has been disposed
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see Path
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public void setClipping(Path path) {
	}

	/**
	 * Sets the area of the receiver which can be changed by drawing operations
	 * to the rectangular area specified by the argument. Specifying
	 * <code>null</code> for the rectangle reverts the receiver's clipping area
	 * to its original value.
	 * 
	 * @param rect
	 *            the clipping rectangle or <code>null</code>
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setClipping(Rectangle rect) {
		setClipping(rect.x, rect.y, rect.width, rect.height);
	}

	/**
	 * Sets the area of the receiver which can be changed by drawing operations
	 * to the region specified by the argument. Specifying <code>null</code> for
	 * the region reverts the receiver's clipping area to its original value.
	 * 
	 * @param region
	 *            the clipping region or <code>null</code>
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the region has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setClipping(Region region) {
	}

	/**
	 * Sets the font which will be used by the receiver to draw and measure text
	 * to the argument. If the argument is null, then a default font appropriate
	 * for the platform will be used instead.
	 * 
	 * @param font
	 *            the new font for the receiver, or null to indicate a default
	 *            font
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the font has been disposed
	 *                </li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setFont(Font font) {
		this.font = font;
		context2d.setFont(this.font.getFD().toString());
	}

	/**
	 * Sets the receiver's fill rule to the parameter, which must be one of
	 * <code>SWT.FILL_EVEN_ODD</code> or <code>SWT.FILL_WINDING</code>.
	 * 
	 * @param rule
	 *            the new fill rule
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the rule is not one of
	 *                <code>SWT.FILL_EVEN_ODD</code> or
	 *                <code>SWT.FILL_WINDING</code></li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public void setFillRule(int rule) {
	}

	/**
	 * Sets the foreground color. The foreground color is used for drawing
	 * operations including when text is drawn.
	 * 
	 * @param color
	 *            the new foreground color for the receiver
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the color is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the color has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setForeground(Color color) {
		foreground = color;
		context2d.setStrokeStyle(toColorString(color));
	}

	/**
	 * Sets the foreground pattern. The default value is <code>null</code>.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param pattern
	 *            the new foreground pattern
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see Pattern
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public void setForegroundPattern(Pattern pattern) {
	}

	/**
	 * Sets the receiver's interpolation setting to the parameter, which must be
	 * one of <code>SWT.DEFAULT</code>, <code>SWT.NONE</code>,
	 * <code>SWT.LOW</code> or <code>SWT.HIGH</code>.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param interpolation
	 *            the new interpolation setting
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the rule is not one of
	 *                <code>SWT.DEFAULT</code>, <code>SWT.NONE</code>,
	 *                <code>SWT.LOW</code> or <code>SWT.HIGH</code>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public void setInterpolation(int interpolation) {
	}

	/**
	 * Sets the receiver's line attributes.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param attributes
	 *            the line attributes
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the attributes is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if any of the line attributes
	 *                is not valid</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see LineAttributes
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.3
	 */
	public void setLineAttributes(LineAttributes attributes) {
		setLineWidth((int) attributes.width);
	}

	/**
	 * Sets the receiver's line cap style to the argument, which must be one of
	 * the constants <code>SWT.CAP_FLAT</code>, <code>SWT.CAP_ROUND</code>, or
	 * <code>SWT.CAP_SQUARE</code>.
	 * 
	 * @param cap
	 *            the cap style to be used for drawing lines
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the style is not valid</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public void setLineCap(int cap) {
		this.lineAttributes.cap = cap;
	}

	/**
	 * Sets the receiver's line dash style to the argument. The default value is
	 * <code>null</code>. If the argument is not <code>null</code>, the
	 * receiver's line style is set to <code>SWT.LINE_CUSTOM</code>, otherwise
	 * it is set to <code>SWT.LINE_SOLID</code>.
	 * 
	 * @param dashes
	 *            the dash style to be used for drawing lines
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if any of the values in the
	 *                array is less than or equal 0</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public void setLineDash(int[] dashes) {
	}

	/**
	 * Sets the receiver's line join style to the argument, which must be one of
	 * the constants <code>SWT.JOIN_MITER</code>, <code>SWT.JOIN_ROUND</code>,
	 * or <code>SWT.JOIN_BEVEL</code>.
	 * 
	 * @param join
	 *            the join style to be used for drawing lines
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the style is not valid</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.1
	 */
	public void setLineJoin(int join) {
		this.lineAttributes.join = join;
	}

	/**
	 * Sets the receiver's line style to the argument, which must be one of the
	 * constants <code>SWT.LINE_SOLID</code>, <code>SWT.LINE_DASH</code>,
	 * <code>SWT.LINE_DOT</code>, <code>SWT.LINE_DASHDOT</code> or
	 * <code>SWT.LINE_DASHDOTDOT</code>.
	 * 
	 * @param lineStyle
	 *            the style to be used for drawing lines
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the style is not valid</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setLineStyle(int lineStyle) {
		this.lineAttributes.style = lineStyle;
	}

	/**
	 * Sets the width that will be used when drawing lines for all of the figure
	 * drawing operations (that is, <code>drawLine</code>,
	 * <code>drawRectangle</code>, <code>drawPolyline</code>, and so forth.
	 * <p>
	 * Note that line width of zero is used as a hint to indicate that the
	 * fastest possible line drawing algorithms should be used. This means that
	 * the output may be different from line width one.
	 * </p>
	 * 
	 * @param lineWidth
	 *            the width of a line
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setLineWidth(int lineWidth) {
		this.lineAttributes.width = lineWidth;
		context2d.setLineWidth(lineWidth);
	}

	/**
	 * Sets the receiver's text anti-aliasing value to the parameter, which must
	 * be one of <code>SWT.DEFAULT</code>, <code>SWT.OFF</code> or
	 * <code>SWT.ON</code>. Note that this controls anti-aliasing only for all
	 * <em>text drawing</em> operations.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param antialias
	 *            the anti-aliasing setting
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter is not one
	 *                of <code>SWT.DEFAULT</code>, <code>SWT.OFF</code> or
	 *                <code>SWT.ON</code></li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * @see #setAntialias
	 * 
	 * @since 3.1
	 */
	public void setTextAntialias(int antialias) {
	}

	/**
	 * Sets the transform that is currently being used by the receiver. If the
	 * argument is <code>null</code>, the current transform is set to the
	 * identity transform.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param transform
	 *            the transform to set
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * 
	 * @see Transform
	 * @see #getAdvanced
	 * @see #setAdvanced
	 * 
	 * @since 3.1
	 */
	public void setTransform(Transform transform) {
		if (transform != null) {
			double[] matrix = transform.getMatrix();
			double m11 = matrix[0];
			double m12 = matrix[2];
			double m21 = matrix[1];
			double m22 = matrix[3];
			double dx = matrix[4];
			double dy = matrix[5];
			context2d.setTransform(m11, m12, m21, m22, dx, dy);
			this.transform = transform;
		} else {
			context2d.setTransform(1, 0, 0, 1, 0, 0);
			this.transform = new Transform(null);
		}
		// if (transform != null) {
		// if(transform.getMatrix() ! = null)
		// for (int i = 0; i < 6; i++) {
		// System.out.print(transform.getMatrix()[i] + ", ");
		// }
		// }
		// transform.getElements(new)
		// for (int i = 0; i < 6; i++) {
		// System.out.print(transform.getMatrix()[i] + ", ");
		// }
	}

	/**
	 * If the argument is <code>true</code>, puts the receiver in a drawing mode
	 * where the resulting color in the destination is the <em>exclusive or</em>
	 * of the color values in the source and the destination, and if the
	 * argument is <code>false</code>, puts the receiver in a drawing mode where
	 * the destination color is replaced with the source color value.
	 * <p>
	 * Note that this mode in fundamentally unsupportable on certain platforms,
	 * notably Carbon (Mac OS X). Clients that want their code to run on all
	 * platforms need to avoid this method.
	 * </p>
	 * 
	 * @param xor
	 *            if <code>true</code>, then <em>xor</em> mode is used,
	 *            otherwise <em>source copy</em> mode is used
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @deprecated this functionality is not supported on some platforms
	 */
	public void setXORMode(boolean xor) {
		this.xorMode = xor;
		if (xorMode) {
			setAlpha(127);
		} else {
			setAlpha(255);
		}
	}

	/**
	 * Returns the extent of the given string. No tab expansion or carriage
	 * return processing will be performed.
	 * <p>
	 * The <em>extent</em> of a string is the width and height of the
	 * rectangular area it would cover if drawn in a particular font (in this
	 * case, the current font in the receiver).
	 * </p>
	 * 
	 * @param string
	 *            the string to measure
	 * @return a point containing the extent of the string
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Point stringExtent(String string) {
		return new Point((int) Math.ceil(context2d.measureText(string)
				.getWidth()), getFont().getFD().getHeight());
	}

	/**
	 * Returns the extent of the given string. Tab expansion and carriage return
	 * processing are performed.
	 * <p>
	 * The <em>extent</em> of a string is the width and height of the
	 * rectangular area it would cover if drawn in a particular font (in this
	 * case, the current font in the receiver).
	 * </p>
	 * 
	 * @param string
	 *            the string to measure
	 * @return a point containing the extent of the string
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Point textExtent(String string) {
		return textExtent(string, SWT.DRAW_DELIMITER | SWT.DRAW_TAB);
	}

	/**
	 * Returns the extent of the given string. Tab expansion, line delimiter and
	 * mnemonic processing are performed according to the specified flags, which
	 * can be a combination of:
	 * <dl>
	 * <dt><b>DRAW_DELIMITER</b></dt>
	 * <dd>draw multiple lines</dd>
	 * <dt><b>DRAW_TAB</b></dt>
	 * <dd>expand tabs</dd>
	 * <dt><b>DRAW_MNEMONIC</b></dt>
	 * <dd>underline the mnemonic character</dd>
	 * <dt><b>DRAW_TRANSPARENT</b></dt>
	 * <dd>transparent background</dd>
	 * </dl>
	 * <p>
	 * The <em>extent</em> of a string is the width and height of the
	 * rectangular area it would cover if drawn in a particular font (in this
	 * case, the current font in the receiver).
	 * </p>
	 * 
	 * @param string
	 *            the string to measure
	 * @param flags
	 *            the flags specifying how to process the text
	 * @return a point containing the extent of the string
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the string is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Point textExtent(String string, int flags) {
		String font = context2d.getFont();
		int textWidth;
		if (font != null) {
			String[] fontSplit = font.split(" ");
			for (String string2 : fontSplit) {
				if (string2.endsWith("px")) {
					fontSize = Integer.parseInt(string2.split("px")[0]);
				}
			}
		} else {
			fontSize = 10;
		}
		if (true) {
			textWidth = (int) Math.ceil(context2d.measureText(string)
					.getWidth());
		}

		return new Point(textWidth, fontSize);
	}

	private String toColorString(Color c) {
		return "rgba(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue()
				+ "," + (this.alpha * c.getAlpha()) + ")";
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		return "GC";
	}

	private void reInitContext2d() {
		if (CLIP) {
			if (clipRect != null) {
				context2d.beginPath();
				context2d.rect(clipRect.x, clipRect.y, clipRect.width,
						clipRect.height);
				context2d.closePath();
				context2d.clip();
			}
		}
		Style canvasStyle = context2d.getCanvas().getStyle();
		String isDirty = canvasStyle.getProperty("DIRTY");
		if (isDirty != null && isDirty.equals("dirty")) {
			if (fillStyle != null) {
				context2d.setFillStyle(fillStyle);
			}
			context2d.setFont(font.getFD().toString());
			context2d.setStrokeStyle(toColorString(foreground));
			context2d.setLineWidth(this.lineAttributes.width);
			if (transform != null) {
				double[] matrix = transform.getMatrix();
				double m11 = matrix[0];
				double m21 = matrix[1];
				double m12 = matrix[2];
				double m22 = matrix[3];
				double dx = matrix[4];
				double dy = matrix[5];
				context2d.setTransform(m11, m12, m21, m22, dx, dy);
			}
			canvasStyle.setProperty("DIRTY", "clean");
			context2d.save();
		}
	}

	public Context2d getContext2d() {
		return context2d;
	}

	private void debugContext2d() {
		System.out.println("FillStyle = " + context2d.getFillStyle());
		System.out.println("StrokeStyle = " + context2d.getStrokeStyle());
		System.out.println("LineWidth = " + context2d.getLineWidth());

	}

	public void setFillStyle(int x, int y, Color bgc, int width, int height,
			Color transformColor) {
		CanvasGradient grad = context2d.createLinearGradient(x, y, width,
				height);
		grad.addColorStop(0, toColorString(bgc));
		grad.addColorStop(1, toColorString(transformColor));
		context2d.setFillStyle(grad);
		fillStyle = grad;
	}

	private void testPaint() {
		context2d.save();
		CanvasGradient grad = context2d.createLinearGradient(0, 500, 0, 600);
		grad.addColorStop(0, toColorString(background));
		grad.addColorStop(1, toColorString(foreground));
		context2d.setFillStyle(grad);
		context2d.fillRect(100, 500, 100, 100);
		context2d.restore();
	}

	public static void addCairoString(long handle2, String string, float x,
			float y, Font font2) {

	}
}
