package com.rubypaper.biz.client;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

import com.rubypaper.biz.domain.Department;
import com.rubypaper.biz.domain.Employee;

public class JPQLNamedQueryClient {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("Chapter06");
		
		try {
			dataInsert(emf);
			//dataSelect(emf);
			dataUpdate(emf);
			//dataDelete(emf);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			emf.close();
		}
	}
	
	private static void dataDelete(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		Query query = em.createQuery("DELETE Employee e WHERE e.name= :empName");
		query.setParameter("empName", "아르바이트");
		int updateCount = query.executeUpdate();
		System.out.println(updateCount + "건의 데이터 갱신됨");
		
		em.getTransaction().commit();
		em.close();
	}
	
	private static void dataUpdate(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		// 엔티티를 조회하여 캐시에 등록
//		Employee findEmp = em.find(Employee.class, 3L);
//		System.out.println("수정 전 급여 : " + findEmp.getSalary());
		
		/*
		 * JPQL 을 이용하여 데이터를 수정, 삭제하는 경우에는 삭제할 데이터를 먼저 검색하지 말고 수정이나 삭제를 먼저 처리한 후에 검색하는 것을 원칙으로 하자.
		 * JPQL 을 이용한 데이터 수정, 삭제는 캐시에 영향을 주지 않는다.
		 */
		
		Query query = em.createQuery("UPDATE Employee e "
				
								    + "SET e.salary=salary * 1.3 "
								    + "WHERE e.id=:empid");
		query.setParameter("empid", 3L);
		int updateCount = query.executeUpdate();
		
		String jpql = "SELECT e FROM Employee e WHERE e.id = 3L";
		query = em.createQuery(jpql);
		Employee employee = (Employee) query.getSingleResult();
		System.out.println(employee.getId() + " 번 직원의 수정된 급여 : " + employee.getSalary());
		em.getTransaction().commit();
		em.close();
	}
	
	private static void dataSelect(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		
//		TypedQuery<Employee> query = em.createNamedQuery("Employee.searchByName", Employee.class);
//		
//		query.setParameter("searchKeyword", "%영업%");
		
		
		Query query = em.createNamedQuery("Employee.searchByDeptId");
		query.setParameter("deptId", 2L);
		query.setFirstResult(0);
		query.setMaxResults(3);
		
		List<Object[]> resultList = query.getResultList();
		for (Object[] result : resultList) {
			System.out.println("---> "+ Arrays.deepToString(result));
		}
		
		em.close();
	}
	
	private static void dataInsert(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		
		Department department1 = new Department();
		department1.setName("개발부");
		for (int i=1; i<=3; i++) {
			Employee employee = new Employee();
			employee.setName("개발 직원 " + i);
			employee.setSalary(i * 12700.00);
			employee.setMailId("Dev-" + i);
			employee.setDept(department1);
		}
		em.persist(department1);
		
		Department department2 = new Department();
		department2.setName("영업부");
		for (int i=1; i<=3; i++) {
			Employee employee =new Employee();
			employee.setName("영업직원 " + i);
			employee.setSalary(27300.00 * i);
			employee.setMailId("Sale-" + i);
			employee.setDept(department2);
		}
		em.persist(department2);
		
		Department department3 = new Department();
		department3.setName("인재개발부");
		em.persist(department3);
		
		// 부서 정보가 없는 새로운 직원 추가
		Employee employee = new Employee();
		employee.setName("아르바이트");
		employee.setMailId("Alba-01");
		employee.setSalary(10000.00);
		em.persist(employee);
		
		// 이름이 영업부인 새로운 직원 추가
		Employee employee2 = new Employee();
		employee2.setName("영업부");
		em.persist(employee2);
		
		em.getTransaction().commit();
		em.close();		
	}
	
	
}
