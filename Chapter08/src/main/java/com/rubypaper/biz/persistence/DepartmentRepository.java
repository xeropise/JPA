package com.rubypaper.biz.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.rubypaper.biz.domain.Department;

@Repository
public class DepartmentRepository {

	@PersistenceContext
	private EntityManager em;
	
	public void insertDepartment(Department department) {
		System.out.println("===> JPA로 insertDepartement() 기능 처리");
		em.persist(department);
	}
	
	public void updateDepartment(Department department) {
		em.merge(department);
	}
	
	public void deleteDepartment(Department department) {
		em.remove(em.find(Department.class, department.getDeptId()));
	}
	
	public Department getDepartment(Department department) {
		System.out.println("===> JPA로 getDepartmenet() 기능 처리");
		return em.find(Department.class, department.getDeptId());
	}
	
	public List<Department> getDepartmentList(Department department) {
		return em.createQuery("from Department dept order by dept.deptId desc").getResultList();
	}
}
