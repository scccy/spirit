package com.flower.spirit.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.flower.spirit.common.AjaxEntity;
import com.flower.spirit.config.Global;
import com.flower.spirit.dao.CollectdDataDao;
import com.flower.spirit.dao.VideoDataDao;
import com.flower.spirit.entity.CollectDataDetailEntity;
import com.flower.spirit.entity.CollectDataEntity;
import com.flower.spirit.entity.VideoDataEntity;
import com.flower.spirit.utils.BiliUtil;
import com.flower.spirit.utils.DataUtil;
import com.flower.spirit.utils.DateUtils;
import com.flower.spirit.utils.HttpUtil;
import com.flower.spirit.utils.StringUtil;

@Service
public class CollectDataService {
	
	
	private ExecutorService exec = Executors.newFixedThreadPool(1);
	
	@Autowired
	private CollectdDataDao collectdDataDao;
	
	@Autowired
	private CollectDataDetailService collectDataDetailService;
	
	@Autowired
	private VideoDataService videoDataService;
	
	@Autowired
	private VideoDataDao videoDataDao;
	
	private Logger logger = LoggerFactory.getLogger(CollectDataService.class);
	
	
    /**
     * 文件储存真实路径
     */
    @Value("${file.save.path}")
    private String uploadRealPath;
    
    /**
     * 映射路径
     */
    @Value("${file.save}")
    private String savefile;
    
	
	
	@SuppressWarnings("serial")
	public AjaxEntity findPage(CollectDataEntity res) {
		PageRequest of= PageRequest.of(res.getPageNo(), res.getPageSize());
		Specification<CollectDataEntity> specification = new Specification<CollectDataEntity>() {

			@Override
			public Predicate toPredicate(Root<CollectDataEntity> root, CriteriaQuery<?> query,
					CriteriaBuilder criteriaBuilder) {
				Predicate predicate=criteriaBuilder.conjunction();
				CollectDataEntity seachDate = (CollectDataEntity) res;
				if(seachDate != null && StringUtil.isString(seachDate.getTaskid())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("taskid"), "%"+seachDate.getTaskid()+"%"));
				}
				if(seachDate != null && StringUtil.isString(seachDate.getPlatform())) {
					predicate.getExpressions().add(criteriaBuilder.like(root.get("platform"), "%"+seachDate.getPlatform()+"%"));
				}

				query.orderBy(criteriaBuilder.desc(root.get("id")));
				return predicate;
			}};
			
			Page<CollectDataEntity> findAll = collectdDataDao.findAll(specification,of);
			return new AjaxEntity(Global.ajax_success, "数据获取成功", findAll);
	}

	public AjaxEntity deleteCollectData(CollectDataEntity collectDataEntity) {
		collectdDataDao.deleteById(collectDataEntity.getId());
		return new AjaxEntity(Global.ajax_success, "操作成功", null);
	}

	/**
	 * 提交任务 
	 * @param collectDataEntity
	 * @return
	 */
	public AjaxEntity submitCollectData(CollectDataEntity collectDataEntity) {
		if(null != collectDataEntity.getPlatform() && collectDataEntity.getPlatform().equals("哔哩") ) {
			//必须授权ck
			if(Global.bilicookies.equals("")) {
				return new AjaxEntity(Global.ajax_uri_error, "必须填写bili ck", null);
			}
			String api = "https://api.bilibili.com/x/v3/fav/resource/ids?media_id="+collectDataEntity.getOriginaladdress()+"&platform=web";
			String httpGetBili = HttpUtil.httpGetBili(api, "UTF-8", Global.bilicookies);
			JSONArray jsonArray = JSONObject.parseObject(httpGetBili).getJSONArray("data");
			if(jsonArray.size() >0) {
				//进线程前创建collectDataEntity
				collectDataEntity.setTaskstatus("已提交待处理");
				collectDataEntity.setCreatetime(DateUtils.formatDateTime(new Date()));
				collectDataEntity.setCount(String.valueOf(jsonArray.size()));
				CollectDataEntity save = collectdDataDao.save(collectDataEntity);
				//提交线程
				exec.execute(() -> {
					try {
						this.createBiliData(save, jsonArray);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
				return new AjaxEntity(Global.ajax_success, "已提交线程处理,如填错但线程已开启请重启容器解决", null);
			}
			return new AjaxEntity(Global.ajax_uri_error, "数据为空 请检查收藏ID", null);
			
			
		}
		if(null != collectDataEntity.getPlatform() && collectDataEntity.getPlatform().equals("抖音") ) {
			return new AjaxEntity(Global.ajax_uri_error, "我还没做 哈哈~ 再等一下", null);
		}
		return null;
	}
	
	
	public void createBiliData(CollectDataEntity entity,JSONArray json) throws Exception {
		//线程开始  变更状态
		String videofile = uploadRealPath+"video/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"); //真实地址
		entity.setTaskstatus("已开始处理");
		collectdDataDao.save(entity);
		for(int i = 0;i<json.size();i++) {
			JSONObject data = json.getJSONObject(i);
			String avid = data.getString("id");
			String bvid = data.getString("bvid");
			Map<String, String> videoDataInfo = BiliUtil.getVideoDataInfo("/video/"+bvid);
			String status ="";
			if(videoDataInfo !=  null) {
				String cid = videoDataInfo.get("cid");
				List<VideoDataEntity> findByVideoid = videoDataService.findByVideoid(cid);
				if(findByVideoid.size() == 0) {
					Map<String, String> findVideoStreaming =  BiliUtil.findVideoStreamingNoData(videoDataInfo,"/video/"+bvid,Global.bilicookies,videofile);
					String coverunaddr =  savefile+"cover/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"); //映射
					String videounaddr =  savefile+"video/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM")+"/"+findVideoStreaming.get("videoname");//映射
					 //封面down
					HttpUtil.downBiliFromUrl(findVideoStreaming.get("pic"), findVideoStreaming.get("cid")+".jpg", uploadRealPath+"cover/"+DateUtils.getDate("yyyy")+"/"+DateUtils.getDate("MM"));
					//封面down
					VideoDataEntity videoDataEntity = new VideoDataEntity(findVideoStreaming.get("cid"),findVideoStreaming.get("title"), findVideoStreaming.get("desc"), "哔哩", coverunaddr+"/"+findVideoStreaming.get("cid")+".jpg", findVideoStreaming.get("video"),videounaddr,bvid);
				    videoDataDao.save(videoDataEntity);
				    logger.info("收藏"+(i+1)+"下载流程结束");
				}
				 //新建明细
				status =findByVideoid.size() == 0?"已完成":"已完成(未下载已存在)";
			}else {
				status ="视频异常下载失败";
			}
		   
		    CollectDataDetailEntity collectDataDetailEntity = new CollectDataDetailEntity();
		    collectDataDetailEntity.setDataid(entity.getId());
		    collectDataDetailEntity.setVideoid(videoDataInfo == null ?bvid:videoDataInfo.get("cid"));
		    collectDataDetailEntity.setOriginaladdress(bvid);
		    collectDataDetailEntity.setStatus(status);
		    collectDataDetailEntity.setCreatetime(DateUtils.formatDateTime(new Date()));
		    collectDataDetailService.save(collectDataDetailEntity);
		    //修改主体
		    String carriedout = entity.getCarriedout() == null ?"1":String.valueOf(Integer.parseInt(entity.getCarriedout())+1);
		    entity.setCarriedout(carriedout);
		    collectdDataDao.save(entity);
		}
		entity.setTaskstatus("处理完成");
		entity.setEndtime(DateUtils.formatDateTime(new Date()));
		collectdDataDao.save(entity);
		System.gc();
		
	}
	

}
