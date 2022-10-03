/*
 * Copyright 2022 Harshit Poddar
 */
package servlet;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;

import scraper.LatestNews;

/**
 * Servlet implementation class LatestStoryServlet
 */
@WebServlet("/getTimeStories")
public class LatestNewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			JSONArray jsonArray = LatestNews.getLatestNews();
			if(!jsonArray.isEmpty()) {
				HttpSession session = request.getSession();
				session.setAttribute("jsonResult", jsonFormatter(jsonArray.toString()));
				response.sendRedirect("result.jsp");
			}
		} catch (IOException e) {
			LatestNews.LOG.error("error in doGet method:: " + e.getMessage(), e);
		}
	}

	private String jsonFormatter(String jsonString) {
		return jsonString.replaceAll("\\[\\{\"", "\\[<br>&ensp;\\{<br>&emsp;&thinsp;\"").replaceAll("\"\\}\\]", "\"<br>&ensp;\\}<br>\\]")
				.replaceAll("\",\"", "\",<br>&emsp;&thinsp;\"").replaceAll("\\},\\{", "<br>&ensp;\\},<br>&ensp;\\{<br>&emsp;&thinsp;");
	}
}