package org.eclipse.swt.graphics;

public class TextLayout extends Resource implements ITextLayout {

	private String text;
	private int width;

	public TextLayout(Device device) {

	}

	@Override
	public void draw(GC gc, int x, int y) {
	}

	@Override
	public void draw(GC gc, int x, int y, int selectionStart, int selectionEnd,
			Color selectionForeground, Color selectionBackground) {
		draw(gc, x, y, selectionStart, selectionEnd, selectionForeground,
				selectionBackground, 0);
	}

	@Override
	public void draw(GC gc, int x, int y, int selectionStart, int selectionEnd,
			Color selectionForeground, Color selectionBackground, int flags) {
		gc.drawText(text, x, y);
	}

	@Override
	public int getAlignment() {
		return 0;
	}

	@Override
	public int getAscent() {
		return 0;
	}

	@Override
	public Rectangle getBounds() {
		return null;
	}

	@Override
	public Rectangle getBounds(int start, int end) {
		return null;
	}

	@Override
	public int getDescent() {
		return 0;
	}

	@Override
	public Font getFont() {
		return null;
	}

	@Override
	public int getIndent() {
		return 0;
	}

	@Override
	public boolean getJustify() {
		return false;
	}

	@Override
	public int getLevel(int offset) {
		return 0;
	}

	@Override
	public Rectangle getLineBounds(int lineIndex) {
		return null;
	}

	@Override
	public int getLineCount() {
		return 0;
	}

	@Override
	public int getLineIndex(int offset) {
		return 0;
	}

	@Override
	public FontMetrics getLineMetrics(int lineIndex) {
		return null;
	}

	@Override
	public int[] getLineOffsets() {
		return null;
	}

	@Override
	public Point getLocation(int offset, boolean trailing) {
		return null;
	}

	@Override
	public int getNextOffset(int offset, int movement) {
		return 0;
	}

	@Override
	public int getOffset(Point point, int[] trailing) {
		return 0;
	}

	@Override
	public int getOffset(int x, int y, int[] trailing) {
		return 0;
	}

	@Override
	public int getOrientation() {
		return 0;
	}

	@Override
	public int getPreviousOffset(int index, int movement) {
		return 0;
	}

	@Override
	public int[] getRanges() {
		return null;
	}

	@Override
	public int[] getSegments() {
		return null;
	}

	@Override
	public char[] getSegmentsChars() {
		return null;
	}

	@Override
	public int getSpacing() {
		return 0;
	}

	@Override
	public TextStyle getStyle(int offset) {
		return null;
	}

	@Override
	public TextStyle[] getStyles() {
		return null;
	}

	@Override
	public int[] getTabs() {
		return null;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getWrapIndent() {
		return 0;
	}

	@Override
	public boolean isDisposed() {
		return false;
	}

	@Override
	public void setAlignment(int alignment) {

	}

	@Override
	public void setAscent(int ascent) {

	}

	@Override
	public void setDescent(int descent) {

	}

	@Override
	public void setFont(Font font) {

	}

	@Override
	public void setIndent(int indent) {

	}

	@Override
	public void setJustify(boolean justify) {

	}

	@Override
	public void setOrientation(int orientation) {

	}

	@Override
	public void setSpacing(int spacing) {

	}

	@Override
	public void setSegments(int[] segments) {

	}

	@Override
	public void setSegmentsChars(char[] segmentsChars) {

	}

	@Override
	public void setStyle(TextStyle style, int start, int end) {

	}

	@Override
	public void setTabs(int[] tabs) {

	}

	@Override
	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
	}

	@Override
	public void setWrapIndent(int wrapIndent) {

	}

}