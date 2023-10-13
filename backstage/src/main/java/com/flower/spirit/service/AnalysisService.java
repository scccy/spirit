package com.flower.spirit.service;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.Aria2Util;
import com.flower.spirit.utils.BiliUtil;
import com.flower.spirit.utils.CommandUtil;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.DouUtil;
import com.flower.spirit.utils.FileUtil;
import com.flower.spirit.utils.FileUtils;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.Steamcmd;
import com.flower.spirit.utils.StringUtil;
import com.flower.spirit.utils.TikTokUtil;
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
	
	
	private ExecutorService steamcmd = Executors.newFixedThreadPool(1);
	
	private ExecutorService douyin = Executors.newFixedThreadPool(1);
	
	private ExecutorService bilibili = Executors.newFixedThreadPool(1);

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
				douyin.execute(() -> {
					try {
						this.dyvideo(platform, this.getUrl(video));
					}catch (Exception e) {
						
					}
				});
				return;
			}
			if(platform.equals("哔哩")) {
				bilibili.execute(() -> {
					try {
						this.bilivideo(platform, this.getUrl(video));
					}catch (Exception e) {
						
					}
				});
				return;
			}
			if(platform.equals("steam")) {
				
				steamcmd.execute(() -> {
					try {
						this.steamwork(video);
					}catch (Exception e) {
						
					}
				});
				return;
			}
			if(platform.equals("tiktok")) {
				douyin.execute(() -> {
					try {
						this.tiktok(platform, this.getUrl(video));
					}catch (Exception e) {
						logger.info("tiktok解析异常");
					}
				});
			}
	       
	}
	
	private void tiktok(String platform, String url) throws IOException {
		 ProcessHistoryEntity saveProcess = processHistoryService.saveProcess(null, url, platform);
		 String tikTokVideoId = TikTokUtil.getTikTokVideoId("https://vt.tiktok.com/ZSN2uaA2M/");
			if(tikTokVideoId != null) {
				Map<String, String> videoData = TikTokUtil.getVideoData(tikTokVideoId);
				String awemeid = videoData.get("awemeid");
				String videoplay = videoData.get("videoplay");
				String desc = videoData.get("desc");
				String cover = videoData.get("cover");
				
				String filename = StringUtil.getFileName(desc, awemeid);
				String videofile = FileUtil.createDirFile(Global.down_path, ".mp4", filename, Global.platform.tiktok.name());
			    String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename, Global.platform.tiktok.name());
		        String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename, Global.platform.tiktok.name());
				
		        if(Global.downtype.equals("a2")) {
			      	 Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createparameter(videoplay, FileUtil.createTemporaryDirectory(Global.platform.tiktok.name(), filename, Global.down_path), filename+".mp4", Global.a2_token));
		        }
		        if(Global.downtype.equals("http")) {
		        	//内置下载器
		        	HttpUtil.downDouFromUrl(videoplay, filename+".mp4",FileUtil.createTemporaryDirectory(Global.platform.tiktok.name(), filename, FileUtil.uploadRealPath),null);
		        }
		        videofile = FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename, Global.platform.tiktok.name());
		        HttpUtil.downLoadFromUrl(cover, filename+".jpg", FileUtil.createTemporaryDirectory(Global.platform.tiktok.name(), filename, uploadRealPath)+"/");
		        VideoDataEntity videoDataEntity = new VideoDataEntity(awemeid,desc, desc, platform, coverunaddr, videofile,videounrealaddr,url);
		        videoDataDao.save(videoDataEntity);
				logger.info("下载流程结束");
			}
		 processHistoryService.saveProcess(saveProcess.getId(), url, platform);
	
	}
/**
	 * 不支持 从本处登录帐号并验证 二次令牌 请先使用 docker exec 进行登录帐号 在配置帐号密码进行下载
	 * 支持从steam 工坊下载
	 * @param video
	 * @throws IOException 
	 */
	private void steamwork(String video) throws IOException {
		File file = new File("/app/db/account.txt");
		if(file.exists()) {
			String account ="";
			String password ="";
	        BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String readStr;
	            while ((readStr = reader.readLine()) != null) {
	            	if(readStr.contains("account")) {
	            		 account = readStr.replaceAll("account:", "");
	            	}
	            	if(readStr.contains("password")) {
	            		password = readStr.replaceAll("password:", "");
	           	}
	            }
	            reader.close();

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (reader != null) {
	                try {
	                    reader.close();
	                } catch (IOException e1) {
	                    e1.printStackTrace();
	                }
	            }
	        }
	        String wallpaperId = getWallpaperId(video);
	        VideoDataEntity findByVideoid = findByVideoid(video,"wallpaper");
	        if(findByVideoid != null) {
	        	logger.info("重复ID 不下载");
	        	return;
	        }
			String steamcmd = CommandUtil.steamcmd(account,password,wallpaperId);
			if(!steamcmd.equals("")) {
				//下载完成 cp 文件
				logger.info("ok");
				String localapp = uploadRealPath+"wallpaper/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM");
				FileUtils.createDirectory(localapp);
				CommandUtil.command("mv "+steamcmd+" "+localapp);
//				复制完成 建档
				String json = localapp+"/"+wallpaperId+"/project.json";
				try {
					String jsonContent = new String(Files.readAllBytes(Paths.get(json)));
					JSONObject jsonObject = JSON.parseObject(jsonContent);
					String filename = jsonObject.getString("file");
					String previewname = jsonObject.getString("preview");
					String title = jsonObject.getString("title");
					String cosaddr = savefile+"wallpaper/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+wallpaperId;
					VideoDataEntity videoDataEntity = new VideoDataEntity(wallpaperId,title,title, "wallpaper", cosaddr+"/"+previewname, localapp+"/"+wallpaperId+"/"+filename,cosaddr+"/"+filename,video);
				    videoDataDao.save(videoDataEntity);
				    logger.info("下载流程结束");
				} catch (IOException e) {
					logger.info("建档异常-");
				}
			}
		}else {
			//文件不存在 认为使用screen 模式 
		    String wallpaperId = getWallpaperId(video);
	        VideoDataEntity findByVideoid = findByVideoid(video,"wallpaper");
	        if(findByVideoid != null) {
	        	logger.info("重复ID 不下载");
	        	return;
	        }
			try {
				String execAndListening = Steamcmd.execAndListening(wallpaperId);
				if(execAndListening != null) {
					logger.info("ok");
					String localapp = uploadRealPath+"wallpaper/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM");
					FileUtils.createDirectory(localapp);
					CommandUtil.command("mv "+execAndListening+" "+localapp);
//					复制完成 建档
					String json = localapp+"/"+wallpaperId+"/project.json";
					try {
						String jsonContent = new String(Files.readAllBytes(Paths.get(json)));
						JSONObject jsonObject = JSON.parseObject(jsonContent);
						String filename = jsonObject.getString("file");
						String previewname = jsonObject.getString("preview");
						String title = jsonObject.getString("title");
						String cosaddr = savefile+"wallpaper/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+wallpaperId;
						VideoDataEntity videoDataEntity = new VideoDataEntity(wallpaperId,title,title, "wallpaper", cosaddr+"/"+previewname, localapp+"/"+wallpaperId+"/"+filename,cosaddr+"/"+filename,video);
					    videoDataDao.save(videoDataEntity);
					     logger.info("下载流程结束");
					} catch (IOException e) {
						logger.info("建档异常-");
					}
				}
			} catch (Exception e) {
				logger.info("系统异常");
			}
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
		  try {
			 /*
			  * bili 的下载 此方法内没有  只下了 封面 视频源文件下载 从findVideoStreaming方法中完成  
			  * **/
			 List<Map<String, String>> findVideoStreaming = BiliUtil.findVideoStreaming(video, Global.bilicookies);
			 for(int i = 0;i<findVideoStreaming.size();i++) {
				 Map<String, String> map = findVideoStreaming.get(i);
				 String filename = StringUtil.getFileName(map.get("title"), map.get("cid"));		 
				 String coverunaddr =  FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename,Global.platform.bilibili.name());
				 String videounaddr =  FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename,Global.platform.bilibili.name());
				 //封面down
				 HttpUtil.downBiliFromUrl(map.get("pic"), filename+".jpg", FileUtil.createTemporaryDirectory(Global.platform.bilibili.name(), filename));
				//封面down
				 VideoDataEntity videoDataEntity = new VideoDataEntity(map.get("cid"),map.get("title"), map.get("desc"), platform, coverunaddr, map.get("video"),videounaddr,video);
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
		String videofile = FileUtil.createDirFile(Global.down_path, ".mp4", filename, Global.platform.douyin.name());
	    String videounrealaddr = FileUtil.createDirFile(FileUtil.savefile, ".mp4", filename, Global.platform.douyin.name());
        String coverunaddr = FileUtil.createDirFile(FileUtil.savefile, ".jpg", filename, Global.platform.douyin.name());
		if(type.equals("client")) {
			    logger.info("已使用htmlunit进行解析,下载器类型为:"+Global.downtype);
		        if(Global.downtype.equals("a2")) {
		           Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createparameter("https:"+playApi, FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, Global.down_path), filename+".mp4", Global.a2_token));
		        }
		        if(Global.downtype.equals("http")) {
		        	//内置下载器
		        	HttpUtil.downLoadFromUrl("https:"+playApi, filename+".mp4",FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, FileUtil.uploadRealPath));
		        }
		        videofile = FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename, Global.platform.douyin.name());
		        //下载封面图当容器映射目录
		        HttpUtil.downLoadFromUrl("https:"+cover, filename+".jpg", FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, uploadRealPath)+"/");
		}
		if(type.equals("api")) {
			logger.info("已使用api进行解析,下载器类型为:"+Global.downtype);
	        if(Global.downtype.equals("a2")) {
		      	 Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createDouparameter(playApi, FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, Global.down_path), filename+".mp4", Global.a2_token,cookie));
	        }
	        if(Global.downtype.equals("http")) {
	        	//内置下载器
	        	HttpUtil.downDouFromUrl(playApi, filename+".mp4",FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, FileUtil.uploadRealPath),cookie);
	        }
	        videofile = FileUtil.createDirFile(FileUtil.uploadRealPath, ".mp4", filename, Global.platform.douyin.name());
	        HttpUtil.downLoadFromUrl(cover, filename+".jpg", FileUtil.createTemporaryDirectory(Global.platform.douyin.name(), filename, uploadRealPath)+"/");
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
		if(input.contains("steamcommunity.com")) {
			return "steam";
		}
		return URLUtil.urlAnalysis(input);
    }

	private String getWallpaperId(String url) {
		 // 创建正则表达式模式，匹配id参数的值
        Pattern pattern = Pattern.compile("\\?id=(\\d+)");
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            // 提取id的值
            String idValue = matcher.group(1);
            return idValue;
        } else {
            System.out.println("ID not found in the URL.");
        }
		return null;
	}
	
	public VideoDataEntity findByVideoid(String id,String platform) {
		 List<VideoDataEntity> findByVideoid = videoDataDao.findByVideoidAndVideoplatform(id,platform);
		 if(findByVideoid.size()>1) {
			 return findByVideoid.get(0);
		 }
		return null;
	}
	
	
}
