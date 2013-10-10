/*******************************************************************************
 * Copyright (c) 2000, 2010, 2012 IBM Corporation, Gerhardt Informatics Kft. and others.
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWTException;

import com.google.gwt.resources.client.ImageResource;

/**
 * Instances of this class are graphics which have been prepared for display on
 * a specific device. That is, they are ready to paint using methods such as
 * <code>GC.drawImage()</code> and display on widgets with, for example,
 * <code>Button.setImage()</code>.
 * <p>
 * If loaded from a file format that supports it, an <code>Image</code> may have
 * transparency, meaning that certain pixels are specified as being transparent
 * when drawn. Examples of file formats that support transparency are GIF and
 * PNG.
 * </p>
 * <p>
 * There are two primary ways to use <code>Images</code>. The first is to load a
 * graphic file from disk and create an <code>Image</code> from it. This is done
 * using an <code>Image</code> constructor, for example:
 * 
 * <pre>
 * Image i = new Image(device, &quot;C:\\graphic.bmp&quot;);
 * </pre>
 * 
 * A graphic file may contain a color table specifying which colors the image
 * was intended to possess. In the above example, these colors will be mapped to
 * the closest available color in SWT. It is possible to get more control over
 * the mapping of colors as the image is being created, using code of the form:
 * 
 * <pre>
 * ImageData data = new ImageData(&quot;C:\\graphic.bmp&quot;);
 * RGB[] rgbs = data.getRGBs();
 * // At this point, rgbs contains specifications of all
 * // the colors contained within this image. You may
 * // allocate as many of these colors as you wish by
 * // using the Color constructor Color(RGB), then
 * // create the image:
 * Image i = new Image(device, data);
 * </pre>
 * <p>
 * Applications which require even greater control over the image loading
 * process should use the support provided in class <code>ImageLoader</code>.
 * </p>
 * <p>
 * Application code must explicitly invoke the <code>Image.dispose()</code>
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 * 
 * @see Color
 * @see ImageData
 * @see ImageLoader
 * @see <a href="http://www.eclipse.org/swt/snippets/#image">Image snippets</a>
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Examples:
 *      GraphicsExample, ImageAnalyzer</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 */
public final class Image extends Resource implements Drawable {

	/**
	 * specifies whether the receiver is a bitmap or an icon (one of
	 * <code>SWT.BITMAP</code>, <code>SWT.ICON</code>)
	 * <p>
	 * <b>IMPORTANT:</b> This field is <em>not</em> part of the SWT public API.
	 * It is marked public only so that it can be shared within the packages
	 * provided by SWT. It is not available on all platforms and should never be
	 * accessed from application code.
	 * </p>
	 * 
	 * @noreference This field is not intended to be referenced by clients.
	 */
	public int type;

	/**
	 * The handle to the OS pixmap resource. (Warning: This field is platform
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
	public long /* int */pixmap;

	/**
	 * The handle to the OS mask resource. (Warning: This field is platform
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
	public long /* int */mask;

	long /* int */surface, surfaceData;

	/**
	 * specifies the transparent pixel
	 */
	int transparentPixel = -1;

	/**
	 * The GC the image is currently selected in.
	 */
	GC memGC;

	/**
	 * The alpha data of the image.
	 */
	byte[] alphaData;

	/**
	 * The global alpha value to be used for every pixel.
	 */
	int alpha = -1;

	/**
	 * The width of the image.
	 */
	int width = -1;

	/**
	 * The height of the image.
	 */
	int height = -1;

	/**
	 * Specifies the default scanline padding.
	 */
	static final int DEFAULT_SCANLINE_PAD = 4;

	com.google.gwt.user.client.ui.Image gwtImage;

	public com.google.gwt.user.client.ui.Image getGwtImage() {
		return gwtImage;
	}

	Image(Device device) {
		super(device);
	}

	/**
	 * Constructs an empty instance of this class with the specified width and
	 * height. The result may be drawn upon by creating a GC and using any of
	 * its drawing operations, as shown in the following example:
	 * 
	 * <pre>
	 * Image i = new Image(device, width, height);
	 * GC gc = new GC(i);
	 * gc.drawRectangle(0, 0, 50, 50);
	 * gc.dispose();
	 * </pre>
	 * <p>
	 * Note: Some platforms may have a limitation on the size of image that can
	 * be created (size depends on width, height, and depth). For example,
	 * Windows 95, 98, and ME do not allow images larger than 16M.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to create the image
	 * @param width
	 *            the width of the new image
	 * @param height
	 *            the height of the new image
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if either the width or height
	 *                is negative or zero</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */
	public Image(Device device, int width, int height) {
		super(device);
		init(width, height);
		init();
	}

	/**
	 * Constructs a new instance of this class based on the provided image, with
	 * an appearance that varies depending on the value of the flag. The
	 * possible flag values are:
	 * <dl>
	 * <dt><b>{@link SWT#IMAGE_COPY}</b></dt>
	 * <dd>the result is an identical copy of srcImage</dd>
	 * <dt><b>{@link SWT#IMAGE_DISABLE}</b></dt>
	 * <dd>the result is a copy of srcImage which has a <em>disabled</em> look</dd>
	 * <dt><b>{@link SWT#IMAGE_GRAY}</b></dt>
	 * <dd>the result is a copy of srcImage which has a <em>gray scale</em> look
	 * </dd>
	 * </dl>
	 * 
	 * @param device
	 *            the device on which to create the image
	 * @param srcImage
	 *            the image to use as the source
	 * @param flag
	 *            the style, either <code>IMAGE_COPY</code>,
	 *            <code>IMAGE_DISABLE</code> or <code>IMAGE_GRAY</code>
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if srcImage is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the flag is not one of
	 *                <code>IMAGE_COPY</code>, <code>IMAGE_DISABLE</code> or
	 *                <code>IMAGE_GRAY</code></li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the image has been
	 *                disposed</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or
	 *                an icon, or is otherwise in an invalid state</li>
	 *                <li>ERROR_UNSUPPORTED_DEPTH - if the depth of the image is
	 *                not supported</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */
	public Image(Device device, Image srcImage, int flag) {
	}

	/**
	 * Constructs an empty instance of this class with the width and height of
	 * the specified rectangle. The result may be drawn upon by creating a GC
	 * and using any of its drawing operations, as shown in the following
	 * example:
	 * 
	 * <pre>
	 * Image i = new Image(device, boundsRectangle);
	 * GC gc = new GC(i);
	 * gc.drawRectangle(0, 0, 50, 50);
	 * gc.dispose();
	 * </pre>
	 * <p>
	 * Note: Some platforms may have a limitation on the size of image that can
	 * be created (size depends on width, height, and depth). For example,
	 * Windows 95, 98, and ME do not allow images larger than 16M.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to create the image
	 * @param bounds
	 *            a rectangle specifying the image's width and height (must not
	 *            be null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the bounds rectangle is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if either the rectangle's
	 *                width or height is negative</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */
	public Image(Device device, Rectangle bounds) {
		super(device);
		if (bounds == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		init(bounds.width, bounds.height);
		init();
	}

	/**
	 * Constructs an instance of this class from the given
	 * <code>ImageData</code>.
	 * 
	 * @param device
	 *            the device on which to create the image
	 * @param data
	 *            the image data to create the image from (must not be null)
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the image data is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_UNSUPPORTED_DEPTH - if the depth of the
	 *                ImageData is not supported</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */
	public Image(Device device, ImageData data) {
		super(device);
		init(data);
		init();
	}

	/**
	 * Constructs an instance of this class, whose type is <code>SWT.ICON</code>
	 * , from the two given <code>ImageData</code> objects. The two images must
	 * be the same size. Pixel transparency in either image will be ignored.
	 * <p>
	 * The mask image should contain white wherever the icon is to be visible,
	 * and black wherever the icon is to be transparent. In addition, the source
	 * image should contain black wherever the icon is to be transparent.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to create the icon
	 * @param source
	 *            the color data for the icon
	 * @param mask
	 *            the mask data for the icon
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if either the source or mask is
	 *                null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if source and mask are
	 *                different sizes</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */
	public Image(Device device, ImageData source, ImageData mask) {
	}

	/**
	 * Constructs an instance of this class by loading its representation from
	 * the specified input stream. Throws an error if an error occurs while
	 * loading the image, or if the result is an image of an unsupported type.
	 * Application code is still responsible for closing the input stream.
	 * <p>
	 * This constructor is provided for convenience when loading a single image
	 * only. If the stream contains multiple images, only the first one will be
	 * loaded. To load multiple images, use <code>ImageLoader.load()</code>.
	 * </p>
	 * <p>
	 * This constructor may be used to load a resource as follows:
	 * </p>
	 * 
	 * <pre>
	 * static Image loadImage(Display display, Class clazz, String string) {
	 * 	InputStream stream = clazz.getResourceAsStream(string);
	 * 	if (stream == null)
	 * 		return null;
	 * 	Image image = null;
	 * 	try {
	 * 		image = new Image(display, stream);
	 * 	} catch (SWTException ex) {
	 * 	} finally {
	 * 		try {
	 * 			stream.close();
	 * 		} catch (IOException ex) {
	 * 		}
	 * 	}
	 * 	return image;
	 * }
	 * </pre>
	 * 
	 * @param device
	 *            the device on which to create the image
	 * @param stream
	 *            the input stream to load the image from
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                <li>ERROR_NULL_ARGUMENT - if the stream is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_IO - if an IO error occurs while reading from
	 *                the stream</li>
	 *                <li>ERROR_INVALID_IMAGE - if the image stream contains
	 *                invalid data</li>
	 *                <li>ERROR_UNSUPPORTED_DEPTH - if the image stream
	 *                describes an image with an unsupported depth</li>
	 *                <li>ERROR_UNSUPPORTED_FORMAT - if the image stream
	 *                contains an unrecognized format</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */

	/**
	 * TODO
	 * 
	 */
	// public Image(Device device, InputStream stream) {
	// super(device);
	// init(new ImageData(stream));
	// init();
	// }

	/**
	 * Constructs an instance of this class by loading its representation from
	 * the file with the specified name. Throws an error if an error occurs
	 * while loading the image, or if the result is an image of an unsupported
	 * type.
	 * <p>
	 * This constructor is provided for convenience when loading a single image
	 * only. If the specified file contains multiple images, only the first one
	 * will be used.
	 * 
	 * @param device
	 *            the device on which to create the image
	 * @param filename
	 *            the name of the file to load the image from
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li> <li>ERROR_NULL_ARGUMENT - if the
	 *                file name is null</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_IO - if an IO error occurs while reading from
	 *                the file</li> <li>ERROR_INVALID_IMAGE - if the image file
	 *                contains invalid data </li> <li>ERROR_UNSUPPORTED_DEPTH -
	 *                if the image file describes an image with an unsupported
	 *                depth</li> <li>ERROR_UNSUPPORTED_FORMAT - if the image
	 *                file contains an unrecognized format</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle could not be obtained for
	 *                image creation</li>
	 *                </ul>
	 */
	public Image(Device device, String filename) {
		super(device);
		if (filename == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		initNative(filename);
		if (this.pixmap == 0)
			init(new ImageData(filename));
		init();
	}

	public Image(Object object, Object stream) {
		// TODO Auto-generated constructor stub
		if (stream instanceof ImageResource) {
			gwtImage = new com.google.gwt.user.client.ui.Image(
					(ImageResource) stream);
		} else if (stream instanceof String) {
			gwtImage = new com.google.gwt.user.client.ui.Image((String) stream);
		}
	}

	void initNative(String filename) {
	}

	void createAlphaMask(int width, int height) {
	}

	/**
	 * Create the receiver's mask if necessary.
	 */
	void createMask() {
		if (mask != 0)
			return;
		mask = createMask(getImageData(), false);
		if (mask == 0)
			SWT.error(SWT.ERROR_NO_HANDLES);
	}

	long /* int */createMask(ImageData image, boolean copy) {
		return 0;
	}

	void createSurface() {
	}

	/**
	 * Destroy the receiver's mask if it exists.
	 */
	void destroyMask() {
	}

	void destroy() {
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
		if (object == this)
			return true;
		if (!(object instanceof Image))
			return false;
		Image image = (Image) object;
		return device == image.device && pixmap == image.pixmap;
	}

	/**
	 * Returns the color to which to map the transparent pixel, or null if the
	 * receiver has no transparent pixel.
	 * <p>
	 * There are certain uses of Images that do not support transparency (for
	 * example, setting an image into a button or label). In these cases, it may
	 * be desired to simulate transparency by using the background color of the
	 * widget to paint the transparent pixels of the image. Use this method to
	 * check which color will be used in these cases in place of transparency.
	 * This value may be set with setBackground().
	 * <p>
	 * 
	 * @return the background color of the image, or null if there is no
	 *         transparency in the image
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public Color getBackground() {
		if (isDisposed())
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		if (transparentPixel == -1)
			return null;
		// NOT DONE
		return null;
	}

	/**
	 * Returns the bounds of the receiver. The rectangle will always have x and
	 * y values of 0, and the width and height of the image.
	 * 
	 * @return a rectangle specifying the image's bounds
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or
	 *                an icon</li>
	 *                </ul>
	 */
	public Rectangle getBounds() {
		if (gwtImage != null) {
			return new Rectangle(0, 0, gwtImage.getWidth(),
					gwtImage.getHeight());
		}
		System.out.println("Image.getBounds() returns (0,0,0,0).");
		return new Rectangle(0, 0, 0, 0);
	}

	/**
	 * Returns an <code>ImageData</code> based on the receiver Modifications
	 * made to this <code>ImageData</code> will not affect the Image.
	 * 
	 * @return an <code>ImageData</code> containing the image's data and
	 *         attributes
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_INVALID_IMAGE - if the image is not a bitmap or
	 *                an icon</li>
	 *                </ul>
	 * 
	 * @see ImageData
	 */
	public ImageData getImageData() {
		return null;
	}

	/**
	 * Invokes platform specific functionality to allocate a new image.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Image</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to allocate the color
	 * @param type
	 *            the type of the image (<code>SWT.BITMAP</code> or
	 *            <code>SWT.ICON</code>)
	 * @param pixmap
	 *            the OS handle for the image
	 * @param mask
	 *            the OS handle for the image mask
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public static Image gtk_new(Device device, int type, long /* int */pixmap,
			long /* int */mask) {
		Image image = new Image(device);
		image.type = type;
		image.pixmap = pixmap;
		image.mask = mask;
		return image;
	}

	/**
	 * Returns an integer hash code for the receiver. Any two objects that
	 * return <code>true</code> when passed to <code>equals</code> must return
	 * the same value for this method.
	 * 
	 * @return the receiver's hash
	 * 
	 * @see #equals
	 */
	public int hashCode() {
		return (int) /* 64 */pixmap;
	}

	void init(int width, int height) {
	}

	void init(ImageData image) {
	}

	/**
	 * Invokes platform specific functionality to allocate a new GC handle.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Image</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param data
	 *            the platform specific GC data
	 * @return the platform specific GC handle
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public long /* int */internal_new_GC(GCData data) {
		return 0;
	}

	/**
	 * Invokes platform specific functionality to dispose a GC handle.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public API for
	 * <code>Image</code>. It is marked public only so that it can be shared
	 * within the packages provided by SWT. It is not available on all
	 * platforms, and should never be called from application code.
	 * </p>
	 * 
	 * @param hDC
	 *            the platform specific GC handle
	 * @param data
	 *            the platform specific GC data
	 * 
	 * @noreference This method is not intended to be referenced by clients.
	 */
	public void internal_dispose_GC(long /* int */gdkGC, GCData data) {
	}

	/**
	 * Returns <code>true</code> if the image has been disposed, and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the image. When an image has been
	 * disposed, it is an error to invoke any other method (except
	 * {@link #dispose()}) using the image.
	 * 
	 * @return <code>true</code> when the image is disposed and
	 *         <code>false</code> otherwise
	 */
	public boolean isDisposed() {
		return pixmap == 0;
	}

	/**
	 * Sets the color to which to map the transparent pixel.
	 * <p>
	 * There are certain uses of <code>Images</code> that do not support
	 * transparency (for example, setting an image into a button or label). In
	 * these cases, it may be desired to simulate transparency by using the
	 * background color of the widget to paint the transparent pixels of the
	 * image. This method specifies the color that will be used in these cases.
	 * For example:
	 * 
	 * <pre>
	 * Button b = new Button();
	 * image.setBackground(b.getBackground());
	 * b.setImage(image);
	 * </pre>
	 * 
	 * </p>
	 * <p>
	 * The image may be modified by this operation (in effect, the transparent
	 * regions may be filled with the supplied color). Hence this operation is
	 * not reversible and it is not legal to call this function twice or with a
	 * null argument.
	 * </p>
	 * <p>
	 * This method has no effect if the receiver does not have a transparent
	 * pixel value.
	 * </p>
	 * 
	 * @param color
	 *            the color to use when a transparent pixel is specified
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
		if (isDisposed())
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		if (color == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (color.isDisposed())
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		if (transparentPixel == -1)
			return;
		// NOT DONE
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		if (isDisposed())
			return "Image {*DISPOSED*}";
		return "Image {" + pixmap + "}";
	}

}
