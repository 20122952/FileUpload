package com.server.servlet;

public class StreamException extends Exception {
	private static final long serialVersionUID = 1277712815L;
	public static int ERROR_FILE_RANGE_START = 409;
	public static int ERROR_FILE_NOT_EXIST = 401;
	private int code;

	public StreamException(int code) {
		this.code = code;
	}

	public int getCode() {
		return this.code;
	}
}