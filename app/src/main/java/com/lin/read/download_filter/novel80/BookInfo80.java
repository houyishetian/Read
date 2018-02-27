package com.lin.read.download_filter.novel80;

public class BookInfo80 {

	private String bookName;
	private String author;
	private String latestUpdate;
	private String latestContent;
	private String bookLink;
	private String bookType;
	public String getBookType() {
		return bookType;
	}
	public void setBookType(String bookType) {
		this.bookType = bookType;
	}
	public String getBookLink() {
		return bookLink;
	}
	public void setBookLink(String bookLink) {
		this.bookLink = bookLink;
	}
	public String getBookName() {
		return bookName;
	}
	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getLatestUpdate() {
		return latestUpdate;
	}
	public void setLatestUpdate(String latestUpdate) {
		this.latestUpdate = latestUpdate;
	}
	public String getLatestContent() {
		return latestContent;
	}
	public void setLatestContent(String latestContent) {
		this.latestContent = latestContent;
	}
	@Override
	public String toString() {
		return "BookInfo80 [bookName=" + bookName + ", author=" + author
				+ ", latestUpdate=" + latestUpdate + ", latestContent="
				+ latestContent + ", bookLink=" + bookLink + "]";
	}
	
	
}
