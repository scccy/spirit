package com.flower.spirit.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.flower.spirit.entity.BiliConfigEntity;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.TikTokConfigEntity;
import com.flower.spirit.service.BiliConfigService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.service.DownloaderService;
import com.flower.spirit.service.TikTokConfigService;

/**
 * @author flower
 *程序初始化入口 加载常量信息到应用缓存
 */
@Configuration
public class AppConfig {
	
	@Autowired
	private DownloaderService downloaderService;
	
	@Autowired
	private ConfigService configService;
	

	@Autowired
	private BiliConfigService biliConfigService;
	
	@Autowired
	private TikTokConfigService  tikTokConfigService;
	
	
	@PostConstruct
	public void init() {
		downloaderService.renovate();
		ConfigEntity data = configService.getData();
		Global.apptoken =data.getApptoken();
		if(null != data.getOpenprocesshistory() && data.getOpenprocesshistory().equals("1")) {
			Global.openprocesshistory =true;
		}
		BiliConfigEntity bili = biliConfigService.getData();
		Global.bilicookies =bili.getBilicookies();
		if(null != bili.getBigmember() && bili.getBigmember().equals("是")) {
			Global.bilimember= true;
		}
		if(null != bili.getBitstream() && !"".equals(bili.getBitstream())) {
			Global.bilibitstream= bili.getBitstream();
		}
		TikTokConfigEntity tiktok = tikTokConfigService.getData();
		if(null !=tiktok.getCookies() && !"".equals(tiktok.getCookies())) {
			Global.tiktokCookie =tiktok.getCookies();
		}
		if(null != tiktok.getAnalysisserver() && !"".equals(tiktok.getAnalysisserver())) {
			Global.analysiSserver =tiktok.getAnalysisserver();
		}
	}

}
