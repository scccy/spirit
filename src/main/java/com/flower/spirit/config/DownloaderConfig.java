package com.flower.spirit.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.flower.spirit.service.DownloaderService;

@Configuration
public class DownloaderConfig {
	
	@Autowired
	private DownloaderService downloaderService;
	
	@PostConstruct
	public void init() {
		downloaderService.renovate();
	}

}
