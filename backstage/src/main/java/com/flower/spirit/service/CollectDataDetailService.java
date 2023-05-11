package com.flower.spirit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.dao.CollectdDataDetailDao;

@Service
public class CollectDataDetailService {
	
	
	@Autowired
	private CollectdDataDetailDao collectdDataDetailDao;

}
