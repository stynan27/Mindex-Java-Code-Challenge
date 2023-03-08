package com.mindex.challenge.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mindex.challenge.service.impl.EmployeeServiceImpl;

public class ReportingStructure {
	
	private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);
	
	private Employee employee;
	private int numberOfReports;
	
	public ReportingStructure() {
	}
	
	public ReportingStructure(Employee employee, int numberOfReports) {
		super();
		
		LOG.debug("Creating ReportingStructure with employee [{} {}] and numberOfReports [{}]", employee.getFirstName(), employee.getLastName(), numberOfReports);
		
		this.employee = employee;
		this.numberOfReports = numberOfReports;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public int getNumberOfReports() {
		return numberOfReports;
	}

	public void setNumberOfReports(int numberOfReports) {
		this.numberOfReports = numberOfReports;
	}

	@Override
	public String toString() {
		return "ReportingStructure [employee=" + employee + ", numberOfReports=" + numberOfReports + "]";
	}
}
