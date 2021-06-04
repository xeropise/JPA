package com.rubypaper.biz.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicUpdate;

import lombok.Data;

@Data
@DynamicUpdate
@Entity
@Table(name = "S_EMP")
public class Employee {

	@Id
	@GeneratedValue//(strategy = GenerationType.IDENTITY)
	@Column(length = 7, nullable = false)
	private Long id;
	
	private String name;
}
