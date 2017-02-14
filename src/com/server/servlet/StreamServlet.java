package com.server.servlet;

import com.server.config.Configurations;
import com.server.util.IoUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class StreamServlet extends HttpServlet {
	private static final long serialVersionUID = 2092517257L;
	static final int BUFFER_LENGTH = 10240;
	static final String START_FIELD = "start";
	public static final String CONTENT_RANGE_HEADER = "content-range";

	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doOptions(req, resp);
		//上传文件信息
		String token = req.getParameter("token");
		String size = req.getParameter("size");
		String fileNameString = req.getParameter("name");
		String fileName = new String(fileNameString.getBytes("ISO-8859-1"),"UTF-8");

		PrintWriter writer = resp.getWriter();

		JSONObject json = new JSONObject();
		long start = 0L;
		boolean success = true;
		String message = "";
		try {
			File f = IoUtil.getTokenedFile(token);
			start = f.length();

			if ((!(token.endsWith("_0"))) || (!("0".equals(size)))
					|| (0L != start))
				f.renameTo(IoUtil.getFile(fileName));
		} catch (FileNotFoundException fne) {
			message = "Error: " + fne.getMessage();
			success = false;
			try {
				if (success)
					json.put("start", start);
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException localJSONException1) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		} finally {
			try {
				if (success)
					json.put("start", start);
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException localJSONException2) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		}
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doOptions(req, resp);

		String token = req.getParameter("token");
		String fileNameString = req.getParameter("name");
		String fileName = new String(fileNameString.getBytes("ISO-8859-1"),
				"UTF-8");
		Range range = IoUtil.parseRange(req);

		OutputStream out = null;
		InputStream content = null;
		PrintWriter writer = resp.getWriter();

		JSONObject json = new JSONObject();
		long start = 0L;
		boolean success = true;
		String message = "000000000000000000";
		File dst;
		File f = IoUtil.getTokenedFile(token);
		try {
			if (f.length() != range.getFrom()) {
				throw new StreamException(
						StreamException.ERROR_FILE_RANGE_START);
			}

			out = new FileOutputStream(f, true);
			content = req.getInputStream();
			int read = 0;
			byte[] bytes = new byte[10240];
			while ((read = content.read(bytes)) != -1)
				out.write(bytes, 0, read);

			start = f.length();
		} catch (StreamException se) {
			success = StreamException.ERROR_FILE_RANGE_START == se.getCode();
			message = "Code: " + se.getCode();

			IoUtil.close(out);
			IoUtil.close(content);

			if (range.getSize() == start) {

				dst = IoUtil.getFile(fileName);
				dst.delete();
				f.renameTo(dst);
				System.out.println("TK: `" + token + "`, NE: `" + fileName
						+ "`");

				if (Configurations.isDeleteFinished())
					dst.delete();
			}
			try {
				if (success)
					json.put("start", start);
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException d) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		} catch (FileNotFoundException fne) {
			message = "Code: " + StreamException.ERROR_FILE_NOT_EXIST;
			success = false;

			IoUtil.close(out);
			IoUtil.close(content);

			if (range.getSize() == start) {
				dst = IoUtil.getFile(fileName);
				dst.delete();
				f.renameTo(dst);
				System.out.println("TK: `" + token + "`, NE: `" + fileName
						+ "`");

				if (Configurations.isDeleteFinished())
					dst.delete();
			}
			try {
				if (success)
					json.put("start", start);
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException d) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		} catch (IOException io) {
			message = "IO Error: " + io.getMessage();
			success = false;

			IoUtil.close(out);
			IoUtil.close(content);

			if (range.getSize() == start) {
				dst = IoUtil.getFile(fileName);
				dst.delete();
				f.renameTo(dst);
				System.out.println("TK: `" + token + "`, NE: `" + fileName
						+ "`");

				if (Configurations.isDeleteFinished())
					dst.delete();
			}
			try {
				if (success)
					json.put("start", start);
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException d) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		} finally {
			IoUtil.close(out);
			IoUtil.close(content);

			if (range.getSize() == start) {
				dst = IoUtil.getFile(fileName);
				dst.delete();
				f.renameTo(dst);
				System.out.println("TK: `" + token + "`, NE: `" + fileName
						+ "`");

				if (Configurations.isDeleteFinished())
					dst.delete();
			}
			try {
				if (success)
					json.put("start", start);
				json.put("success", success);
				json.put("message", message);
			} catch (JSONException d) {
			}
			writer.write(json.toString());
			IoUtil.close(writer);
		}
	}

	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//响应头信息
		resp.setContentType("application/json;charset=UTF-8;pageEncoding=UTF-8");
		resp.setHeader("Access-Control-Allow-Headers",
				"Content-Range,Content-Type");
		resp.setHeader("Access-Control-Allow-Origin",
				Configurations.getCrossOrigins());
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
	}

	public void destroy() {
		super.destroy();
	}
}