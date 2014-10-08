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
package org.eclipse.draw2d;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A scrolling Canvas that contains {@link Figure Figures} viewed through a
 * {@link Viewport}. Call {@link #setContents(IFigure)} to specify the root of
 * the tree of <tt>Figures</tt> to be viewed through the <tt>Viewport</tt>.
 * <p>
 * Normal procedure for using a FigureCanvas:
 * <ol>
 * <li>Create a FigureCanvas.
 * <li>Create a Draw2d Figure and call {@link #setContents(IFigure)}. This
 * Figure will be the top-level Figure of the Draw2d application.
 * </ol>
 * <dl>
 * <dt><b>Required Styles (when using certain constructors):</b></dt>
 * <dd>V_SCROLL, H_SCROLL, NO_REDRAW_RESIZE</dd>
 * <dt><b>Optional Styles:</b></dt>
 * <dd>DOUBLE_BUFFERED, RIGHT_TO_LEFT, LEFT_TO_RIGHT, NO_BACKGROUND, BORDER</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles RIGHT_TO_LEFT, LEFT_TO_RIGHT may be specified.
 * </p>
 */
public class FigureCanvas extends Canvas {

	private static final int ACCEPTED_STYLES = SWT.RIGHT_TO_LEFT
			| SWT.LEFT_TO_RIGHT | SWT.V_SCROLL | SWT.H_SCROLL
			| SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED
			| SWT.BORDER;

	/**
	 * The default styles are mixed in when certain constructors are used. This
	 * constant is a bitwise OR of the following SWT style constants:
	 * <UL>
	 * <LI>{@link SWT#NO_REDRAW_RESIZE}</LI>
	 * <LI>{@link SWT#NO_BACKGROUND}</LI>
	 * <LI>{@link SWT#V_SCROLL}</LI>
	 * <LI>{@link SWT#H_SCROLL}</LI>
	 * </UL>
	 */
	static final int DEFAULT_STYLES = SWT.NO_REDRAW_RESIZE | SWT.NO_BACKGROUND
			| SWT.V_SCROLL | SWT.H_SCROLL;

	private static final int REQUIRED_STYLES = SWT.NO_REDRAW_RESIZE
			| SWT.V_SCROLL | SWT.H_SCROLL;

	/** Never show scrollbar */
	public static int NEVER = 0;
	/** Automatically show scrollbar when needed */
	public static int AUTOMATIC = 1;
	/** Always show scrollbar */
	public static int ALWAYS = 2;

	private int vBarVisibility = AUTOMATIC;
	private int hBarVisibility = AUTOMATIC;
	private Viewport viewport;
	private Font font;
	private int hBarOffset;
	private int vBarOffset;

	private PropertyChangeListener horizontalChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			RangeModel model = getViewport().getHorizontalRangeModel();
			hBarOffset = Math.max(0, -model.getMinimum());
			getHorizontalBar().setValues(model.getValue() + hBarOffset,
					model.getMinimum() + hBarOffset,
					model.getMaximum() + hBarOffset, model.getExtent(),
					Math.max(1, model.getExtent() / 20),
					Math.max(1, model.getExtent() * 3 / 4));
		}
	};

	private PropertyChangeListener verticalChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			RangeModel model = getViewport().getVerticalRangeModel();
			vBarOffset = Math.max(0, -model.getMinimum());
			getVerticalBar().setValues(model.getValue() + vBarOffset,
					model.getMinimum() + vBarOffset,
					model.getMaximum() + vBarOffset, model.getExtent(),
					Math.max(1, model.getExtent() / 20),
					Math.max(1, model.getExtent() * 3 / 4));
		}
	};

	public final int SCROLL_SIZE;

	private PropertyChangeListener horizontalChangeListenerGWT = new PropertyChangeListener() {

		private boolean scrollbarsVisible = false;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			com.google.gwt.canvas.client.Canvas canvas = (com.google.gwt.canvas.client.Canvas) getNativeWidget();
			LayoutPanel canvasParent = (LayoutPanel) canvas.getParent();
			if (isInPalette(canvas)) {
				return;
			}
			int defWidth = canvasParent.getOffsetWidth();
			RangeModel model = (RangeModel) evt.getSource();
			int maxVal = model.getMaximum();
			int minVal = model.getMinimum();
			int value = model.getValue();
			int size = maxVal - minVal;
			LayoutPanel simplePanel = getSimplePanel();
			simplePanel.setWidth(size + "px");
			ScrollPanel parent = (ScrollPanel) simplePanel.getParent();
			Element canvasContainerElement = canvasParent
					.getWidgetContainerElement(canvas);
			if ((minVal < 0 || maxVal > defWidth) && defWidth > 0
					&& scrollbarsVisible == false) {
				canvasContainerElement.getStyle().setBottom(SCROLL_SIZE,
						Unit.PX);
				onResize();
				scrollbarsVisible = true;
			} else if (minVal >= 0 && maxVal <= defWidth
					&& scrollbarsVisible == true) {
				canvasContainerElement.getStyle().setBottom(0, Unit.PX);
				onResize();
				scrollbarsVisible = false;
			}
			parent.setHorizontalScrollPosition(value - minVal);
			// There is a missing event that isn't sent:
			scroll(parent);
			getLightweightSystem().getRootFigure().repaint();
		}

	};

	private PropertyChangeListener verticalChangeListenerGWT = new PropertyChangeListener() {

		private boolean scrollbarsVisible = false;

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			com.google.gwt.canvas.client.Canvas canvas = (com.google.gwt.canvas.client.Canvas) getNativeWidget();
			LayoutPanel canvasParent = (LayoutPanel) canvas.getParent();
			if (isInPalette(canvas)) {
				return;
			}
			int defHeight = canvasParent.getOffsetHeight();
			RangeModel model = (RangeModel) evt.getSource();
			int maxVal = model.getMaximum();
			int minVal = model.getMinimum();
			int size = maxVal - minVal;
			LayoutPanel simplePanel = getSimplePanel();
			simplePanel.setHeight(size + "px");
			ScrollPanel parent = (ScrollPanel) simplePanel.getParent();
			parent.setVerticalScrollPosition(model.getValue() - minVal);
			Element canvasContainerElement = canvasParent
					.getWidgetContainerElement(canvas);
			if ((minVal < 0 || maxVal > defHeight && defHeight > 0)
					&& scrollbarsVisible == false) {
				canvasContainerElement.getStyle()
				.setRight(SCROLL_SIZE, Unit.PX);
				onResize();
				scrollbarsVisible = true;
			} else if (minVal >= 0 && maxVal <= defHeight
					&& scrollbarsVisible == true) {
				canvasContainerElement.getStyle().setRight(0, Unit.PX);
				onResize();
				scrollbarsVisible = false;
			}
			// There is a missing event that isn't sent:
			scroll(parent);
			getLightweightSystem().getRootFigure().repaint();
		}
	};

	private boolean isInPalette(Widget widget) {
		Widget parent = widget.getParent();
		if (parent == null) {
			return false;
		} else {
			if (parent.getElement().getId().equals("PALETTE_PANEL")) {
				return true;
			} else {
				return isInPalette(parent);
			}
		}
	}

	private final LightweightSystem lws;

	/**
	 * Creates a new FigureCanvas with the given parent and the
	 * {@link #DEFAULT_STYLES}.
	 * 
	 * @param parent
	 *            the parent
	 */
	public FigureCanvas(Composite parent) {
		this(parent, SWT.DOUBLE_BUFFERED, new LightweightSystem());
	}

	public FigureCanvas(com.google.gwt.canvas.client.Canvas canvas) {
		super(canvas);
		SCROLL_SIZE = 17;
		this.lws = new LightweightSystem();
		this.lws.setControl(this);
		getHorizontalBar().setVisible(false);
		getVerticalBar().setVisible(false);
		hook();
	}
	
	public FigureCanvas(com.google.gwt.canvas.client.Canvas canvas, LightweightSystem system) {
		super(canvas);
		SCROLL_SIZE = 17;
		this.lws = system;
		this.lws.setControl(this);
		getHorizontalBar().setVisible(false);
		getVerticalBar().setVisible(false);
		hook();
	}

	/**
	 * Constructor which applies the default styles plus any optional styles
	 * indicated.
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            see the class javadoc for optional styles
	 * @since 3.1
	 */
	public FigureCanvas(Composite parent, int style) {
		this(new ScrolledComposite(parent, style), style,
				new LightweightSystem());
	}

	/**
	 * Constructor which uses the given styles verbatim. Certain styles must be
	 * used with this class. Refer to the class javadoc for more details.
	 * 
	 * @param style
	 *            see the class javadoc for <b>required</b> and optional styles
	 * @param parent
	 *            the parent composite
	 * @since 3.4
	 */
	public FigureCanvas(int style, Composite parent) {
		this(style, parent, new LightweightSystem());
	}

	/**
	 * Constructs a new FigureCanvas with the given parent and
	 * LightweightSystem, using the {@link #DEFAULT_STYLES}.
	 * 
	 * @param parent
	 *            the parent
	 * @param lws
	 *            the LightweightSystem
	 */
	public FigureCanvas(Composite parent, LightweightSystem lws) {
		this(DEFAULT_STYLES, parent, lws);
	}

	/**
	 * Constructor taking a lightweight system and SWT style, which is used
	 * verbatim. Certain styles must be used with this class. Refer to the class
	 * javadoc for more details.
	 * 
	 * @param style
	 *            see the class javadoc for <b>required</b> and optional styles
	 * @param parent
	 *            the parent composite
	 * @param lws
	 *            the LightweightSystem
	 * @since 3.4
	 */
	public FigureCanvas(int style, Composite parent, LightweightSystem lws) {
		super(parent, checkStyle(style));
		SCROLL_SIZE = 17;
		getHorizontalBar().setVisible(false);
		getVerticalBar().setVisible(false);
		this.lws = lws;
		lws.setControl(this);
		hook();
	}

	private void hookGWTScrolling() {
		Viewport vp = getViewport();

		vp.getHorizontalRangeModel().addPropertyChangeListener(horizontalChangeListenerGWT);
		vp.getVerticalRangeModel().addPropertyChangeListener(verticalChangeListenerGWT);
	}

	private void unhookGWTScrolling() {
		Viewport vp = getViewport();

		vp.getHorizontalRangeModel().removePropertyChangeListener(horizontalChangeListenerGWT);
		vp.getVerticalRangeModel().removePropertyChangeListener(verticalChangeListenerGWT);
	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            the parent composite
	 * @param style
	 *            look at class javadoc for valid styles
	 * @param lws
	 *            the lightweight system
	 * @since 3.1
	 */
	public FigureCanvas(Composite parent, int style, LightweightSystem lws) {
		this(style | DEFAULT_STYLES, parent, lws);
	}

	private static int checkStyle(int style) {
		if ((style & REQUIRED_STYLES) != REQUIRED_STYLES)
			throw new IllegalArgumentException(
					"Required style missing on FigureCanvas"); //$NON-NLS-1$
		if ((style & ~ACCEPTED_STYLES) != 0)
			throw new IllegalArgumentException(
					"Invalid style being set on FigureCanvas"); //$NON-NLS-1$
		return style;
	}

	/**
	 * @see org.eclipse.swt.widgets.Composite#computeSize(int, int, boolean)
	 */
	public org.eclipse.swt.graphics.Point computeSize(int wHint, int hHint,
			boolean changed) {
		// TODO Still doesn't handle scrollbar cases, such as when a constrained
		// width
		// would require a horizontal scrollbar, and therefore additional
		// height.
		int borderSize = computeTrim(0, 0, 0, 0).x * -2;
		if (wHint >= 0)
			wHint = Math.max(0, wHint - borderSize);
		if (hHint >= 0)
			hHint = Math.max(0, hHint - borderSize);
		Dimension size = getLightweightSystem().getRootFigure()
				.getPreferredSize(wHint, hHint)
				.getExpanded(borderSize, borderSize);
		size.union(new Dimension(wHint, hHint));
		return new org.eclipse.swt.graphics.Point(size.width, size.height);
	}

	/**
	 * @return the contents of the {@link Viewport}.
	 */
	public IFigure getContents() {
		return getViewport().getContents();
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#getFont()
	 */
	public Font getFont() {
		if (font == null)
			font = super.getFont();
		return font;
	}

	/**
	 * @return the horizontal scrollbar visibility.
	 */
	public int getHorizontalScrollBarVisibility() {
		return hBarVisibility;
	}

	/**
	 * @return the LightweightSystem
	 */
	public LightweightSystem getLightweightSystem() {
		return lws;
	}

	/**
	 * @return the vertical scrollbar visibility.
	 */
	public int getVerticalScrollBarVisibility() {
		return vBarVisibility;
	}

	/**
	 * Returns the Viewport. If it's <code>null</code>, a new one is created.
	 * 
	 * @return the viewport
	 */
	public Viewport getViewport() {
		if (viewport == null)
			setViewport(new Viewport(true));
		return viewport;
	}

	/**
	 * Adds listeners for scrolling.
	 */
	private void hook() {
		getLightweightSystem().getUpdateManager().addUpdateListener(
				new UpdateListener() {
					public void notifyPainting(Rectangle damage, java.util.Map dirtyRegions) {
					}

					public void notifyValidating() {
						if (!isDisposed())
							layoutViewport();
					}
				});

		getHorizontalBar().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollToX(getHorizontalBar().getSelection() - hBarOffset);
			}
		});

		getVerticalBar().addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				scrollToY(getVerticalBar().getSelection() - vBarOffset);
			}
		});

		if (getSimplePanel() != null) {
			final ScrollPanel sp = (ScrollPanel) getSimplePanel().getParent();

			if (sp != null) {
				sp.addScrollHandler(new ScrollHandler() {
					@Override
					public void onScroll(ScrollEvent event) {
						scroll(sp);
					}
				});
			}
		}
	}

	private void scroll(ScrollPanel sp) {
		int hsp = sp.getHorizontalScrollPosition();
		int vsp = sp.getVerticalScrollPosition();

		int hminimum = getViewport().getHorizontalRangeModel().getMinimum();
		int vminimum = getViewport().getVerticalRangeModel().getMinimum();

		scrollTo(hminimum + hsp, vminimum + vsp);
	}

	private void hookViewport() {
		getViewport().getHorizontalRangeModel().addPropertyChangeListener(
				horizontalChangeListener);
		getViewport().getVerticalRangeModel().addPropertyChangeListener(
				verticalChangeListener);
		hookGWTScrolling();
	}

	private void unhookViewport() {
		getViewport().getHorizontalRangeModel().removePropertyChangeListener(
				horizontalChangeListener);
		getViewport().getVerticalRangeModel().removePropertyChangeListener(
				verticalChangeListener);
		unhookGWTScrolling();
	}

	private void layoutViewport() {
		ScrollPaneSolver.Result result;
		result = ScrollPaneSolver.solve(
				new Rectangle(getBounds()).setLocation(0, 0), getViewport(),
				getHorizontalScrollBarVisibility(),
				getVerticalScrollBarVisibility(),
				computeTrim(0, 0, 0, 0).width, computeTrim(0, 0, 0, 0).height);
		getLightweightSystem().setIgnoreResize(true);
		try {
			if (getHorizontalBar().getVisible() != result.showH)
				getHorizontalBar().setVisible(result.showH);
			if (getVerticalBar().getVisible() != result.showV)
				getVerticalBar().setVisible(result.showV);
			Rectangle r = new Rectangle(getBounds());
			r.setLocation(0, 0);
			getLightweightSystem().getRootFigure().setBounds(r);
		} finally {
			getLightweightSystem().setIgnoreResize(false);
		}
	}

	/**
	 * Scrolls in an animated way to the new x and y location.
	 * 
	 * @param x
	 *            the x coordinate to scroll to
	 * @param y
	 *            the y coordinate to scroll to
	 */
	public void scrollSmoothTo(int x, int y) {
		// Ensure newHOffset and newVOffset are within the appropriate ranges
		x = verifyScrollBarOffset(getViewport().getHorizontalRangeModel(), x);
		y = verifyScrollBarOffset(getViewport().getVerticalRangeModel(), y);

		int oldX = getViewport().getViewLocation().x;
		int oldY = getViewport().getViewLocation().y;
		int dx = x - oldX;
		int dy = y - oldY;

		if (dx == 0 && dy == 0)
			return; // Nothing to do.

		Dimension viewingArea = getViewport().getClientArea().getSize();

		int minFrames = 3;
		int maxFrames = 6;
		if (dx == 0 || dy == 0) {
			minFrames = 6;
			maxFrames = 13;
		}
		int frames = (Math.abs(dx) + Math.abs(dy)) / 15;
		frames = Math.max(frames, minFrames);
		frames = Math.min(frames, maxFrames);

		int stepX = Math.min((dx / frames), (viewingArea.width / 3));
		int stepY = Math.min((dy / frames), (viewingArea.height / 3));

		for (int i = 1; i < frames; i++) {
			scrollTo(oldX + i * stepX, oldY + i * stepY);
			getViewport().getUpdateManager().performUpdate();
		}
		scrollTo(x, y);
	}

	/**
	 * Scrolls the contents to the new x and y location. If this scroll
	 * operation only consists of a vertical or horizontal scroll, a call will
	 * be made to {@link #scrollToY(int)} or {@link #scrollToX(int)},
	 * respectively, to increase performance.
	 * 
	 * @param x
	 *            the x coordinate to scroll to
	 * @param y
	 *            the y coordinate to scroll to
	 */
	public void scrollTo(int x, int y) {
		x = verifyScrollBarOffset(getViewport().getHorizontalRangeModel(), x);
		y = verifyScrollBarOffset(getViewport().getVerticalRangeModel(), y);
		if (x == getViewport().getViewLocation().x)
			scrollToY(y);
		else if (y == getViewport().getViewLocation().y)
			scrollToX(x);
		else
			getViewport().setViewLocation(x, y);
	}

	/**
	 * Scrolls the contents horizontally so that they are offset by
	 * <code>hOffset</code>.
	 * 
	 * @param hOffset
	 *            the new horizontal offset
	 */
	public void scrollToX(int hOffset) {
		hOffset = verifyScrollBarOffset(
				getViewport().getHorizontalRangeModel(), hOffset);
		int hOffsetOld = getViewport().getViewLocation().x;
		if (hOffset == hOffsetOld)
			return;
		int dx = -hOffset + hOffsetOld;

		Rectangle clientArea = getViewport().getBounds().getCropped(
				getViewport().getInsets());
		Rectangle blit = clientArea.getResized(-Math.abs(dx), 0);
		Rectangle expose = clientArea.getCopy();
		Point dest = clientArea.getTopLeft();
		expose.width = Math.abs(dx);
		if (dx < 0) { // Moving left?
			blit.translate(-dx, 0); // Move blit area to the right
			expose.x = dest.x + blit.width;
		} else
			// Moving right
			dest.x += dx; // Move expose area to the right

		// fix for bug 41111
		Control[] children = getChildren();

		boolean[] manualMove = new boolean[children.length];
		for (int i = 0; i < children.length; i++) {
			org.eclipse.swt.graphics.Rectangle bounds = children[i].getBounds();
			manualMove[i] = blit.width <= 0 || bounds.x > blit.x + blit.width
					|| bounds.y > blit.y + blit.height
					|| bounds.x + bounds.width < blit.x
					|| bounds.y + bounds.height < blit.y;
		}
		scroll(dest.x, dest.y, blit.x, blit.y, blit.width, blit.height, true);
		for (int i = 0; i < children.length; i++) {
			if (children[i].isDisposed())
				continue;
			org.eclipse.swt.graphics.Rectangle bounds = children[i].getBounds();
			if (manualMove[i])
				children[i].setBounds(bounds.x + dx, bounds.y, bounds.width,
						bounds.height);
		}

		getViewport().setIgnoreScroll(true);
		getViewport().setHorizontalLocation(hOffset);
		getViewport().setIgnoreScroll(false);
		redraw(expose.x, expose.y, expose.width, expose.height, true);
	}

	/**
	 * Scrolls the contents vertically so that they are offset by
	 * <code>vOffset</code>.
	 * 
	 * @param vOffset
	 *            the new vertical offset
	 */
	public void scrollToY(int vOffset) {
		vOffset = verifyScrollBarOffset(getViewport().getVerticalRangeModel(),
				vOffset);
		int vOffsetOld = getViewport().getViewLocation().y;
		if (vOffset == vOffsetOld)
			return;
		int dy = -vOffset + vOffsetOld;

		Rectangle clientArea = getViewport().getBounds().getCropped(
				getViewport().getInsets());
		Rectangle blit = clientArea.getResized(0, -Math.abs(dy));
		Rectangle expose = clientArea.getCopy();
		Point dest = clientArea.getTopLeft();
		expose.height = Math.abs(dy);
		if (dy < 0) { // Moving up?
			blit.translate(0, -dy); // Move blit area down
			expose.y = dest.y + blit.height; // Move expose area down
		} else
			// Moving down
			dest.y += dy;

		// fix for bug 41111
		Control[] children = getChildren();
		boolean[] manualMove = new boolean[children.length];
		for (int i = 0; i < children.length; i++) {
			org.eclipse.swt.graphics.Rectangle bounds = children[i].getBounds();
			manualMove[i] = blit.height <= 0 || bounds.x > blit.x + blit.width
					|| bounds.y > blit.y + blit.height
					|| bounds.x + bounds.width < blit.x
					|| bounds.y + bounds.height < blit.y;
		}
		scroll(dest.x, dest.y, blit.x, blit.y, blit.width, blit.height, true);
		for (int i = 0; i < children.length; i++) {
			if (children[i].isDisposed())
				continue;
			org.eclipse.swt.graphics.Rectangle bounds = children[i].getBounds();
			if (manualMove[i])
				children[i].setBounds(bounds.x, bounds.y + dy, bounds.width,
						bounds.height);
		}

		getViewport().setIgnoreScroll(true);
		getViewport().setVerticalLocation(vOffset);
		getViewport().setIgnoreScroll(false);
		redraw(expose.x, expose.y, expose.width, expose.height, true);
	}

	/**
	 * Sets the given border on the LightweightSystem's root figure.
	 * 
	 * @param border
	 *            The new border
	 */
	public void setBorder(Border border) {
		getLightweightSystem().getRootFigure().setBorder(border);
	}

	/**
	 * Sets the contents of the {@link Viewport}.
	 * 
	 * @param figure
	 *            the new contents
	 */
	public void setContents(IFigure figure) {
		getViewport().setContents(figure);
	}

	/**
	 * @see org.eclipse.swt.widgets.Control#setFont(org.eclipse.swt.graphics.Font)
	 */
	public void setFont(Font font) {
		this.font = font;
		super.setFont(font);
	}

	/**
	 * Sets the horizontal scrollbar visibility. Possible values are
	 * {@link #AUTOMATIC}, {@link #ALWAYS}, and {@link #NEVER}.
	 * 
	 * @param v
	 *            the new visibility
	 */
	public void setHorizontalScrollBarVisibility(int v) {
		hBarVisibility = v;
	}

	/**
	 * Sets both the horizontal and vertical scrollbar visibility to the given
	 * value. Possible values are {@link #AUTOMATIC}, {@link #ALWAYS}, and
	 * {@link #NEVER}.
	 * 
	 * @param both
	 *            the new visibility
	 */
	public void setScrollBarVisibility(int both) {
		setHorizontalScrollBarVisibility(both);
		setVerticalScrollBarVisibility(both);
	}

	/**
	 * Sets the vertical scrollbar visibility. Possible values are
	 * {@link #AUTOMATIC}, {@link #ALWAYS}, and {@link #NEVER}.
	 * 
	 * @param v
	 *            the new visibility
	 */
	public void setVerticalScrollBarVisibility(int v) {
		vBarVisibility = v;
	}

	/**
	 * Sets the Viewport. The given Viewport must use "fake" scrolling. That is,
	 * it must be constructed using <code>new Viewport(true)</code>.
	 * 
	 * @param vp
	 *            the new viewport
	 */
	public void setViewport(Viewport vp) {
		if (viewport != null)
			unhookViewport();
		viewport = vp;
		lws.setContents(viewport);
		hookViewport();
	}

	private int verifyScrollBarOffset(RangeModel model, int value) {
		/**
		 * FIXME: The maximum - extent value is too small and cuts of 17px of
		 * the diagram if both scrollbars are visible.
		 * 
		 * @author Szenko
		 */
		// value = Math.max(model.getMinimum(), value);
		// return Math.min(model.getMaximum() - model.getExtent(), value);
		return value;
	}

	@Override
	public void onResize() {
		final Element realParent = (Element) ((LayoutPanel) canvas.getParent())
				.getWidgetContainerElement(canvas);

		int pw = realParent.getClientWidth();
		int ph = realParent.getClientHeight();

		// in case of trouble :)
		// canvas.setPixelSize(pw, ph);

		canvas.setCoordinateSpaceWidth(pw);
		canvas.setCoordinateSpaceHeight(ph);

		canvas.getElement().getStyle().setProperty("DIRTY", "dirty");

		IFigure rootFigure = getLightweightSystem().getRootFigure();
		rootFigure.revalidate();
		rootFigure.repaint();
	}

	@Override
	public void redraw() {
		super.redraw();
		getLightweightSystem().getRootFigure().repaint();
	}

}
