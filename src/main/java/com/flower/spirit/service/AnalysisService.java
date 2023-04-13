package com.flower.spirit.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.Aria2Util;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.ThreadConfig;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author flower
 *
 */
@Service
public class AnalysisService {
	
	
    @Value("${file.save}")
    private String savefile;
    
    @Value("${file.save.path}")
    private String uploadRealPath;
	
	@Autowired
	private VideoDataDao videoDataDao;
	
	private Logger logger = LoggerFactory.getLogger(AnalysisService.class);
	
	

	@SuppressWarnings("static-access")
	public void processingVideos(String token, String video) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
			if(null == token || !token.equals(Global.apptoken)) {
				return;
			}
			String platform = this.getPlatform(video);
	        WebClient webClient = ThreadConfig.getWebClient();
	        HtmlPage page = null;
	        try {
	            page = webClient.getPage(this.findAddr(video));
	        } catch (Exception e) {
	        	logger.error(e.getMessage());
	        }finally {
	            webClient.close();
	        }
	        webClient.waitForBackgroundJavaScript(300);
	        String pageXml = page.asXml();
	        Document parse = Jsoup.parse(pageXml);
	        Element render_data = parse.getElementById("RENDER_DATA");
	        String encode = URLDecoder.decode(render_data.html().substring("//<![CDATA[".length(), render_data.html().length() - "//]]>".length()).trim(), "UTF-8");
	        JSONObject jsonObject = JSON.parseObject(encode);
	        jsonObject.forEach((key, value) -> {
	        	if(this.isJSONString(value.toString())) {
	        		  JSONObject aweme = JSONObject.parseObject(value.toString()).getJSONObject("aweme");
	        		  if(aweme != null) {
	        			  JSONObject detail = aweme.getJSONObject("detail");
	        			  String awemeId = detail.getString("awemeId");
	        			  String desc = detail.getString("desc");
	        			  JSONObject videoobj = detail.getJSONObject("video");
	        	          String playApi = videoobj.getString("playApi");
	        	          String cover = videoobj.getString("cover");
	        	          //首先推送源文件到下载器 目前仅支持a2 其他待优化
	        	          String videofile = Global.a2_down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+awemeId+".mp4";
	        	          String videounrealaddr = savefile+"video/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+awemeId+".mp4";
	        	          if(Global.downtype.equals("a2")) {
	        	        	   Aria2Util.sendMessage(Global.a2_link,  Aria2Util.createparameter("https:"+playApi, Global.a2_down_path+"/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"), awemeId+".mp4", Global.a2_token));
	        	          }
	        	          //下载封面图当容器映射目录
	        	          String coverunaddr =  savefile+"cover/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+awemeId+".jpg";
	        	          HttpUtil.downLoadFromUrl("https:"+cover, awemeId+".jpg", uploadRealPath+"cover/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/");
	        	          //推送完成后建立历史资料  此处注意  a2 地址需要与spring boot 一致否则 无法打开视频
	        	          VideoDataEntity videoDataEntity = new VideoDataEntity(awemeId, desc, platform, coverunaddr, videofile,videounrealaddr);
	        	          videoDataDao.save(videoDataEntity);
	        	          //建档结束
	        		  }
	        	}
				
			});
	}
	
	
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
	
	public String findAddr(String videourl) {
		return this.getUrl(videourl);
	}
	
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
		return "未知";
    }

	
	
	
}
