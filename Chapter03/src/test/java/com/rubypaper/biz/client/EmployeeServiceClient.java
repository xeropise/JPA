package com.rubypaper.biz.client;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import com.rubypaper.biz.domain.Employee;

public class EmployeeServiceClient {

	public static void main(String[] args) {
		EntityManagerFactory emf =
				Persistence.createEntityManagerFactory("Chapter03");
		
		EntityManager em = emf.createEntityManager();
		
		// 엔티티 트랜잭션 생성
		EntityTransaction tx = em.getTransaction();
		try {
			// 직원 등록
			tx.begin();
			for (int i=2; i<=10; i++) {
				Employee employee = new Employee();
				employee.setName("직원-" + i);
				em.persist(employee);
			}
			tx.commit();
			
			// 직원 목록 조회
			String jpql = "SELECT e FROM Employee e ORDER BY e.id DESC";
			
			List<Employee> employeeList =
					em.createQuery(jpql, Employee.class).getResultList();
			
			for(Employee employee : employeeList) {
				System.out.println("---> "+ employee);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
			// 트랜잭션 종료(ROLLBACK)
			tx.rollback();
		} finally {
			em.close();
			emf.close();
		}
	}
}
