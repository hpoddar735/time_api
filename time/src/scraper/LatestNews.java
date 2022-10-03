/*
 * Copyright 2022 Harshit Poddar
 */
package scraper;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class LatestNews {
	public static final Log LOG = LogFactory.getLog(LatestNews.class);

	private LatestNews() {
		throw new IllegalStateException("Utility class");
	}

	public static JSONArray getLatestNews() {
		JSONArray jsonArray = new JSONArray();
		JSONObject item = null;
		String browserResponse = navigateTo("times", "https://time.com/");
		String latestStoriesRegex = "<li\\s+class=\"latest-stories__item\"[^<a]+<a\\s+href=\"([^\"]+)\"[^<]+<[^>]+>([^<]+)";
		Matcher latestStoriesMatcher = getMatches(browserResponse, latestStoriesRegex);
		while(latestStoriesMatcher.find()) {
			item = new JSONObject();
			item.put("title", latestStoriesMatcher.group(2));
			item.put("link", "https://time.com" + latestStoriesMatcher.group(1));
			jsonArray.put(item);
		}
		return jsonArray;
	}

	private static String navigateTo(String name, String url){
		StringBuilder httpResponseString = new StringBuilder();
		LOG.debug("navigating to: " + name + " with url: " + url);
		try(CloseableHttpClient httpclient = HttpClients.createDefault();) {
			Scanner sc = null;
			HttpResponse httpresponse = null;
			if(!url.isEmpty()) {
				HttpGet httpget = new HttpGet(url);
				httpresponse = httpclient.execute(httpget);
				if(!httpresponse.getEntity().getContent().toString().isEmpty()) {
					sc = new Scanner(httpresponse.getEntity().getContent());
					while(sc.hasNext()) {
						httpResponseString.append(sc.nextLine());
					}
					sc.close();
				}
			}
		}catch(Exception e) {
			LOG.error("Failed to navigate " + name  + " due to: " + e.getMessage(), e);
		}
		return httpResponseString.toString();
	}

	private static Matcher getMatches(String documentRes, String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
		return pattern.matcher(documentRes);
	}
}