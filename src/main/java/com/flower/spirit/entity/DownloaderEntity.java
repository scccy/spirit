package com.flower.spirit.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "biz_downloader")
public class DownloaderEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 441169571615432179L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE,generator="downloader_seq")
	@TableGenerator(name = "downloader_seq", allocationSize = 1, table = "seq_common", pkColumnName = "seq_id", valueColumnName = "seq_count")
    private Integer id;
	
	

}
