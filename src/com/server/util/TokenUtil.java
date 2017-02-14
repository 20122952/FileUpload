package com.server.util;

import java.io.IOException;

public class TokenUtil {
	public static String generateToken(String name, String size)
			throws IOException {
		if ((name == null) || (size == null))
			return "";
		int code = name.hashCode();
		try {
			String token = ((code > 0) ? "A" : "B") + Math.abs(code) + "_"
					+ size.trim();

			IoUtil.storeToken(token);

			return token;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}