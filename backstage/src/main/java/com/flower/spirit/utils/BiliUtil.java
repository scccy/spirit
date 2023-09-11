package com.flower.spirit.utils;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.FfmpegQueueDao;
import com.flower.spirit.dao.FfmpegQueueDataDao;
import com.flower.spirit.entity.FfmpegQueueDataEntity;
import com.flower.spirit.entity.FfmpegQueueEntity;

@Component
public class BiliUtil {
	
	private static Logger logger = LoggerFactory.getLogger(BiliUtil.class);
	
	
	@Autowired
	private  FfmpegQueueDataDao ffmpegQueueDataDao;
	
	
	@Autowired
	private  FfmpegQueueDao ffmpegQueueDao;
	
	
	private static BiliUtil biliUtil;
	
	@PostConstruct
	public void init(){
		biliUtil = this;
		biliUtil.ffmpegQueueDataDao = this.ffmpegQueueDataDao;
		biliUtil.ffmpegQueueDao = this.ffmpegQueueDao;
    }
	
	/**
	 * 
	 * 用于收藏夹下载  
	 * 
	 * 2023/09/11 移除参数url  无用参数
	 * 2023/09/11 移除参数filepath  优化路径生成
	 * 
	 * @throws Exception 
	 * 
	 */
	public static  Map<String, String> findVideoStreamingNoData(Map<String, String> videoDataInfo,String token,String quality) throws Exception {
		String api = buildInterfaceAddress(videoDataInfo.get("aid"), videoDataInfo.get("cid"), token,quality);
		String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", token);
		JSONObject parseObject = JSONObject.parseObject(httpGetBili);
		String filename = StringUtil.getFileName(videoDataInfo.get("title"), videoDataInfo.get("cid"));
		if(Integer.valueOf(Global.bilibitstream) >=120 && quality.equals("1")) {
			//执行DASH格式合并  默认取第一个  最大清晰度
//			Map<String, String> processing = processing(parseObject, videoDataInfo, filepath, filename);
			Map<String, String> processing = processing(parseObject, videoDataInfo, filename);
			return processing;
		}
		String video = parseObject.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
		if(Global.downtype.equals("http")) {
//			HttpUtil.downBiliFromUrl(video, filename+".mp4", filepath);
			HttpUtil.downBiliFromUrl(video, filename+".mp4", FileUtil.createTemporaryDirectory( Global.platform.bilibili.name(),filename));
		}
		if(Global.downtype.equals("a2")) {
			Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video, FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), filename, Global.down_path),filename+".mp4", Global.a2_token));
			//Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video, Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), filename+".mp4", Global.a2_token));
		}
//		videoDataInfo.put("video", filepath+"/"+filename+".mp4");
		videoDataInfo.put("video", FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename,  Global.platform.bilibili.name()));
		videoDataInfo.put("videoname", filename+".mp4");
		return videoDataInfo;
	}
	
	/**
	 * 解释video 信息 并下载源信息  此处不下载封面  仅下载视频
	 * 移除 方法参数用 String filepath 参数 2023/09/11
	 * @throws Exception 
	 * 
	 */
	public static List<Map<String, String>> findVideoStreaming(String url,String token) throws Exception {
	
		List<Map<String, String>> videoDataInfo = BiliUtil.getVideoDataInfo(url);
		List<Map<String, String>> res = new ArrayList<Map<String,String>>();
		for(int i =0;i<videoDataInfo.size();i++) {
			Map<String, String> map = videoDataInfo.get(i);
			String quality = map.get("quality");
			String api = buildInterfaceAddress(map.get("aid"), map.get("cid"), token,quality);
			String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", token);
			JSONObject parseObject = JSONObject.parseObject(httpGetBili);
			String filename = StringUtil.getFileName(map.get("title"), map.get("cid"));
			if(Integer.valueOf(Global.bilibitstream) >=120 && quality.equals("1")) {
				//执行DASH格式合并  默认取第一个  最大清晰度
				Map<String, String> processing = processing(parseObject, map, filename);
				res.add(processing);
				return res;
			}
			//普通mp4
			String video = parseObject.getJSONObject("data").getJSONArray("durl").getJSONObject(0).getString("url");
			if(Global.downtype.equals("http")) {
//				HttpUtil.downBiliFromUrl(video, filename+".mp4", filepath);
				HttpUtil.downBiliFromUrl(video, filename+".mp4", FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), filename));
			}
			if(Global.downtype.equals("a2")) {
				Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video, FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), filename, Global.down_path),filename+".mp4", Global.a2_token));
				//Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video, Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), filename+".mp4", Global.a2_token));
			}
//			map.put("video", filepath+"/"+filename+".mp4");
			map.put("video", FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename, Global.platform.bilibili.name()));
			map.put("videoname", filename+".mp4");
			res.add(map);
		}
		
		
		
		return res;
	}
	
	/**
	 * 
	 * 根据对应参数 下载视频 或者合并视频
	 * 移除方法中参数 String filepath 2023/09/11
	 * @param parseObject
	 * @param map
	 * @param filepath
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> processing(JSONObject parseObject,Map<String, String> map,String filename) throws Exception {
		//执行DASH格式合并  默认取第一个  最大清晰度
		String video = parseObject.getJSONObject("data").getJSONObject("dash").getJSONArray("video").getJSONObject(0).getString("base_url");
		String audio = parseObject.getJSONObject("data").getJSONObject("dash").getJSONArray("audio").getJSONObject(0).getString("base_url");
		//创建临时目录用于合并生成
		if(Global.downtype.equals("http")) {
			//http  需要创建临时目录
//			String newpath =filepath+"/"+map.get("cid");
			String newpath =FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), map.get("cid"));
			String filepath =FileUtil.createDirFile("", ".mp4", filename,Global.platform.bilibili.name());
			FileUtils.createDirectory(newpath);
			HttpUtil.downBiliFromUrl(video, filename+"-video.m4s", newpath);
			HttpUtil.downBiliFromUrl(audio, filename+"-audio.m4s", newpath);
			//此处可以直接合并 由于http 不是异步
			//ffmpeg -i video.m4s -i audio.m4s -c:v copy -c:a copy -f mp4 Download_video.mp4
			CommandUtil.command("ffmpeg -i "+newpath+File.separator+filename+"-video.m4s -i "+newpath+File.separator+filename+"-audio.m4s -c:v copy -c:a copy -f mp4 "+filepath+"/"+filename+".mp4");
			//删除
			FileUtils.deleteDirectory(newpath);
			
		}
		if(Global.downtype.equals("a2")) {
			//a2 不需要 目录有a2托管  此处路径应该可以优化
			String a2path= Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+map.get("cid");
			String videores = Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(video,a2path , filename+"-video.m4s", Global.a2_token));
			String audiores = Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createBiliparameter(audio,a2path , filename+"-audio.m4s", Global.a2_token));
			//保存到FfmpegQueueEntity队列
			logger.info("4k以上仅支持dash 提交到ffmpeg队列等待下载完成合并-前置任务结束");
			FfmpegQueueEntity ffmpegQueueEntity = new FfmpegQueueEntity();
			ffmpegQueueEntity.setVideoid(map.get("cid"));
			ffmpegQueueEntity.setVideoname(map.get("title"));
			ffmpegQueueEntity.setPendingfolder(a2path);
			ffmpegQueueEntity.setAudiostatus("0");
			ffmpegQueueEntity.setVideostatus("0");
			ffmpegQueueEntity.setFilepath(FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename,Global.platform.bilibili.name()));
//			ffmpegQueueEntity.setFilepath(filepath+"/"+filename+".mp4");
			ffmpegQueueEntity.setStatus("0");
			ffmpegQueueEntity.setCreatetime(DateUtils.getDateTime());
			biliUtil.ffmpegQueueDao.save(ffmpegQueueEntity);
			//数据
			FfmpegQueueDataEntity videoData = new FfmpegQueueDataEntity();
			videoData.setQueueid(ffmpegQueueEntity.getId());
			videoData.setTaskid(JSONObject.parseObject(videores).getString("result"));
			videoData.setFiletype("v");
			videoData.setStatus("0");
			videoData.setFilepath(a2path+File.separator+filename+"-video.m4s");
			videoData.setCreatetime(DateUtils.getDateTime());
			biliUtil.ffmpegQueueDataDao.save(videoData);
			
			
			FfmpegQueueDataEntity audioData = new FfmpegQueueDataEntity();
			audioData.setQueueid(ffmpegQueueEntity.getId());
			audioData.setTaskid(JSONObject.parseObject(audiores).getString("result"));
			audioData.setFiletype("a");
			audioData.setStatus("0");
			audioData.setFilepath(a2path+File.separator+filename+"-audio.m4s");
			audioData.setCreatetime(DateUtils.getDateTime());
			biliUtil.ffmpegQueueDataDao.save(audioData);
			//创建完成交由数据库处理
		}
//		map.put("video", filepath+"/"+filename+".mp4");
		map.put("video", FileUtil.createDirFile(FileUtil.uploadRealPath,".mp4",filename,Global.platform.bilibili.name()));
		map.put("videoname", filename+".mp4");
		return map;
	}
	
	
	/**
	 * 方法需要代码优化  有时间再说
	 * @param url
	 * @return
	 */
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
		if(videoData.getString("code").equals("0")) {		
			//优化多集问题 从page 里取
			
			String bvid = videoData.getJSONObject("data").getString("bvid");
			String aid = videoData.getJSONObject("data").getString("aid");
//			String cid = videoData.getJSONObject("data").getString("cid");
//			String title = videoData.getJSONObject("data").getString("title");
//			String pic = videoData.getJSONObject("data").getString("pic");
			String desc = videoData.getJSONObject("data").getString("desc");
			JSONObject dimension = videoData.getJSONObject("data").getJSONObject("dimension");
			Integer width = dimension.getInteger("width");
			Integer height = dimension.getInteger("height");
	
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
				if(width>=1920 ||height >=1920) {
					data.put("quality", "1");
				}else {
					data.put("quality", "0");
				}
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
	
	public static String buildInterfaceAddress(String aid,String cid,String token,String quality) {
		String bilibitstream = Global.bilibitstream;
		if(quality.equals("0")) {
			logger.info("视频没有2k以上进行画质降级");
			bilibitstream ="80"; //画质降级
		}
		String api ="https://api.bilibili.com/x/player/playurl?avid="+aid+"&cid="+cid;
		if(null != token && !token.equals("")) {
			if(!bilibitstream.equals("64")) {
				//vip
				if(Integer.valueOf(bilibitstream) >=120) {
					api =api+"&qn=0";
				}else {
					api =api+"&qn="+bilibitstream;
				}
				
			}else {
				api =api+"&qn=80";
			}
			
		}else {
			api =api+"&qn=64";
		}
		api =api+"&fnver=0";  //固定 0
		switch (bilibitstream) {
		case "80":
			api =api+"&fourk=1&fnval="+Integer.toString(16|128);   //4k 传128
			break;
		case "112":
			api =api+"&fourk=1&fnval="+Integer.toString(16|128);   //4k 传128
			break;
		case "116":
			api =api+"&fourk=1&fnval="+Integer.toString(16|128);   //4k 传128
			break;
		case "120":
			api =api+"&fourk=1&fnval="+Integer.toString(16|128);   //4k 传128
			break;
		case "125":
			api =api+"&fourk=1&fnval="+Integer.toString(16|64);   //hdr 传64
			break;
		case "126":
			api =api+"&fourk=1&fnval="+Integer.toString(16|512);   //杜比视界 传128
			break;
		case "127":
			api =api+"&fourk=1&fnval="+Integer.toString(16|1024);   //8k 传128
			break;
		default:
			api =api+"&fourk=0&fnval=1";
			break;
		}
		System.out.println(api);
		return api;
	}

	public static void main(String[] args) throws Exception {
		//video/BV1mz4y1q7Pb
		///video/BV1qM4y1w716

		List<Map<String, String>> findVideoStreaming = BiliUtil.findVideoStreaming("/video/BV1mz4y1q7Pb","cookie");
		System.out.println(findVideoStreaming);
	}
}
