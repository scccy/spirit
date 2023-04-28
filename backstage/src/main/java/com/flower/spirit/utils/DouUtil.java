package com.flower.spirit.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpException;
import org.htmlunit.WebClient;
import org.htmlunit.html.HtmlPage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class DouUtil {
	
	private static Logger logger = LoggerFactory.getLogger(DouUtil.class);
	
	//  备用
	
//	private static String  token_generate ="https://tiktok.iculture.cc/X-Bogus";   //网上找的
	
	
	private static String ttwid ="https://ttwid.bytedance.com/ttwid/union/register/";  //ttwid申请  暂时不用 需要配合 https://tiktok.iculture.cc/X-Bogus
	
	public static String icultureapi = "https://api.iculture.cc/api/douyin/?url=";  //备用   先不用  开源用别人的公开API不太好
	
	private static JSONObject ttwidData =  JSONObject.parseObject("{\"region\":\"cn\",\"aid\":1768,\"needFid\":false,\"service\":\"www.ixigua.com\",\"migrate_info\":{\"ticket\":\"\",\"source\":\"node\"},\"cbUrlProtocol\":\"https\",\"union\":true}");  //需要配合ttwid 使用
	
	private static String  token_generate ="https://spirit.lifeer.xyz/spirit-token";   //自建 
	
	
	//private static String  token_generate ="http://127.0.0.1/spirit-token";   //自建 
	
//	private static String ua="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36";  //有毒 不知道哪里有问题  这个ua 有问题 先记录一下
	
	public static String ua="Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36";

	
	public static  Map<String, String>  downVideo(String url) {
		Document document = null;
		Map<String, String>  data = new HashMap<String, String>();
		try {
			 document = Jsoup.connect(url).userAgent(ua).get();
			 String baseUri = document.baseUri();
			 String pattern="(?<=/video/).*?(?=/)";
		     Pattern compile = Pattern.compile(pattern);
		     Matcher matcher = compile.matcher(baseUri);
		     if(matcher.find()) {
		    	  String vedioId = matcher.group(0);
		    	  data  = DouUtil.getBogus(vedioId);
		    	  if(data != null) {
		    		  return data;
		    	  }else {
		    		  return DouUtil.htmlclient(url);
		    	  }
		     }else {
		    	 return DouUtil.htmlclient(url);
		     }
			
		} catch (IOException e1) {
			return DouUtil.htmlclient(url);
		}
	}
	public static  Map<String, String> htmlclient(String url) {
		 logger.info("WebClient客户端开始启动");
		 Map<String, String> res = new HashMap<String, String>();
		 WebClient webClient = ThreadConfig.getWebClient();
	        HtmlPage page = null;
	        try {
	            page = webClient.getPage(url);
		        webClient.waitForBackgroundJavaScript(300);
		        String pageXml = page.asXml();
		        Document parse = Jsoup.parse(pageXml);
		        Element render_data = parse.getElementById("RENDER_DATA");
		        String encode = URLDecoder.decode(render_data.html().substring("//<![CDATA[".length(), render_data.html().length() - "//]]>".length()).trim(), "UTF-8");
		        JSONObject jsonObject = JSON.parseObject(encode);
		        jsonObject.forEach((key, value) -> {
		        	if(DouUtil.isJSONString(value.toString())) {
		        		  JSONObject aweme = JSONObject.parseObject(value.toString()).getJSONObject("aweme");
		        		  if(aweme != null) {
		        			  JSONObject detail = aweme.getJSONObject("detail");
		        			  String awemeId = detail.getString("awemeId");
		        			  String desc = detail.getString("desc");
		        			  JSONObject videoobj = detail.getJSONObject("video");
		        	          String playApi = videoobj.getString("playApi");
		        	          String cover = videoobj.getString("cover");
		        	          res.put("cover", cover);
		        	     	  res.put("awemeid", awemeId);
		        			  res.put("videoplay", playApi);
		        			  res.put("desc", desc);
		        			  res.put("type", "client");
		        		  }
		        	}
					
				});
	        } catch (Exception e) {
	        	logger.info("获取不到");
	        }finally {
				//如果后续观察内存占用问题比较大 考虑取消此处注释
				webClient.getCurrentWindow().getJobManager().removeAllJobs();
				webClient.getCurrentWindow().getJobManager().shutdown();
				webClient.close();
				System.gc();
	        }
	        logger.info("下载流程结束");
	        return res;
	
	}
	
	public static  Map<String, String> getBogus(String aweme_id) throws HttpException, IOException {
		Map<String, String> res = new HashMap<String, String>();
		JSONObject token = HttpUtil.doPostNew(token_generate, DouUtil.generateparameters(aweme_id));
		String code = token.getString("code");
		if(code.equals("200")) {
			 String url = token.getJSONObject("data").getString("url");
			 String cookie = token.getJSONObject("data").getString("cookie"); 
			 String httpget = DouUtil.httpget(url.trim(), cookie.trim());
			 JSONObject data = JSONObject.parseObject(httpget);
			 JSONObject aweme_detail = data.getJSONObject("aweme_detail");
			 String coveruri = "";
			 JSONArray cover = aweme_detail.getJSONObject("video").getJSONObject("cover").getJSONArray("url_list");
			 if(cover.size() >=2) {
				 coveruri = cover.getString(2);
			 }else {
				 coveruri = cover.getString(0);
			 }
			 JSONArray jsonArray = aweme_detail.getJSONObject("video").getJSONObject("play_addr").getJSONArray("url_list");
			 String videoplay = "";
			 if(jsonArray.size() >=2) {
				 videoplay = jsonArray.getString(2);
			 }else {
				 videoplay = jsonArray.getString(0);
			 }
			 String desc = aweme_detail.getString("desc");
			 res.put("awemeid", aweme_id);
			 res.put("videoplay", videoplay);
			 res.put("desc", desc);
			 res.put("cover", coveruri);
			 res.put("cookie", cookie.trim());
			 res.put("type", "api");
			 return res;
		}
		return null;

		
	}
	public static String httpget(String addr,String ck) throws IOException {
		String cookie = ck;
        String urlString = addr;    
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("user-agent", ua);
        conn.setRequestProperty("cookie", cookie);
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
	}
	
	

	
	public static JSONObject generateparameters(String aweme_id) {
		JSONObject data =  new JSONObject();
		data.put("awemeid", aweme_id);
		data.put("ua", ua);
		return data;
	}
	
	public static void main(String[] args) throws HttpException, IOException {
		downVideo("https://v.douyin.com/A3bMHaT/");
		
	}
	
	/**
	 * 判断是否为json
	 * @param str
	 * @return
	 */
	@SuppressWarnings("unused")
	public static boolean isJSONString(String str) {
	    boolean result = false;
	    try {
	    	JSONObject obj=JSONObject.parseObject(str);
	        result = true;
	    } catch (Exception e) {
	        result=false;
	    }
	    return result;
	}
}
