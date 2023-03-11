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
    
    @PostMapping("/employee/{id}/compensation")
    public ResponseEntity<Compensation> create(@RequestBody Compensation compensation) {
        LOG.debug("Received compensation create request for employee [{} {}]", 
        		compensation.getEmployee().getFirstName(),
        		compensation.getEmployee().getLastName());
        
        // Catch/throw invalid employee handled here.
        employeeService.read(compensation.getEmployee().getEmployeeId());

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

    @PutMapping("/employee/{id}")
    public Employee update(@PathVariable String id, @RequestBody Employee employee) {
        LOG.debug("Received employee update request for id [{}] and employee [{}]", id, employee);

        // Catch/throw invalid employee handled here.
        employeeService.read(id);
        
        employee.setEmployeeId(id);
        return employeeService.update(employee);
    }
}
