package com.flower.spirit.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.service.AnalysisService;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	@Autowired
	private AnalysisService analysisService;
	
	
	@RequestMapping("/processingVideos")
	public AjaxEntity processingVideos(String token,String video) {
		try {
			 analysisService.processingVideos(token,video);
		}catch (Exception e) {
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", "");
	
	}

}
