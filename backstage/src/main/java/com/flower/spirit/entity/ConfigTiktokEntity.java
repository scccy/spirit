package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "biz_tiktok_config")
public class ConfigTiktokEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2472625323664839181L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_tiktok_config")
	@TableGenerator(name = "biz_tiktok_config", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	private String cookies;
	
	/**
	 * 解析server
	 */
	private String analysisserver;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public String getAnalysisserver() {
		return analysisserver;
	}

	public void setAnalysisserver(String analysisserver) {
		this.analysisserver = analysisserver;
	}
	
	
	

}
