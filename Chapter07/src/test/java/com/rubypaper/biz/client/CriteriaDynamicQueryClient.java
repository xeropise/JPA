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
import javax.persistence.criteria.Subquery;

import com.rubypaper.biz.domain.Department;
import com.rubypaper.biz.domain.Employee;

public class CriteriaDynamicQueryClient {

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
		
//	private static void dataSelect(EntityManagerFactory emf) {
//		EntityManager em = emf.createEntityManager();
//		
//		// 검색 정보 설정
//		String searchCondition = "NAME";
//		String searchKeyword = "아르바이트";
//		
//		// 검색 관련 쿼리
//		String jpqlByMailId = "SELECT e FROM Employee e "
//							 +"WHERE e.mailId= :searchKeyword";
//		String jpqlByName   = "SELECT e FROM Employee e "
//							 +"WHERE e.name= :searchKeyword";
//		String jpqlByTitle  = "SELECT e FROM Employee e "
//							 +"WHERE e.title= :searchKeyword";
//		
//		TypedQuery<Employee> query = null;
//		
//		// 검색 조건에 따른 분기 처리
//		if("NAME".equals(searchCondition)) {
//			query = em.createQuery(jpqlByName, Employee.class);
//		} else if ("MAILID".equals(searchCondition)) {
//			query = em.createQuery(jpqlByMailId, Employee.class);
//		} else if ("TITLE".equals(searchCondition)) {
//			query = em.createQuery(jpqlByTitle, Employee.class);
//		}
//		
//		query.setParameter("searchKeyword", searchKeyword);
//		List<Employee> resultList = query.getResultList();
//		
//		System.out.println(searchCondition + "을 기준으로한 검색 결과 ");
//		for (Employee result : resultList) {
//			System.out.println("---> " + result);
//		}
//		
//		em.close();
//	}
	
//	private static void dataSelect(EntityManagerFactory emf) {
//		EntityManager em = emf.createEntityManager();
//		
//		// 검색 정보 설정
//		String searchCondition = "TITLE";
//		String searchKeyword = "과장";
//		
//		// 크라이테리어 빌더 생덩
//		CriteriaBuilder builder = em.getCriteriaBuilder();
//		
//		// 크라이테리어 커ㅜ리 생성
//		CriteriaQuery<Employee> criteriaQuery =
//					builder.createQuery(Employee.class);
//		
//		// FROM Employee emp
//		Root<Employee> emp = criteriaQuery.from(Employee.class);
//		
//		// SELECT emp
//		criteriaQuery.select(emp);
//		
//		// 검색 조건에 따른 분기 처리
//		if("NAME".contentEquals(searchCondition)) {
//			criteriaQuery.where(
//					builder.equal(emp.get("name"), searchKeyword));
//		} else if ("MAILID".contentEquals(searchCondition)) {
//			criteriaQuery.where(
//					builder.equal(emp.get("mailId"), searchKeyword));
//		}
//		
//		TypedQuery<Employee> query = em.createQuery(criteriaQuery);
//		List<Employee> resultList = query.getResultList();
//		for (Employee result : resultList) {
//			System.out.println("---> " + result);
//		}
//		
//		em.close();
//	}
	
	private static void dataSelect(EntityManagerFactory emf) {
		EntityManager em = emf.createEntityManager();
		
		// 크라이테리어 빌더 생성
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Employee> criteriaQuery = 
				builder.createQuery(Employee.class);
		
		/** 서브쿼리 생성 */
		Subquery<Double> subquery = criteriaQuery.subquery(Double.class);
		
		// FROM Employee e
		Root<Employee> e = subquery.from(Employee.class);
		
		// SELECT AVG(e.salary)
		subquery.select(builder.avg(e.<Double>get("salary")));
		
		/** 메인쿼리 생성 */
		// FROM Employee emp
		Root<Employee> emp = criteriaQuery.from(Employee.class);
		
		// SELECT emp
		criteriaQuery.select(emp);
		
		// JOIN FETCH emp.dept dept
		emp.fetch("dept");
		
		/** 메인쿼리에 서브쿼리 연결하기 */
		// WHERE salary >= (서브쿼리)
		criteriaQuery.where(builder.ge(emp.<Double>get("salary"), subquery));
		
		TypedQuery<Employee> query = em.createQuery(criteriaQuery);
		List<Employee> resultList = query.getResultList();
		
		for(Employee result : resultList) {
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
