package com.rubypaper.persistence.mybatis;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class EmployeeVO {
	private Long id; 				// 직원 아이디
	private String name; 			// 직원 이름
	private Timestamp startDate; 	// 입사일
	private String title; 			// 직급
	private String deptName; 		// 부서 이름
	private Double salary; 			// 급여
}
