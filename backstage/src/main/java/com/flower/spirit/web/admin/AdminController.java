package com.flower.spirit.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.common.RequestEntity;
import com.flower.spirit.entity.BiliConfigEntity;
import com.flower.spirit.entity.CollectDataEntity;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.DownloaderEntity;
import com.flower.spirit.entity.ProcessHistoryEntity;
import com.flower.spirit.entity.UserEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.service.BiliConfigService;
import com.flower.spirit.service.CollectDataService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.service.DownloaderService;
import com.flower.spirit.service.ProcessHistoryService;
import com.flower.spirit.service.SystemService;
import com.flower.spirit.service.UserService;
import com.flower.spirit.service.VideoDataService;


/**
 * 
 * 后台api 控制器
 * @author flower
 *
 */
@RestController
@RequestMapping("/admin/api")
public class AdminController {
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private UserService userService;
	
	
	@Autowired
	private DownloaderService downloaderService;
	
	
	@Autowired
	private ConfigService configService;

	
	@Autowired
	private VideoDataService videoDataService;
	
	@Autowired
	private BiliConfigService biliConfigService;
	
	@Autowired
	private ProcessHistoryService processHistoryService;
	
	@Autowired
	private CollectDataService collectDataService;
	
	/**  
	
	 * <p>Title: login</p>  
	
	 * <p>Description:用户管理员登录 </p>  
	
	 * @param userEntity
	 * @return  
	
	 */  
	@PostMapping(value = "/login")
	public AjaxEntity login(UserEntity userEntity,HttpServletRequest request) {
		return systemService.loginUser(userEntity,request);
	}
/**  
	
	 * <p>Title: findUserList</p>  
	
	 * <p>Description:分页获取管理员的列表 </p>  
	
	 * @param res
	 * @param request
	 * @return  
	
	 */  
	@PostMapping(value = "/findUserList")
	public AjaxEntity findUserList(RequestEntity res,HttpServletRequest request) {
		return userService.findUserList(res);
	}
	
	/**  
	
	 * <p>Title: addUser</p>  
	
	 * <p>Description: 添加用户</p>  
	
	 * @param userEntity
	 * @param request
	 * @return  
	
	 */  
	@PostMapping(value = "/addUser")
	public AjaxEntity addUser(UserEntity userEntity,HttpServletRequest request) {
		return userService.addUser(userEntity);
	}
	
	/**
	 * 删除管理用户
	 * @param userEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/delUser")
	public AjaxEntity delUser(UserEntity userEntity,HttpServletRequest request) {
		return userService.delUser(userEntity);
	}
	
	/**
	 * 分页获取下载器列表
	 * @param res
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/finddownLoaderList")
	public AjaxEntity finddownLoaderList(RequestEntity res,HttpServletRequest request) {
		return downloaderService.finddownLoaderList(res);
	}
	
	/**
	 * 删除下载器
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/deleteDownLoader")
	public AjaxEntity delDownLoader(DownloaderEntity downloaderEntity,HttpServletRequest request) {
		return downloaderService.delDownLoader(downloaderEntity);
	}
	
	/**
	 * 新增或修改下载器
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/addDownLoader")
	public AjaxEntity addDownLoader(DownloaderEntity downloaderEntity,HttpServletRequest request) {
		return downloaderService.addDownLoader(downloaderEntity);
	}
	/**
	 * 获取单个下载器信息
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/getDownLoader")
	public AjaxEntity getDownLoader(DownloaderEntity downloaderEntity,HttpServletRequest request) {
		return downloaderService.getDownLoader(downloaderEntity);
	}
	
	/**
	 * 修改系统基础设置 apptoken
	 * @param configEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/saveConfig")
	public AjaxEntity saveConfig(ConfigEntity configEntity,HttpServletRequest request) {
		return configService.saveConfig(configEntity);
	}
	/**
	 * 分页获取已缓存的视频历史记录
	 * @param videoDataEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/findVideoDataList")
	public AjaxEntity findVideoDataList(VideoDataEntity videoDataEntity,HttpServletRequest request) {
		return videoDataService.findPage(videoDataEntity);
	}
	
	/**
	 * 删除视频缓存信息及视频文件
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@GetMapping(value = "/deleteVideoData")
	public AjaxEntity deleteVideoData(VideoDataEntity downloaderEntity,HttpServletRequest request) {
		return videoDataService.deleteVideoData(downloaderEntity);
	}
	
	/**
	 * 更新视频基础文件
	 * @param downloaderEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/updateVideoData")
	public AjaxEntity updateVideoData(VideoDataEntity downloaderEntity,HttpServletRequest request) {
		return videoDataService.updateVideoData(downloaderEntity);
	}
	
	
	/**修改Bili配置信息
	 * @param biliConfigEntity
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/updateBiliConfig")
	public AjaxEntity updateBiliConfig(BiliConfigEntity biliConfigEntity,HttpServletRequest request) {
		return biliConfigService.updateBiliConfig(biliConfigEntity);
	}
	
	@GetMapping(value = "/getBiliCode")
	public AjaxEntity getBiliCode() {
		return biliConfigService.getBiliCode();
	}
	
	
	@GetMapping(value = "/checkBiliLogin")
	public AjaxEntity checkBiliLogin(String qrcodekey) {
		return biliConfigService.checkBiliLogin(qrcodekey);
	}
	
	@PostMapping(value = "/findProcessHistoryList")
	public AjaxEntity findProcessHistoryList(ProcessHistoryEntity processHistoryEntity,HttpServletRequest request) {
		return processHistoryService.findPage(processHistoryEntity);
	}
	
	@GetMapping(value = "/deleteProcessHistoryData")
	public AjaxEntity deleteProcessHistoryData(ProcessHistoryEntity processHistoryEntity,HttpServletRequest request) {
		return processHistoryService.deleteProcessHistoryData(processHistoryEntity);
	}
	
	@PostMapping(value = "/findCollectDataList")
	public AjaxEntity findCollectDataList(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.findPage(collectDataEntity);
	}
	
	@GetMapping(value = "/deleteCollectData")
	public AjaxEntity deleteCollectData(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.deleteCollectData(collectDataEntity);
	}
	@GetMapping(value = "/submitCollectData")
	public AjaxEntity submitCollectData(CollectDataEntity collectDataEntity,HttpServletRequest request) {
		return collectDataService.submitCollectData(collectDataEntity);
	}
} 
