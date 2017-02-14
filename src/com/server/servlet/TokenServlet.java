package com.server.servlet;

import com.server.config.Configurations;
import com.server.util.TokenUtil;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

public class TokenServlet extends HttpServlet {
	private static final long serialVersionUID = -697466551L;
	static final String FILE_NAME_FIELD = "name";
	static final String FILE_SIZE_FIELD = "size";
	static final String TOKEN_FIELD = "token";
	static final String SERVER_FIELD = "server";
	static final String SUCCESS = "success";
	static final String MESSAGE = "message";

	public void init() throws ServletException {
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//响应头
		resp.setContentType("text/html;charset=UTF-8;pageEncoding=UTF-8");
		//传来文件的参数
		String nameString = req.getParameter("name");
		String name = new String(nameString.getBytes("ISO-8859-1"), "UTF-8");
		String size = req.getParameter("size");
		String token = TokenUtil.generateToken(name, size);

		PrintWriter writer = resp.getWriter();  //获取response的输出流，这个流是字符流用来向jsp界面输出字符串的

		JSONObject json = new JSONObject();
		try {
			json.put("token", token);
			if (Configurations.isCrossed())
				json.put("server", Configurations.getCrossServer());
			json.put("success", true);
			json.put("message", "");
		} catch (JSONException localJSONException) {
		}
		writer.write(json.toString());
	}

	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doHead(req, resp);
	}

	public void destroy() {
		super.destroy();
	}
}