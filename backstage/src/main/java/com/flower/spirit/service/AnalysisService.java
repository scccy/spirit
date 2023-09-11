package com.flower.spirit.service;


import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.Aria2Util;
import com.flower.spirit.utils.BiliUtil;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.URLUtil;


/**
 * @author flower
 *
 */
@Service
public class AnalysisService {
	
	
    /**
     * 映射路径
     */
    @Value("${file.save}")
    private String savefile;
    
    /**
     * 文件储存真实路径
     */
    @Value("${file.save.path}")
    private String uploadRealPath;
	
	@Autowired
	private VideoDataDao videoDataDao;
	
	private Logger logger = LoggerFactory.getLogger(AnalysisService.class);
	
	@Autowired
	private ProcessHistoryService processHistoryService;
	
	

	/**解析资源
	 * @param token
	 * @param video
	 * @throws Exception 
	 */
	public void processingVideos(String token, String video) throws Exception {
			if(null == token || !token.equals(Global.apptoken)) {
				logger.info("token异常");
				return;
			}
			logger.info("解析开始~原地址:"+video);
			String platform = this.getPlatform(video);
			if(platform.equals("抖音")) {
				this.dyvideo(platform, this.getUrl(video));
				return;
			}
			if(platform.equals("哔哩")) {
				this.bilivideo(platform, this.getUrl(video));
				return;
			}
	       
	}
	
	/**
	 * 哔哩解析
	 * @param platform
	 * @param video
	 * @throws Exception
	 */
	public void bilivideo(String platform,String  video) throws Exception {
		  logger.info("bilivideo down");
		  ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, video, platform);
		  //String videofile = uploadRealPath+"video/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"); //真实地址
		  try {
//			 Map<String, String> findVideoStreaming = BiliUtil.findVideoStreaming(video, Global.bilicookies, videofile);
			 /*
			  * bili 的下载 此方法内没有  只下了 封面 视频源文件下载 从findVideoStreaming方法中完成  
			  * **/
			 List<Map<String, String>> findVideoStreaming = BiliUtil.findVideoStreaming(video, Global.bilicookies);
			 for(int i = 0;i<findVideoStreaming.size();i++) {
				 Map<String, String> map = findVideoStreaming.get(i);
				 String filename = StringUtil.getFileName(map.get("title"), map.get("cid"));
				 
//				 String coverunaddr =  savefile+"cover/bilibili/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"); //映射
//				 String videounaddr =  savefile+"video/bilibili/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+map.get("videoname");//映射
//				 
				 
				 String coverunaddr =  FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,Global.platform.bilibili.name());
				 String videounaddr =  FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,Global.platform.bilibili.name());
				 //封面down
//				 HttpUtil.downBiliFromUrl(map.get("pic"), filename+".jpg", uploadRealPath+"cover/bilibili/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"));
				 HttpUtil.downBiliFromUrl(map.get("pic"), filename+".jpg", FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), filename));
				//封面down
				 VideoDataEntity videoDataEntity = new VideoDataEntity(map.get("cid"),map.get("title"), map.get("desc"), platform, coverunaddr+"/"+filename+".jpg", map.get("video"),videounaddr,video);
			     videoDataDao.save(videoDataEntity);
			     logger.info("下载流程结束");
			     processHistoryService.saveProcess(saveProcess.getId(), video, platform);
			 }
		} catch (Exception e) {
			throw e;
		}
	}
	
	
	/**抖音解析
	 * @param platform
	 * @param video
	 * @throws Exception
	 */
	public void dyvideo(String platform,String  video) throws Exception {
		 ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, video, platform);
		 Map<String, String> downVideo = DouUtil.downVideo(video);
		
		 this.putRecord(downVideo.get("awemeid"), downVideo.get("desc"), downVideo.get("videoplay"), downVideo.get("cover"), platform,video,downVideo.get("type"),downVideo.get("cookie"));
		 System.gc();
		 
		 processHistoryService.saveProcess(saveProcess.getId(), video, platform);

	}
	
	
	/**
	 * 推送建档
	 * @param awemeId
	 * @param desc
	 * @param playApi
	 * @param cover
	 * @param platform
	 */
	public void putRecord(String awemeId,String desc,String playApi,String cover,String platform,String originaladdress,String type,String cookie) {
		String filename = StringUtil.getFileName(desc, awemeId);
	    String videofile = Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".mp4";
        String videounrealaddr = savefile+"video/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".mp4";
        String coverunaddr =  savefile+"cover/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".jpg";
		if(type.equals("client")) {
			    logger.info("已使用htmlunit进行解析,下载器类型为:"+Global.downtype);
		        if(Global.downtype.equals("a2")) {
		      	   Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createparameter("https:"+playApi, Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), filename+".mp4", Global.a2_token));
		      	   videofile = "/app/resources/video/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".mp4";
		        }
		        if(Global.downtype.equals("http")) {
		        	//内置下载器
		        	HttpUtil.downLoadFromUrl("https:"+playApi, filename+".mp4","/app/resources/video/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"));
		        	videofile = "/app/resources/video/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".mp4";
		        }
		        //下载封面图当容器映射目录
		        HttpUtil.downLoadFromUrl("https:"+cover, filename+".jpg", uploadRealPath+"cover/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/");
		}
		if(type.equals("api")) {
			logger.info("已使用api进行解析,下载器类型为:"+Global.downtype);
	        if(Global.downtype.equals("a2")) {
		      	   Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createDouparameter(playApi, Global.down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), filename+".mp4", Global.a2_token,cookie));
		      	   videofile = "/app/resources/video/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".mp4";
	        }
	        if(Global.downtype.equals("http")) {
	        	//内置下载器
	        	HttpUtil.downDouFromUrl(playApi, filename+".mp4","/app/resources/video/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"),cookie);
	        	videofile = "/app/resources/video/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+filename+".mp4";
	        }
	        HttpUtil.downLoadFromUrl(cover, filename+".jpg", uploadRealPath+"cover/douyin/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/");
		}
        //推送完成后建立历史资料  此处注意  a2 地址需要与spring boot 一致否则 无法打开视频
        VideoDataEntity videoDataEntity = new VideoDataEntity(awemeId,desc, desc, platform, coverunaddr, videofile,videounrealaddr,originaladdress);
        videoDataDao.save(videoDataEntity);
		logger.info("下载流程结束");
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
	
	/**
	 * 获得文本中的https地址
	 * @param videourl
	 * @return
	 */
	public String findAddr(String videourl) {
		return this.getUrl(videourl);
	}
	
	/**
	 * 正则获取url
	 * @param input
	 * @return
	 */
	public  String getUrl(String input) {
        String regex = "(https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }
	public  String getPlatform(String input) {
		if(input.contains("抖音")) {
			return "抖音";
		}
		if(input.contains("哔哩")) {
			return "哔哩";
		}
		return URLUtil.urlAnalysis(input);
    }

	
	
	
}
