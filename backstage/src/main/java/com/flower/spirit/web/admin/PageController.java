package com.flower.spirit.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.flower.spirit.config.Global;
import com.flower.spirit.entity.BiliConfigEntity;
import com.flower.spirit.entity.ConfigEntity;
import com.flower.spirit.entity.CookiesConfigEntity;
import com.flower.spirit.entity.ConfigTiktokEntity;
import com.flower.spirit.service.BiliConfigService;
import com.flower.spirit.service.ConfigService;
import com.flower.spirit.service.CookiesConfigService;
import com.flower.spirit.service.ConfigTiktokService;


/**
 * 页面类控制器
 * @author flower
 *
 */
@Controller
@RequestMapping(value = "/admin")
public class PageController {
	

	@Autowired
	private ConfigService configService;
	
	@Autowired
	private BiliConfigService biliConfigService;
	
	@Autowired
	private ConfigTiktokService  ConfigTiktokService;
	
	@Autowired
	private CookiesConfigService cookiesConfigService;
	
	/**
	 * 管理员控制台
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/admin")
	public String admin(HttpServletRequest request) {
		if(request.getSession().getAttribute(Global.user_session_key) ==  null) {
			return "admin/login";
		}else {
			return "admin/index";
		}
	}
	/**
	 * 管理员登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/login")
	public String login(HttpServletRequest request) {
		if(request.getSession().getAttribute(Global.user_session_key) !=  null) {
			return "redirect:/admin/index";
		}
		return "admin/login";
	}

	/**
	 * 管理员控制台
	 * @return
	 */
	@RequestMapping(value = "/index")
	public String index() {
		return "admin/index";
	}
	
	/**
	 * 后台欢迎页
	 * @return
	 */
	@RequestMapping(value = "/welcome")
	public String welcome() {
		return "admin/welcome";
	}
	@RequestMapping(value = "/update")
	public String update() {
		return "admin/update";
	}
	/**
	 * 退出登录
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/loginOut")
	public String loginOut(HttpServletRequest request) {
		request.getSession().setAttribute(Global.user_session_key, null);
		return "admin/login";
	}
	/**
	 * 用户列表页
	 * @return
	 */
	@RequestMapping(value = "/userList")
	public String userList() {
		return "admin/userList";
	}
	/**
	 * 新增用户页
	 * @return
	 */
	@RequestMapping(value = "/addUser")
	public String addUser() {
		return "admin/addUser";
	}
	
	/**
	 * 下载列表页
	 * @return
	 */
	@RequestMapping(value = "/downLoaderList")
	public String downLoaderList() {
		return "admin/downLoaderList";
	}
	/**
	 * 系统配置页
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/config")
	public String config(Model model) {
		ConfigEntity config = configService.getData();
		BiliConfigEntity bili = biliConfigService.getData();
		ConfigTiktokEntity tiktok = ConfigTiktokService.getData();
		CookiesConfigEntity cookies = cookiesConfigService.getData();
		model.addAttribute("bili", bili);
		model.addAttribute("config", config);
		model.addAttribute("tiktok", tiktok);
		model.addAttribute("cookies", cookies);
		return "admin/config";
	}
	/**
	 * 视频列表页
	 * @return
	 */
	@RequestMapping(value = "/videoDataList")
	public String videoDataList() {
		return "admin/videoDataList";
	}
	
	
	@RequestMapping(value = "/processHistoryList")
	public String processHistoryList() {
		return "admin/processHistoryList";
	}
	
	@RequestMapping(value = "/collectDataList")
	public String collectDataList() {
		return "admin/collectDataList";
	}
	
	
	@RequestMapping(value = "/collectDataDetailList")
	public String collectDataDetailList(HttpServletRequest request,Model model) {
		String taskid = request.getParameter("taskid");
		model.addAttribute("taskid", taskid);
		return "admin/collectDataDetailList";
	}
}
