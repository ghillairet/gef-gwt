package org.eclipse.swt.internal.gwt;

public class GdkEventKey extends GdkEvent {
	/** @field cast=(GdkWindow *) */
	public long /* int */window;
	/** @field cast=(gint8) */
	public byte send_event;
	/** @field cast=(guint32) */
	public int time;
	/** @field cast=(guint) */
	public int state;
	/** @field cast=(guint) */
	public int keyval;
	/** @field cast=(gint) */
	public int length;
	/** @field cast=(gchar *) */
	public long /* int */string;
	/** @field cast=(guint16) */
	public short hardware_keycode;
	/** @field cast=(guint8) */
	public byte group;
}
