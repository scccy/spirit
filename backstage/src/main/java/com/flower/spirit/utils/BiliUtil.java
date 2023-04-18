package com.flower.spirit.utils;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BiliUtil {
	
	/**
	 * @throws Exception 
	 * 
	 */
	public static  Map<String, String> findVideoStreaming(String url,String token,String filepath) throws Exception {
		String api ="https://api.bilibili.com/x/player/playurl";
		Map<String, String> videoDataInfo = BiliUtil.getVideoDataInfo(url);
		api=api+"?avid="+videoDataInfo.get("aid")+"&cid="+videoDataInfo.get("cid");
		if(null != token && !token.equals("")) {
			api =api+"&qn=80";
		}else {
			api =api+"&qn=64";
		}
		api =api+"&fnval=1&fnver=0&fourk=0";
		String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", token);
		JSONObject parseObject = JSONObject.parseObject(httpGetBili);
		String video = parseObject.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
		HttpUtil.downBiliFromUrl(video, videoDataInfo.get("cid")+".mp4", filepath);
		videoDataInfo.put("video", filepath+"/"+videoDataInfo.get("cid")+".mp4");
		videoDataInfo.put("videoname", videoDataInfo.get("cid")+".mp4");
		return videoDataInfo;
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
		String serchPersion = HttpUtil.getSerchPersion(api, "UTF-8");
		JSONObject videoData = JSONObject.parseObject(serchPersion);
		System.out.println(serchPersion);
		String bvid = videoData.getJSONObject("data").getString("bvid");
		String aid = videoData.getJSONObject("data").getString("aid");
		String cid = videoData.getJSONObject("data").getString("cid");
		String title = videoData.getJSONObject("data").getString("title");
		String pic = videoData.getJSONObject("data").getString("pic");
		String desc = videoData.getJSONObject("data").getString("desc");
		res.put("title", title);
		res.put("bvid", bvid);
		res.put("aid", aid);
		res.put("cid", cid);
		res.put("pic", pic);
		res.put("desc", desc);
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

	
	
	
	
	
	
	
	
	
	public static void main(String[] args) throws Exception {
//		String findVideoStreaming = BiliUtil.findVideoStreaming("https://b23.tv/kq7ciww",null,"D:\\flower\\uploadFile");
//		System.out.println(findVideoStreaming);
//		System.out.println("BV1NP411S7aY".substring(2,"BV1NP411S7aY".length()));
	}
}
