package com.flower.spirit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.ProcessHistoryDao;
import com.flower.spirit.entity.ProcessHistoryEntity;

@Service
public class ProcessHistoryService {
	
	@Autowired
	private ProcessHistoryDao processHistoryDao;
	
	public ProcessHistoryEntity saveProcess(Integer id,String originaladdress,String videoplatform) {
		if(Global.openprocesshistory) {
			Integer ids = id==null?null:id;
			String status =id==null?"已提交未执行":"执行完毕";
			ProcessHistoryEntity processHistoryEntity = new ProcessHistoryEntity(ids, originaladdress, videoplatform,status);
			return processHistoryDao.save(processHistoryEntity);
		}
		return new ProcessHistoryEntity();
	}

}
