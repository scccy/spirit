package com.flower.spirit.service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.StringUtil;


@Service
public class VideoDataService {
	
	@Autowired
	private VideoDataDao videoDataDao;

	@SuppressWarnings("serial")
	public AjaxEntity findPage(VideoDataEntity res) {
		PageRequest of= PageRequest.of(res.getPageNo(), res.getPageSize());
		Specification<VideoDataEntity> specification = new Specification<VideoDataEntity>() {

			@Override
			public Predicate toPredicate(Root<VideoDataEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Predicate predicate=criteriaBuilder.conjunction();
				VideoDataEntity seachDate = (VideoDataEntity) res;
				if(seachDate != null && StringUtil.isString(seachDate.getVideodesc())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("videodesc"), "%"+seachDate.getVideodesc()+"%"));
				}
				if(seachDate != null && StringUtil.isString(seachDate.getVideoplatform())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("videoplatform"), "%"+seachDate.getVideoplatform()+"%"));
				}
				
				return predicate;
			}};
			
			Page<VideoDataEntity> findAll = videoDataDao.findAll(specification,of);
			return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}

}
