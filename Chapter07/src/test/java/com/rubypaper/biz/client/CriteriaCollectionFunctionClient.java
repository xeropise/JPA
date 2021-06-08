package com.rubypaper.biz.client;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.rubypaper.biz.domain.Department;
import com.rubypaper.biz.domain.Employee;

public class CriteriaCollectionFunctionClient {

	public static void main(String[] args) {
		EntityManagerFactory emf = 
					Persistence.createEntityManagerFactory("Chapter07");
		
		try {			
			dataInsert(emf);
			dataSelect(emf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emf.close();
		}
	}
		
	private static void dataSelect(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		
		// 크라이테리어 빌더 생성
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Department> criteriaQuery = 
				builder.createQuery(Department.class);
		
		// FROM Employee emp
		Root<Department> dept = criteriaQuery.from(Department.class);
		
		// SELECT dept
		criteriaQuery.select(dept).distinct(true);
		
		// WHERE employeeList.size > 2
		criteriaQuery.where(
				builder.ge(builder.size(dept.<List<Employee>>get("employeeList")), 3)
		);	
		
		TypedQuery<Department> query = em.createQuery(criteriaQuery);
		List<Department> resultList = query.getResultList();
		for (Department result : resultList) {
			System.out.println("---> " + result);
		}
		
		em.close();
	}
	
	private static void dataInsert(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		// 부서 정보 등록
		Department devDept = new Department();
		devDept.setName("개발부");
		em.persist(devDept);
		
		Department salesDept = new Department();
		salesDept.setName("영업부");
		em.persist(salesDept);
		
		// 직원 정보 등록
		for (int i=1; i<=3; i++) {
			Employee employee = new Employee();
			employee.setName("개발맨 " + i);
			employee.setMailId("Corona" + i);
//			employee.setDeptName("개발부");
			employee.setDept(devDept);
			employee.setSalary(12700.00 * i);
			employee.setStartDate(new Date());
			employee.setTitle("사원");
			employee.setCommissionPct(10.00);
			em.persist(employee);
		}
		
		for (int i=1; i<=3; i++) {
			Employee employee = new Employee();
			employee.setName("영업맨 " + i);
			employee.setMailId("Virus" + i);
//			employee.setDeptName("영업부");
			employee.setDept(salesDept);
			employee.setSalary(23800.00 * i);
			employee.setStartDate(new Date());
			employee.setTitle("과장");
			employee.setCommissionPct(15.00);
			em.persist(employee);			
		}
		
		// 부서 정보가 없는 직원 등록
		Employee employee = new Employee();
		employee.setName("아르바이트");
		employee.setMailId("Alba-01");
		employee.setSalary(10000.00);
		em.persist(employee);		
		
		em.getTransaction().commit();
		em.close();
	}
//	private static void dataInsert(EntityManagerFactory emf) {
//		EntityManager em = emf.createEntityManager();
//		em.getTransaction().begin();
//		
//		// 직원 정보 등록
//		for (int i=1; i<=3; i++) {
//			Employee employee = new Employee();
//			employee.setName("개발맨 " + i);
//			employee.setMailId("Corona" + i);
//			employee.setDeptName("개발부");
//			employee.setSalary(12700.00 * i);
//			employee.setStartDate(new Date());
//			employee.setTitle("사원");
//			employee.setCommissionPct(10.00);
//			em.persist(employee);
//		}
//		
//		for (int i=1; i<=3; i++) {
//			Employee employee = new Employee();
//			employee.setName("영업맨 " + i);
//			employee.setMailId("Virus" + i);
//			employee.setDeptName("영업부");
//			employee.setSalary(23800.00 * i);
//			employee.setStartDate(new Date());
//			employee.setTitle("과장");
//			employee.setCommissionPct(15.00);
//			em.persist(employee);			
//		}
//		
//		// 부서 정보가 없는 직원 등록
//		Employee employee = new Employee();
//		employee.setName("아르바이트");
//		employee.setMailId("Alba-01");
//		employee.setSalary(10000.00);
//		em.persist(employee);
//		
//		em.getTransaction().commit();
//		em.close();
//	}

}
