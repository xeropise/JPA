package com.rubypaper.biz.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rubypaper.biz.domain.Employee;
import com.rubypaper.biz.repository.EmployeeRepository;

@Service("empService")
@Transactional
public class EmployeeService {

	@Autowired
	private EmployeeRepository empRepository;
	
	public void insertEmployee(Employee employee) {
		empRepository.save(employee);
	}
	
	public void updateEmployee(Employee employee) {
		empRepository.save(employee);
	}
	
	public void deleteEmployee(Employee employee) {
		empRepository.delete(employee);
	}
	
	public Employee getEmployee(Employee employee) {
		return empRepository.findById(employee.getId()).get();
	}
	
//	public List<Employee> getEmployeeList(Employee employee) {
//		return (List<Employee>) empRepository.findAll();
//	}
	
//	public List<Employee> getEmployeeList(Employee employee) {
//		return (List<Employee>) empRepository.findByName(employee.getName());
//	}
	
//	public List<Employee> getEmployeeList(Employee employee) {
//		return (List<Employee>) empRepository.findByMailIdContainingOrderByNameDesc(employee.getMailId());
//	}
	
//	public List<Employee> getEmployeeList(Employee employee, int pageNumber) {
//		Pageable paging = PageRequest.of(pageNumber - 1, 3);
//		
//		return (List<Employee>)empRepository.findByNameContaining(employee.getName(), paging);					
//	}
	
//	public Page<Employee> getEmployeeList(Employee employee, int pageNumber) {
//		Pageable paging = 
//					//PageRequest.of(pageNumber - 1, 3, Sort.Direction.DESC, "id");
//					PageRequest.of(pageNumber - 1, 3, 
//							Sort.by(new Order(Direction.DESC, "mailId"), new Order(Direction.ASC, "salary"))
//					);
//		
//		return empRepository.findByNameContaining(employee.getName(), paging);
//	}
	
	public List<Object[]> getEmployeeList(Employee employee) {
		//return empRepository.findByJPQL(employee.getName());
		//return empRepository.findByNativeQuery(employee.getName());
		
		Pageable paging = PageRequest.of(0,  3, Sort.Direction.DESC, "id");
		return empRepository.findByJPQL(employee.getName(), paging);
	}
}
