package com.server.servlet;

import com.server.util.IoUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.json.JSONException;
import org.json.JSONObject;

public class FormDataServlet extends HttpServlet {
	private static final long serialVersionUID = -1827295024L;
	static final String FILE_FIELD = "file";
	public static final int BUFFER_LENGTH = 10485760;
	static final int MAX_FILE_SIZE = 104857600;

	public void init() throws ServletException {
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
		doOptions(req, resp);  //做选项
		PrintWriter writer = resp.getWriter();
		boolean isMultipart = ServletFileUpload.isMultipartContent(req);  //ServletFileUpload是多部分内容
		if (!(isMultipart)) {  //如果不是是多部分-->进行判断
			writer.println("ERROR: It's not Multipart form.");
			return;
		}
		JSONObject json = new JSONObject();
		long start = 0L;
		boolean success = true;
		String message = "";

		ServletFileUpload upload = new ServletFileUpload();
		InputStream in = null;
		String token = null;
		try {
			FileItemIterator iter = upload.getItemIterator(req);  //从upload获取（req）传来的迭代
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
				String name = item.getFieldName();
				in = item.openStream();
				if (item.isFormField()) {  //判断是否是表单字段
					String value = Streams.asString(in);
					if ("token".equals(name)) {
						token = value;
					}
					System.out.println(name + ":" + value);
				} else {
					String fileName = item.getName();
					start = IoUtil.streaming(in, token, fileName);
				}
			}
		} catch (FileUploadException fne) {
			success = false;
			message = "Error: " + fne.getLocalizedMessage();
			try {
				if (success)
					json.put("start", start);
					json.put("success", success);
					json.put("message", message);
			} catch (JSONException localJSONException1) {
			}
			writer.write(json.toString());
			IoUtil.close(in);
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
			IoUtil.close(in);
			IoUtil.close(writer);
		}
	}

	public void destroy() {
		super.destroy();
	}
}