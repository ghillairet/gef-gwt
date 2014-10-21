package org.eclipse.swt.events;

import java.util.EventListener;

/**
 * This interface is the cross-platform version of the java.util.EventListener
 * interface.
 * <p>
 * It is part of our effort to provide support for both J2SE and J2ME platforms.
 * Under this scheme, classes need to implement SWTEventListener instead of
 * java.util.EventListener.
 * </p>
 * <p>
 * Note: java.util.EventListener is not part of CDC and CLDC.
 * </p>
 */
public interface SWTEventListener extends EventListener {
}
