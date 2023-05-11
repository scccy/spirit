package com.flower.spirit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.dao.CollectdDataDetailDao;
import com.flower.spirit.entity.CollectDataDetailEntity;

@Service
public class CollectDataDetailService {
	
	
	@Autowired
	private CollectdDataDetailDao collectdDataDetailDao;

	public CollectDataDetailEntity save(CollectDataDetailEntity collectDataDetailEntity) {
		return collectdDataDetailDao.save(collectDataDetailEntity);
	}

}
