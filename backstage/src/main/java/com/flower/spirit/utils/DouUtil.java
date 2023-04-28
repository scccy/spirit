package com.flower.spirit.utils;


import java.io.IOException;

import org.apache.commons.httpclient.HttpException;

import com.alibaba.fastjson.JSONObject;

public class DouUtil {
	
	//  备用
	
//	private static String  token_generate ="https://tiktok.iculture.cc/X-Bogus";   //网上找的
	
	
	private static String ttwid ="https://ttwid.bytedance.com/ttwid/union/register/";  //ttwid申请  暂时不用 需要配合 https://tiktok.iculture.cc/X-Bogus
	
	public static String icultureapi = "https://api.iculture.cc/api/douyin/?url=";  //备用   先不用  开源用别人的公开API不太好
	
	private static JSONObject ttwidData =  JSONObject.parseObject("{\"region\":\"cn\",\"aid\":1768,\"needFid\":false,\"service\":\"www.ixigua.com\",\"migrate_info\":{\"ticket\":\"\",\"source\":\"node\"},\"cbUrlProtocol\":\"https\",\"union\":true}");  //需要配合ttwid 使用
	
//	private static String  token_generate ="https://spirit.lifeer.xyz/spirit-token";   //自建 
	
	
	private static String  token_generate ="http://127.0.0.1/spirit-token";   //自建 
	
	private static String ua="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";

	
	
	public static  void getBogus(String aweme_id) throws HttpException, IOException {
		JSONObject token = HttpUtil.doPostNew(token_generate, DouUtil.generateparameters(aweme_id));
		String code = token.getString("code");
		if(code.equals("200")) {
			 String response = "";
			 String url = token.getJSONObject("data").getString("url");
			 String urladdr = token.getJSONObject("data").getString("commonurl");
			 String cookie = token.getJSONObject("data").getString("cookie");
			 System.out.println(url);
			 System.out.println(cookie);	 
			 DouUtil.httpget(url, cookie);
	
		}
		
		
	}
	public static void httpget(String addr,String ck) throws IOException {
		 
	}
	
	

	
	public static JSONObject generateparameters(String aweme_id) {
		JSONObject data =  new JSONObject();
		data.put("awemeid", aweme_id);
		data.put("ua", ua);
		return data;
	}
	
	public static void main(String[] args) throws HttpException, IOException {
		DouUtil.getBogus("7221047525594139944");
//		JSONObject doPost = HttpUtil.doPost(ttwid, ttwidData);
		
	}
}
