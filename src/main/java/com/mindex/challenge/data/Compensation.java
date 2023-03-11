package com.mindex.challenge.data;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mindex.challenge.service.impl.EmployeeServiceImpl;

@Document(collection = "Compensation")
public class Compensation {

	private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);
	
	@Id
	private String compensationId;
	// BigDecimal supports arbitrary-precision decimal arithmetic 
	// Ideal for finance values like salaries due to the flexibility in precision
	// Prevents loss of precision & large values before the decimal point.
	private BigDecimal salary;
	private LocalDate effectiveDate;
	@DBRef
	private Employee employee;
	
	public Compensation() {
	}

	public Compensation(String compensationId, BigDecimal salary, LocalDate effectiveDate, Employee employee) {
		super();
		this.compensationId = compensationId;
		this.salary = salary;
		this.effectiveDate = effectiveDate;
		this.employee = employee;
	}

	public String getCompensationId() {
		return compensationId;
	}

	public void setCompensationId(String compensationId) {
		this.compensationId = compensationId;
	}

	public BigDecimal getSalary() {
		return salary;
	}

	public void setSalary(BigDecimal salary) {
		this.salary = salary;
	}

	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	@Override
	public String toString() {
		return "Compensation [compensationId=" + compensationId + ", salary=" + salary + ", effectiveDate="
				+ effectiveDate + ", employee=" + employee + "]";
	}
}