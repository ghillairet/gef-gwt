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

/**
 * Instances of this class represent transformation matrices for points
 * expressed as (x, y) pairs of floating point numbers.
 * <p>
 * Application code must explicitly invoke the <code>Transform.dispose()</code>
 * method to release the operating system resources managed by each instance
 * when those instances are no longer required.
 * </p>
 * <p>
 * This class requires the operating system's advanced graphics subsystem which
 * may not be available on some platforms.
 * </p>
 * 
 * @see <a href="http://www.eclipse.org/swt/examples.php">SWT Example:
 *      GraphicsExample</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further
 *      information</a>
 * 
 * @since 3.1
 */
public class Transform extends Resource {
	/**
	 * the OS resource for the Transform (Warning: This field is platform
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
	public double[] handle;

	/**
	 * Constructs a new identity Transform.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to allocate the Transform
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle for the Transform could
	 *                not be obtained</li>
	 *                </ul>
	 * 
	 * @see #dispose()
	 */
	public Transform(Device device) {
		this(device, 1, 0, 0, 1, 0, 0);
	}

	/**
	 * Constructs a new Transform given an array of elements that represent the
	 * matrix that describes the transformation.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to allocate the Transform
	 * @param elements
	 *            an array of floats that describe the transformation matrix
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device, or the elements array is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the elements array is too
	 *                small to hold the matrix values</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle for the Transform could
	 *                not be obtained</li>
	 *                </ul>
	 * 
	 * @see #dispose()
	 */
	public Transform(Device device, float[] elements) {
		this(device, checkTransform(elements)[0], elements[1], elements[2],
				elements[3], elements[4], elements[5]);
	}

	/**
	 * Constructs a new Transform given all of the elements that represent the
	 * matrix that describes the transformation.
	 * <p>
	 * This operation requires the operating system's advanced graphics
	 * subsystem which may not be available on some platforms.
	 * </p>
	 * 
	 * @param device
	 *            the device on which to allocate the Transform
	 * @param m11
	 *            the first element of the first row of the matrix
	 * @param m12
	 *            the second element of the first row of the matrix
	 * @param m21
	 *            the first element of the second row of the matrix
	 * @param m22
	 *            the second element of the second row of the matrix
	 * @param dx
	 *            the third element of the first row of the matrix
	 * @param dy
	 *            the third element of the second row of the matrix
	 * 
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if device is null and there is
	 *                no current device</li>
	 *                </ul>
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_NO_GRAPHICS_LIBRARY - if advanced graphics are
	 *                not available</li>
	 *                </ul>
	 * @exception SWTError
	 *                <ul>
	 *                <li>ERROR_NO_HANDLES if a handle for the Transform could
	 *                not be obtained</li>
	 *                </ul>
	 * 
	 * @see #dispose()
	 */
	public Transform(Device device, float m11, float m12, float m21, float m22,
			float dx, float dy) {
		setElements(m11, m12, m21, m22, dx, dy);
	}

	static float[] checkTransform(float[] elements) {
		if (elements == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (elements.length < 6)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		return elements;
	}

	void destroy() {
		handle = null;
	}

	/**
	 * Fills the parameter with the values of the transformation matrix that the
	 * receiver represents, in the order {m11, m12, m21, m22, dx, dy}.
	 * 
	 * @param elements
	 *            array to hold the matrix values
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parameter is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter is too small
	 *                to hold the matrix values</li>
	 *                </ul>
	 */
	public void getElements(float[] elements) {
		if (isDisposed())
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		if (elements == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		if (elements.length < 6)
			SWT.error(SWT.ERROR_INVALID_ARGUMENT);
		elements[0] = (float) handle[0];
		elements[1] = (float) handle[1];
		elements[2] = (float) handle[2];
		elements[3] = (float) handle[3];
		elements[4] = (float) handle[4];
		elements[5] = (float) handle[5];
	}

	/**
	 * Modifies the receiver such that the matrix it represents becomes the
	 * identity matrix.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.4
	 */
	public void identity() {
		setElements(1, 0, 0, 1, 0, 0);
	}

	/**
	 * Modifies the receiver such that the matrix it represents becomes the
	 * mathematical inverse of the matrix it previously represented.
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_CANNOT_INVERT_MATRIX - if the matrix is not
	 *                invertible</li>
	 *                </ul>
	 */
	public void invert() {
	}

	/**
	 * Returns <code>true</code> if the Transform has been disposed, and
	 * <code>false</code> otherwise.
	 * <p>
	 * This method gets the dispose state for the Transform. When a Transform
	 * has been disposed, it is an error to invoke any other method (except
	 * {@link #dispose()}) using the Transform.
	 * 
	 * @return <code>true</code> when the Transform is disposed, and
	 *         <code>false</code> otherwise
	 */
	public boolean isDisposed() {
		// return handle == null;
		return false;
	}

	/**
	 * Returns <code>true</code> if the Transform represents the identity matrix
	 * and false otherwise.
	 * 
	 * @return <code>true</code> if the receiver is an identity Transform, and
	 *         <code>false</code> otherwise
	 */
	public boolean isIdentity() {
		if (isDisposed())
			SWT.error(SWT.ERROR_GRAPHIC_DISPOSED);
		float[] m = new float[6];
		getElements(m);
		return m[0] == 1 && m[1] == 0 && m[2] == 0 && m[3] == 1 && m[4] == 0
				&& m[5] == 0;
	}

	/**
	 * Modifies the receiver such that the matrix it represents becomes the the
	 * result of multiplying the matrix it previously represented by the
	 * argument.
	 * 
	 * @param matrix
	 *            the matrix to multiply the receiver by
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * @exception IllegalArgumentException
	 *                <ul>
	 *                <li>ERROR_NULL_ARGUMENT - if the parameter is null</li>
	 *                <li>ERROR_INVALID_ARGUMENT - if the parameter has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void multiply(Transform matrix) {
	}

	/**
	 * Modifies the receiver so that it represents a transformation that is
	 * equivalent to its previous transformation rotated by the specified angle.
	 * The angle is specified in degrees and for the identity transform 0
	 * degrees is at the 3 o'clock position. A positive value indicates a
	 * clockwise rotation while a negative value indicates a counter-clockwise
	 * rotation.
	 * 
	 * @param angle
	 *            the angle to rotate the transformation by
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void rotate(float angle) {

		double[] rotate = new double[4];
		double radians = -Math.toRadians(angle);
		rotate[0] = Math.cos(radians);
		rotate[1] = Math.sin(radians);
		rotate[2] = -Math.sin(radians);
		rotate[3] = Math.cos(radians);
		double a = handle[0] * rotate[0] + handle[1] * rotate[2];
		double b = handle[0] * rotate[1] + handle[1] * rotate[3];
		double c = handle[2] * rotate[0] + handle[3] * rotate[2];
		double d = handle[2] * rotate[1] + handle[3] * rotate[3];
		handle[0] = a;
		handle[1] = b;
		handle[2] = c;
		handle[3] = d;
	}

	/**
	 * Modifies the receiver so that it represents a transformation that is
	 * equivalent to its previous transformation scaled by (scaleX, scaleY).
	 * 
	 * @param scaleX
	 *            the amount to scale in the X direction
	 * @param scaleY
	 *            the amount to scale in the Y direction
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void scale(float scaleX, float scaleY) {
		handle[0] = handle[0] * scaleX;
		handle[2] = handle[2] * scaleX;
		handle[1] = handle[1] * scaleY;
		handle[3] = handle[3] * scaleY;
	}

	/**
	 * Modifies the receiver to represent a new transformation given all of the
	 * elements that represent the matrix that describes that transformation.
	 * 
	 * @param m11
	 *            the first element of the first row of the matrix
	 * @param m12
	 *            the second element of the first row of the matrix
	 * @param m21
	 *            the first element of the second row of the matrix
	 * @param m22
	 *            the second element of the second row of the matrix
	 * @param dx
	 *            the third element of the first row of the matrix
	 * @param dy
	 *            the third element of the second row of the matrix
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void setElements(float m11, float m12, float m21, float m22,
			float dx, float dy) {
		if (handle == null) {
			handle = new double[6];
		}
		handle[0] = m11;
		handle[1] = m12;
		handle[2] = m21;
		handle[3] = m22;
		handle[4] = dx;
		handle[5] = dy;
	}

	/**
	 * Modifies the receiver so that it represents a transformation that is
	 * equivalent to its previous transformation sheared by (shearX, shearY).
	 * 
	 * @param shearX
	 *            the shear factor in the X direction
	 * @param shearY
	 *            the shear factor in the Y direction
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 * 
	 * @since 3.4
	 */
	public void shear(float shearX, float shearY) {
	}

	/**
	 * Given an array containing points described by alternating x and y values,
	 * modify that array such that each point has been replaced with the result
	 * of applying the transformation represented by the receiver to that point.
	 * 
	 * @param pointArray
	 *            an array of alternating x and y values to be transformed
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
	public void transform(float[] pointArray) {
	}

	/**
	 * Modifies the receiver so that it represents a transformation that is
	 * equivalent to its previous transformation translated by (offsetX,
	 * offsetY).
	 * 
	 * @param offsetX
	 *            the distance to translate in the X direction
	 * @param offsetY
	 *            the distance to translate in the Y direction
	 * 
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_GRAPHIC_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                </ul>
	 */
	public void translate(float offsetX, float offsetY) {
		handle[4] = handle[0] * offsetX + handle[1] * offsetY + handle[4];
		handle[5] = handle[2] * offsetX + handle[3] * offsetY + handle[5];
	}

	/**
	 * Returns a string containing a concise, human-readable description of the
	 * receiver.
	 * 
	 * @return a string representation of the receiver
	 */
	public String toString() {
		if (isDisposed())
			return "Transform {*DISPOSED*}";
		float[] elements = new float[6];
		getElements(elements);
		return "Transform {" + elements[0] + "," + elements[1] + ","
				+ elements[2] + "," + elements[3] + "," + elements[4] + ","
				+ elements[5] + "}";
	}

	public double[] getMatrix() {
		return handle;
	}

}
