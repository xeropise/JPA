package com.rubypaper.biz.repository;

import org.springframework.data.repository.CrudRepository;

import com.rubypaper.biz.domain.Department;

public interface DepartmentRepository extends CrudRepository<Department, Long> {

}
