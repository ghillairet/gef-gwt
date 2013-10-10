package org.eclipse.swt.internal.gwt;

public abstract class XAnyEvent extends XEvent {
	public long /* int */serial;
	public int send_event;
	/** @field cast=(Display *) */
	public long /* int */display;
	/** @field cast=(Window) */
	public long /* int */window;
}
