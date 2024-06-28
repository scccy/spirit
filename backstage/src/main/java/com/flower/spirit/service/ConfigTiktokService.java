package com.flower.spirit.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ConfigTiktokDao;
import com.flower.spirit.entity.ConfigTiktokEntity;

@Service
public class ConfigTiktokService {
	
	
	@Autowired
	private ConfigTiktokDao ConfigTiktokDao;

	public List<ConfigTiktokEntity> findAll(ConfigTiktokEntity ConfigTiktokEntity) {
		return ConfigTiktokDao.findAll();
	}
	
	public ConfigTiktokEntity getData() {
		List<ConfigTiktokEntity> findAll = ConfigTiktokDao.findAll();
		if(findAll.size() ==0) {
			ConfigTiktokEntity ConfigTiktokEntity = new ConfigTiktokEntity();
			ConfigTiktokEntity save = ConfigTiktokDao.save(ConfigTiktokEntity);
			return save;
		}
		return findAll.get(0);
	}

	public AjaxEntity updateConfigTiktok(ConfigTiktokEntity ConfigTiktokEntity) {
		ConfigTiktokDao.save(ConfigTiktokEntity);
		if(null != ConfigTiktokEntity.getCookies() && !"".equals(ConfigTiktokEntity.getCookies())) {
			Global.tiktokCookie = ConfigTiktokEntity.getCookies();
		}
		if(null != ConfigTiktokEntity.getAnalysisserver() && !"".equals(ConfigTiktokEntity.getAnalysisserver())) {
			Global.analysiSserver = ConfigTiktokEntity.getAnalysisserver();
		}
		return new AjaxEntity(Global.ajax_success, "操作成功", ConfigTiktokEntity);
	}

}
