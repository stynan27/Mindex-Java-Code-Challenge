package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CompensationRepository compensationRepository;

    @Override
    public Employee create(Employee employee) {
        LOG.debug("Creating employee [{}]", employee);

        employee.setEmployeeId(UUID.randomUUID().toString());
        employeeRepository.insert(employee);

        return employee;
    }
    
    @Override
    public Compensation createCompensation(Compensation compensation) {
        compensation.setCompensationId(UUID.randomUUID().toString());
        
        // Check if Compensation already exists by employeeID
        // Prevents multiple Compensation entities per employee
        String employeeId = compensation.getEmployee().getEmployeeId();
        Compensation existingCompensation = compensationRepository.findByEmployee_EmployeeId(employeeId);
        
        if (existingCompensation != null) {
        	// Respond with proper entity Conflict 409
            throw new ResponseStatusException(
            	HttpStatus.CONFLICT, "Compensation already exists for employeeId: " + employeeId
            );
        }
     
        LOG.debug("Creating compensation [{}] with employeeId [{}]", compensation, compensation.getEmployee().getEmployeeId());
        compensationRepository.insert(compensation);

        return compensation;
    }

    @Override
    public Employee read(String id) {
        LOG.debug("Get employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
        	// Respond with proper entity Not Found 404
            //throw new RuntimeException("Invalid employeeId: " + id);
            throw new ResponseStatusException(
            	HttpStatus.NOT_FOUND, "Invalid employeeId: " + id
            );
        }

        return employee;
    }
    
    @Override
    public ReportingStructure readReports(Employee employee) {
        LOG.debug("Get ReportStructure with employee id [{}]", employee.getEmployeeId());
        
        ReportingStructure reportingStructure = new ReportingStructure(employee, computeTotalReports(employee));

        return reportingStructure;
    }
    
    @Override
    public Compensation readCompensation(String id) {
        LOG.debug("Get compensation with id [{}]", id);

        Compensation compensation = compensationRepository.findByEmployee_EmployeeId(id);

        if (compensation == null) {
        	// Respond with proper entity Not Found 404
            throw new ResponseStatusException(
            	HttpStatus.NOT_FOUND, "No compensation found for employeeId: " + id
            );
        }

        return compensation;
    }

    @Override
    public Employee update(Employee employee) {
        LOG.debug("Updating employee [{}]", employee);

        return employeeRepository.save(employee);
    }
    
	// Helper method to compute numberOfReports
	// Breadth First Search of Employee Hierarchy
	private int computeTotalReports(Employee employee) {
		// Queue of employees (reports) to check - until all have been visited!
		// instantiate ArrayDeque as a queue - faster than stack and LinkedList when used as a queue
		// source: https://docs.oracle.com/javase/8/docs/api/java/util/ArrayDeque.html
		Queue<Employee> employeeQueue = new ArrayDeque<Employee>();
		// initialize with first employee to check
		employeeQueue.add(employee);
        // HashMap of all reporting employees found
		HashMap<String, Employee> allReports = new HashMap<String, Employee>();
        
        // employee to check
        Employee currentEmployee = null;
		
        // continue to find reports while queue is not empty
        while(employeeQueue.size() != 0)
        {
        	// returns and removes the head of the queue or null if empty
            currentEmployee = employeeQueue.poll();
            if (currentEmployee.getDirectReports() != null) 
            {
                for(Employee reportEmployee : currentEmployee.getDirectReports())
                {
                	if (!(allReports.containsKey(reportEmployee.getEmployeeId())))
                    {
                        employeeQueue.add(reportEmployee);
                        LOG.debug("Employee added... {} {}", reportEmployee.getFirstName(), reportEmployee.getLastName());
                        allReports.put(reportEmployee.getEmployeeId(), reportEmployee);
                    }
                }
            }
        }

        // return total number found
        return allReports.size();
	}
}
