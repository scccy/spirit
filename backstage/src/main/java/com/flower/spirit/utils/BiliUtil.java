package com.flower.spirit.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
public class BiliUtil {
	
	
	/**
	 * @throws Exception 
	 * 
	 */
	public static  Map<String, String> findVideoStreamingNoData(Map<String, String> videoDataInfo,String url,String token,String filepath) throws Exception {
		String api ="https://api.bilibili.com/x/player/playurl";
		api=api+"?avid="+videoDataInfo.get("aid")+"&cid="+videoDataInfo.get("cid");
		//公共部分结束
		//按照配置处理码流设置  优先使用用户配置
		if(null != token && !token.equals("")) {
			if(!Global.bilibitstream.equals("64")) {
				//此处我相信了用户 在选择 选择大会员码率的时候 自己是大会员
				api =api+"&qn="+Global.bilibitstream;
			}else {
				api =api+"&qn=80";
			}
		}else {
			api =api+"&qn=64";
		}
		api =api+"&fnval=1&fnver=0";
		//此处我相信了用户 在选择 选择大会员码率的时候 自己是大会员
		if(Global.bilibitstream.equals("120") && Global.bilimember) {
			api =api+"&fourk=1";
		}else {
			api =api+"&fourk=0";
		}
		
		String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", token);
		JSONObject parseObject = JSONObject.parseObject(httpGetBili);
//		System.out.println(parseObject);
		String video = parseObject.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
		String filename = StringUtil.getFileName(videoDataInfo.get("title"), videoDataInfo.get("cid"));
		if(Global.downtype.equals("http")) {
			HttpUtil.downBiliFromUrl(video, filename+".mp4", filepath);
		}
		if(Global.downtype.equals("a2")) {
			Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video, Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), filename+".mp4", Global.a2_token));
		}
		videoDataInfo.put("video", filepath+"/"+filename+".mp4");
		videoDataInfo.put("videoname", filename+".mp4");
		return videoDataInfo;
	}
	
	/**
	 * @throws Exception 
	 * 
	 */
	public static List<Map<String, String>> findVideoStreaming(String url,String token,String filepath) throws Exception {
		String api ="https://api.bilibili.com/x/player/playurl";
		List<Map<String, String>> videoDataInfo = BiliUtil.getVideoDataInfo(url);
		List<Map<String, String>> res = new ArrayList<Map<String,String>>();
		for(int i =0;i<videoDataInfo.size();i++) {
			Map<String, String> map = videoDataInfo.get(i);
			api=api+"?avid="+map.get("aid")+"&cid="+map.get("cid");
			//公共部分结束
			//按照配置处理码流设置  优先使用用户配置
			if(null != token && !token.equals("")) {
				if(!Global.bilibitstream.equals("64")) {
					//此处我相信了用户 在选择 选择大会员码率的时候 自己是大会员
					api =api+"&qn="+Global.bilibitstream;
				}else {
					api =api+"&qn=80";
				}
			}else {
				api =api+"&qn=64";
			}
			api =api+"&fnval=1&fnver=0";
			//此处我相信了用户 在选择 选择大会员码率的时候 自己是大会员
			if(Global.bilibitstream.equals("120") && Global.bilimember) {
				api =api+"&fourk=1";
			}else {
				api =api+"&fourk=0";
			}
			
			String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", token);
			JSONObject parseObject = JSONObject.parseObject(httpGetBili);
			System.out.println(parseObject);
			String video = parseObject.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
			String filename = StringUtil.getFileName(map.get("title"), map.get("cid"));
			if(Global.downtype.equals("http")) {
				HttpUtil.downBiliFromUrl(video, filename+".mp4", filepath);
			}
			if(Global.downtype.equals("a2")) {
				Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video, Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), filename+".mp4", Global.a2_token));
			}
			map.put("video", filepath+"/"+filename+".mp4");
			map.put("videoname", filename+".mp4");
			res.add(map);
		}
		
		
		
		return res;
	}
	
	
	public static List<Map<String, String>> getVideoDataInfo(String url) {
		List<Map<String, String>> res = new ArrayList<Map<String,String>>();
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
		if(videoData.getString("code").equals("0")) {		
			//优化多集问题 从page 里取
			
			String bvid = videoData.getJSONObject("data").getString("bvid");
			String aid = videoData.getJSONObject("data").getString("aid");
//			String cid = videoData.getJSONObject("data").getString("cid");
//			String title = videoData.getJSONObject("data").getString("title");
//			String pic = videoData.getJSONObject("data").getString("pic");
			String desc = videoData.getJSONObject("data").getString("desc");
			JSONArray jsonArray = videoData.getJSONObject("data").getJSONArray("pages");
	
			for(int i = 0;i<jsonArray.size();i++) {
				Map<String, String> data = new HashMap<String, String>(); 
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String cid = jsonObject.getString("cid");
				String title = jsonObject.getString("part");
				String pic = jsonObject.getString("first_frame");
				data.put("aid", aid);
				data.put("bvid", bvid);
				data.put("desc", desc);
				if(null == pic) {
					pic = videoData.getJSONObject("data").getString("pic");
				}
				data.put("cid", cid);
				data.put("title", title);
				data.put("pic", pic);
				res.add(data);
			}
			return res;
		}else {
			return null;
		}
	}
	
	
	public static String parseEntry(String url) {
		if(url.contains("/video/av") || url.contains("/video/BV") ) {
			return BiliUtil.findUrlAidOrBid(url);
		}else {
			Document document = null;
			try {
				document = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36").get();
				 String baseUri = document.baseUri();
				 return BiliUtil.findUrlAidOrBid(baseUri);
			} catch (IOException e1) {
				
			}
		}
		return "";
	}
	
	
	public static String findUrlAidOrBid(String url) {
		String replace ="";
		if(url.contains("http")) {
			replace = url.replaceAll("http://", "").replaceAll("https://", "").replace("www.bilibili.com/video/", "");
			int indexOf = replace.indexOf("/");
			String id = replace.substring(0, indexOf);
			return id;
		}else {
			replace = url.replaceAll("/video/", "");
			return replace;
		}
	}

	public static void main(String[] args) throws Exception {
		List<Map<String, String>> findVideoStreaming = BiliUtil.findVideoStreaming("/video/BV1Zv4y1E7jD","buvid3=A482E3B3-9600-C78A-83F9-820A593BD77D98106infoc; b_nut=1674643698; _uuid=A34D1EA8-8E3F-42AC-E823-31E87A106BFA196842infoc; rpdid=|(umYJYlYYY)0J'uY~RYRJ~|u; nostalgia_conf=-1; buvid_fp_plain=undefined; hit-new-style-dyn=0; hit-dyn-v2=1; i-wanna-go-back=-1; header_theme_version=CLOSE; CURRENT_BLACKGAP=0; buvid4=A7F1A7FD-D260-A5A3-C4CF-0978621B76B401106-023012518-I+fjjCdS2R4+iUreQE/8BA==; DedeUserID=3493262113376423; DedeUserID__ckMd5=d6afd5e2e0cb60b3; LIVE_BUVID=AUTO3516787998398287; b_ut=5; PVID=1; CURRENT_FNVAL=4048; CURRENT_PID=fed22140-d21a-11ed-8dbf-3f61c17c3c6f; FEED_LIVE_VERSION=V8; CURRENT_QUALITY=80; fingerprint=aaef0e9f1d452954ccb3f45ced603e16; buvid_fp=541890ad651676727c6770a2daf2a081; bp_video_offset_3493262113376423=785525518392885500; innersign=0; b_lsid=82F85E86_187949D9837; SESSDATA=b9852370,1697377571,aa6fa*41; bili_jct=057f32fb819f4bcb47d686c2772c026f; sid=6o43jv69; home_feed_column=4","D:\\flower\\uploadFile");
		System.out.println(findVideoStreaming);
	}
}
