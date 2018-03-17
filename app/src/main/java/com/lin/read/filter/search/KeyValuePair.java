package com.lin.read.filter.search;

public class KeyValuePair {

	private String key;
	private Object value;
	
	public KeyValuePair(String key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	public KeyValuePair() {
		super();
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
