package com.flower.spirit.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BiliUtil {
	
	/**
	 * 
	 */
	public static void findVideoStreaming(String url,String token) {
		
	}
	
	
	public static Map<String, String> getVideoDataInfo(String url) {
		Map<String, String> res = new HashMap<String, String>();
		String parseEntry = BiliUtil.parseEntry(url);
		String api ="";
		if(parseEntry.contains("BV")) {
			api ="https://api.bilibili.com/x/web-interface/view?bvid="+parseEntry.substring(2, parseEntry.length());
		}
		if(parseEntry.contains("av")) {
			api="https://api.bilibili.com/x/web-interface/view?aid="+parseEntry.substring(2, parseEntry.length());
		}
		System.out.println(api);
		String serchPersion = HttpUtil.getSerchPersion(api, "UTF-8");
		JSONObject videoData = JSONObject.parseObject(serchPersion);
		String bvid = videoData.getString("bvid");
		String aid = videoData.getString("aid");
		String cid = videoData.getString("cid");
		res.put("bvid", bvid);
		res.put("aid", aid);
		res.put("cid", cid);
		return res;
	}
	
	
	public static String parseEntry(String url) {
		if(url.contains("/video/av") || url.contains("/video/BV") ) {
			return BiliUtil.findUrlAidOrBid(url);
		}else {
			 WebClient webClient = ThreadConfig.getWebClientNotJavaScript();
			 HtmlPage page = null;
			 try {
				  page = webClient.getPage(url);
				  webClient.waitForBackgroundJavaScript(300);
				  URL biliUrl = page.getUrl();
				  String realUrl = biliUrl.toString();
				  return BiliUtil.findUrlAidOrBid(realUrl);
			}catch (Exception e) {
				
			}finally {
				webClient.close();
			}
		}
		return "";
	}
	
	
	public static String findUrlAidOrBid(String url) {
		String replace = url.replaceAll("http://", "").replaceAll("https://", "").replace("www.bilibili.com/video/", "");
		int indexOf = replace.indexOf("/");
		String id = replace.substring(0, indexOf);
		return id;
	}

	
	
	
	
	
	
	
	
	
	public static void main(String[] args) {
		Map<String, String> videoDataInfo = BiliUtil.getVideoDataInfo("https://b23.tv/kq7ciww");
//		System.out.println("BV1NP411S7aY".substring(2,"BV1NP411S7aY".length()));
	}
}
