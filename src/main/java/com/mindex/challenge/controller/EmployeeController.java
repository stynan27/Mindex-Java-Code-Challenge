package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController {
    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/employee")
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        LOG.debug("Received employee create request for [{}]", employee.getFirstName());

        Employee createdEmployee = employeeService.create(employee);
        return ResponseEntity
                .status(201) // confirm created response
                .body(createdEmployee);
    }
    
    // Rest endpoint to create compensation by existing employeeId
    @PostMapping("/employee/{id}/compensation")
    public ResponseEntity<Compensation> create(@PathVariable String id, @RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employeeId [{}] with salary [{}] and effective date [{}]", 
        		id,
        		compensation.getSalary(),
        		compensation.getEffectiveDate());
        
        // Prevent data corruption by retrieving/setting employee by id
        // Also, catch/throw invalid employee handled here.
        Employee employee = employeeService.read(id);
        compensation.setEmployee(employee);

        Compensation createdCompensation = employeeService.createCompensation(compensation);
        return ResponseEntity
                .status(201) // confirm created response
                .body(createdCompensation);
    }

    @GetMapping("/employee/{id}")
    public Employee read(@PathVariable String id) {
        LOG.debug("Received employee get request for id [{}]", id);
        

        return employeeService.read(id);
    }
    
    // REST endpoint for GETing a dynamically created ReportStructure - Extends Employee endpoint
    @GetMapping("/employee/{id}/reporting-structure")
    public ReportingStructure readReportStructure(@PathVariable String id) {
        LOG.debug("Received report-structure get request for id [{}]", id);
        
        // Catch/throw invalid employee handled here as well...
        Employee employee = employeeService.read(id);
        
        return employeeService.readReports(employee);
    }
    
    // Rest endpoint to retrieve compensation by employeeId
    @GetMapping("/employee/{id}/compensation")
    public Compensation create(@PathVariable String id) {
        LOG.debug("Received GET compensation request for employeeId [{}]", id);
        
        // Catch/throw invalid employee handled here.
        employeeService.read(id);

        return employeeService.readCompensation(id);
    }

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        // Catch/throw invalid employee handled here.
        employeeService.read(id);
        
        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
}
