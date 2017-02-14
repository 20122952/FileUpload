package com.server.util;

import com.server.config.Configurations;
import com.server.servlet.Range;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;

public class IoUtil {
	static final Pattern RANGE_PATTERN = Pattern
			.compile("bytes \\d+-\\d+/\\d+");

	public static File getFile(String filename) throws IOException {
		if ((filename == null) || (filename.isEmpty()))
			return null;
		String name = filename.replaceAll("/", Matcher
				.quoteReplacement(File.separator));
		File f = new File(Configurations.getFileRepository() + File.separator
				+ name);
		if (!(f.getParentFile().exists()))
			f.getParentFile().mkdirs();
		if (!(f.exists()))
			f.createNewFile();

		return f;
	}

	public static File getTokenedFile(String key) throws IOException {
		if ((key == null) || (key.isEmpty()))
			return null;

		File f = new File(Configurations.getFileRepository() + File.separator
				+ key);
		if (!(f.getParentFile().exists()))
			f.getParentFile().mkdirs();
		if (!(f.exists()))
			f.createNewFile();

		return f;
	}

	public static void storeToken(String key) throws IOException {
		if ((key == null) || (key.isEmpty()))
			return;

		File f = new File(Configurations.getFileRepository() + File.separator
				+ key);
		if (!(f.getParentFile().exists()))
			f.getParentFile().mkdirs();
		if (!(f.exists()))
			f.createNewFile();
	}

	public static void close(Closeable stream) {
		try {
			if (stream == null)
				return;
			stream.close();
		} catch (IOException localIOException) {
		}
	}

	public static Range parseRange(HttpServletRequest req) throws IOException {
		String range = req.getHeader("content-range");
		Matcher m = RANGE_PATTERN.matcher(range);
		if (m.find()) {
			range = m.group().replace("bytes ", "");
			String[] rangeSize = range.split("/");
			String[] fromTo = rangeSize[0].split("-");

			long from = Long.parseLong(fromTo[0]);
			long to = Long.parseLong(fromTo[1]);
			long size = Long.parseLong(rangeSize[1]);

			return new Range(from, to, size);
		}
		throw new IOException("Illegal Access!");
	}

	public static long streaming(InputStream in, String key, String fileName)
			throws IOException {
		OutputStream out = null;
		File f = getTokenedFile(key);
		try {
			out = new FileOutputStream(f);

			int read = 0;
			byte[] bytes = new byte[10485760];
			while ((read = in.read(bytes)) != -1)
				out.write(bytes, 0, read);

			out.flush();
		} finally {
			close(out);
		}

		File dst = getFile(fileName);
		dst.delete();
		f.renameTo(dst);

		long length = getFile(fileName).length();

		if (Configurations.isDeleteFinished()) {
			dst.delete();
		}

		return length;
	}
}