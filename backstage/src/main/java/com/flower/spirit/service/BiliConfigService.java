package com.flower.spirit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flower.spirit.dao.BiliConfigDao;
import com.flower.spirit.entity.BiliConfigEntity;

@Service
public class BiliConfigService {
	
	@Autowired
	private BiliConfigDao BiliConfigDao;
	
	public BiliConfigEntity getData() {
		List<BiliConfigEntity> findAll = BiliConfigDao.findAll();
		return findAll.get(0);
	}

}
