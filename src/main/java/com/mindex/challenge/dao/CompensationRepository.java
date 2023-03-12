package com.mindex.challenge.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;

public interface CompensationRepository extends MongoRepository<Compensation, String> {
	
	Compensation findByEmployee_EmployeeId(String employeeId);
}
