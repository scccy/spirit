package com.flower.spirit.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flower.spirit.service.AnalysisService;

@RestController
@RequestMapping("/api")
public class ApiController {
	
	@Autowired
	private AnalysisService analysisService;
	
	
	@RequestMapping("/processingVideos")
	public void processingVideos(String token,String video) {
		try {
			analysisService.processingVideos(token,video);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
