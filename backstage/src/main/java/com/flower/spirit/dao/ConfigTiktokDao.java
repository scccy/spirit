package com.flower.spirit.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.flower.spirit.entity.ConfigTiktokEntity;




@Repository
@Transactional
public interface ConfigTiktokDao extends PagingAndSortingRepository<ConfigTiktokEntity, Integer>, JpaSpecificationExecutor<ConfigTiktokEntity>{

	
	public List<ConfigTiktokEntity> findAll();

	

}
