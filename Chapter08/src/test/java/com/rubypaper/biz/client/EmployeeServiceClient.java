package com.rubypaper.biz.client;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.rubypaper.biz.config.SpringConfiguration;
import com.rubypaper.biz.domain.Department;
import com.rubypaper.biz.domain.Employee;
import com.rubypaper.biz.service.DepartmentService;
import com.rubypaper.biz.service.EmployeeService;

public class EmployeeServiceClient {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext container = new AnnotationConfigApplicationContext(SpringConfiguration.class);
		
		DepartmentService deptService = (DepartmentService) container.getBean("deptService");
		EmployeeService employeeService = (EmployeeService) container.getBean("empService");
		
		dataInsert(deptService, employeeService);
		//dataSelect(employeeService);
		dataSelect(deptService);
		container.close();
	}
	
	private static void dataSelect(DepartmentService deptService) {
		Department department = new Department();
		department.setDeptId(1L);
		Department findDept = deptService.getDepartment(department);
		
		System.out.println("부서명 : " + findDept.getName());
		System.out.println("직원 목록");
		for (Employee employee : findDept.getEmployeeList()) {
			System.out.println("---> " + employee);
		}
	}
	
//	private static void dataSelect(EmployeeService employeeService) {
//		List<Employee> employeeList = employeeService.getEmployeeList(new Employee());
//		
//		System.out.println("직원 목록");
//		for (Employee employee : employeeList) {
//			System.out.println("---> " + employee.getName() + "의 부서명 : " + employee.getDept().getName());
//		}
//	}
	
	private static void dataInsert(DepartmentService deptService, EmployeeService employeeService) {
		Department department1 = new Department();
		department1.setName("개발부");
		deptService.insertDepartment(department1);
		
		Department department2 = new Department();
		department2.setName("영업부");
		deptService.insertDepartment(department2);
		
		for (int i=1; i<=5; i++) {
			Employee employee = new Employee();
			employee.setName("개발직원 " + i);
			employee.setSalary(i * 12700.00);
			employee.setMailId("Dev-" + i);
			employee.setDept(department1);
			employeeService.insertEmployee(employee);
		}
		
		for (int i=1; i<=7; i++) {
			Employee employee = new Employee();
			employee.setName("영업직원  " + i);
			employee.setSalary(i * 24300.00);
			employee.setMailId("Sales-" + i);
			employee.setDept(department2);
			employeeService.insertEmployee(employee);
		}
	}
}
