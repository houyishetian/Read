package com.lin.read.filter.search.novel80;

public class BookIndex80 {

	public static final int TYPE_INDEX = 1;
	public static final int AUTHOR_INDEX = 2;
	public static final int UPDATE_TIME_INDEX = 3;
	public static final int UPDATE_CONTENT_INDEX = 4;
	private int index;

	private int nextLine;
	private final int TYPE_NEXT = 1;
	private final int AUTHOR_NEXT = 2;
	private final int UPDATE_TIME_NEXT = 1;
	private final int UPDATE_CONTENT_NEXT = 1;

	public int getNextLine() {
		return nextLine;
	}

	private void setNextLine(int nextLine) {
		this.nextLine = nextLine;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		switch (index) {
		case TYPE_INDEX:
			setNextLine(TYPE_NEXT);
			break;
		case AUTHOR_INDEX:
			setNextLine(AUTHOR_NEXT);
			break;
		case UPDATE_TIME_INDEX:
			setNextLine(UPDATE_TIME_NEXT);
			break;
		case UPDATE_CONTENT_INDEX:
			setNextLine(UPDATE_CONTENT_NEXT);
			break;
		default:
			break;
		}
	}
}
