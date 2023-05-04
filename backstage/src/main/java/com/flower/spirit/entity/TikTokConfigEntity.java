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
public class TikTokConfigEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2472625323664839181L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="biz_tiktok_config")
	@TableGenerator(name = "biz_tiktok_config", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;

}
