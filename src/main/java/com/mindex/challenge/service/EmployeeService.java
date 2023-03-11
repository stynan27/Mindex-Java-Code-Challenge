package com.mindex.challenge.service;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;

public interface EmployeeService {
    Employee create(Employee employee);
    Compensation createCompensation(Compensation compensation);
    Employee read(String id);
    ReportingStructure readReports(Employee employee);
    Employee update(Employee employee);
}
