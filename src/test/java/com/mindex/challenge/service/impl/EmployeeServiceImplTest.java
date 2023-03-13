package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.intThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;

    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private CompensationRepository compensationRepository;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    


    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = employeeIdUrl + "/reporting-structure";
        compensationUrl = employeeIdUrl + "/compensation";
    }

    @Test
    public void testCreateReadUpdateEmployee() {
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");

        // Create checks
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        assertNotNull(createdEmployee.getEmployeeId());
        assertEmployeeEquivalence(testEmployee, createdEmployee);


        // Read checks
        Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class, createdEmployee.getEmployeeId()).getBody();
        assertEquals(createdEmployee.getEmployeeId(), readEmployee.getEmployeeId());
        assertEmployeeEquivalence(createdEmployee, readEmployee);


        // Update checks
        readEmployee.setPosition("Development Manager");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Employee updatedEmployee =
                restTemplate.exchange(employeeIdUrl,
                        HttpMethod.PUT,
                        new HttpEntity<Employee>(readEmployee, headers),
                        Employee.class,
                        readEmployee.getEmployeeId()).getBody();

        assertEmployeeEquivalence(readEmployee, updatedEmployee);
    }

    private static void assertEmployeeEquivalence(Employee expected, Employee actual) {
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getDepartment(), actual.getDepartment());
        assertEquals(expected.getPosition(), actual.getPosition());
    }
    
    @Test
    public void testCreateAndReadCompensation() { 
    	// Arrange
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");
        testEmployee.setFirstName("George");
        
        Compensation testCompensation = new Compensation();
        BigDecimal testSalary = new BigDecimal("500000.00");
        testCompensation.setSalary(testSalary);
        testCompensation.setEffectiveDate(LocalDate.now());
        testCompensation.setEmployee(testEmployee);
        
        // Execute
        HttpStatus createStatus = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class, testEmployee.getEmployeeId()).getStatusCode();
        Compensation compensationResult = restTemplate.getForEntity(compensationUrl, Compensation.class, testEmployee.getEmployeeId()).getBody();
        
        // Assert
        assertEquals(HttpStatus.CREATED, createStatus);
        assertEquals(testCompensation.getSalary(), compensationResult.getSalary());
        assertEquals(testCompensation.getEffectiveDate(), compensationResult.getEffectiveDate());
        assertEquals(testCompensation.getEmployee().getEmployeeId(), compensationResult.getEmployee().getEmployeeId());
        assertEquals(testCompensation.getEmployee().getFirstName(), compensationResult.getEmployee().getFirstName());
    }
    
    @Test
    public void testReadReportingStructureReturnsAllReports() { 
    	// Arrange
    	String testEmployeeId = "16a596ae-edd3-4847-99fe-c4518e82c86f";
    	
    	Employee testEmployee = new Employee();
    	testEmployee.setEmployeeId(testEmployeeId);
    	testEmployee.setFirstName("John");
    			
    	ReportingStructure testReportingStructure = new ReportingStructure();
    	testReportingStructure.setEmployee(testEmployee);
    	testReportingStructure.setNumberOfReports(4);
        
        // Execute - Read ReportingStructure for given employee
        ReportingStructure readReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployeeId).getBody();

        // Assert
        assertEquals(testReportingStructure.getEmployee().getFirstName(), readReportingStructure.getEmployee().getFirstName());
        assertEquals(testReportingStructure.getNumberOfReports(), readReportingStructure.getNumberOfReports());
        assertEquals("Ringo", readReportingStructure.getEmployee().getDirectReports().get(1).getFirstName());
        assertEquals("Pete", readReportingStructure.getEmployee().getDirectReports().get(1).getDirectReports().get(0).getFirstName());
        assertEquals("George", readReportingStructure.getEmployee().getDirectReports().get(1).getDirectReports().get(1).getFirstName());
    }
    
    @Test
    public void testReadReportingStructureReturnsNoReports() { 
        // Arrange
    	String testEmployeeId = "b7839309-3348-463b-a7e3-5de1c168beb3";
        int expectedReports = 0;

        // Execute
        ReportingStructure readReportingStructure = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployeeId).getBody();

        // Assert
        assertEquals(expectedReports, readReportingStructure.getNumberOfReports());
    }
    
    @Test
    public void testEmployeeServiceReadsExistingEmployee() { 
    	// Create
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("c0c2293d-16bd-4603-8e08-777777777777");
        testEmployee.setFirstName("Jerome");
        testEmployee.setLastName("Powell");
        testEmployee.setDepartment("Federal Reserve");
        testEmployee.setPosition("Chairman");
    	employeeRepository.save(testEmployee);
    	
    	// Execute
    	Employee resultEmployee = employeeService.read(testEmployee.getEmployeeId());
    	
    	// Assert
    	assertEquals(testEmployee.getEmployeeId(), resultEmployee.getEmployeeId());
    	assertEquals(testEmployee.getFirstName(), resultEmployee.getFirstName());
    	assertEquals(testEmployee.getLastName(), resultEmployee.getLastName());
    	assertEquals(testEmployee.getDepartment(), resultEmployee.getDepartment());
    	assertEquals(testEmployee.getPosition(), resultEmployee.getPosition());
    }
    
    @Test
    public void testEmployeeServiceReadsExistingDirectReports() { 
    	// Create
    	Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("c0c2293d-16bd-7777-777-777777777777");
        testEmployee.setFirstName("Jack");
        testEmployee.setLastName("Black");
        
        Employee testReportEmployee1 = new Employee();
        testReportEmployee1.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        Employee testReportEmployee2 = new Employee();
        testReportEmployee2.setEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");
        List<Employee> directReports = new ArrayList<Employee>() {
        	{
            	add(testReportEmployee1);
            	add(testReportEmployee2);
        	}
        };
        testEmployee.setDirectReports(directReports);
    	employeeRepository.save(testEmployee);	
    	
    	// Execute
    	ReportingStructure resultReportingStructure = employeeService.readReports(testEmployee);
    	
    	// Assert
    	assertEquals(testEmployee.getEmployeeId(), resultReportingStructure.getEmployee().getEmployeeId());
    	assertEquals(testEmployee.getFirstName(), resultReportingStructure.getEmployee().getFirstName());
    	assertEquals(testEmployee.getLastName(), resultReportingStructure.getEmployee().getLastName());
    	assertEquals(2, resultReportingStructure.getNumberOfReports());
    }
    
    @Test
    public void testEmployeeServiceReadsExistingCompensatation() { 
    	// Create
    	Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("c0c2293d-16bd-4603-777-777777777777");
        testEmployee.setFirstName("Cornelius");
        testEmployee.setLastName("Fudge");
        testEmployee.setDepartment("Mysteries");
        testEmployee.setPosition("Minister");
    	employeeRepository.save(testEmployee);
    	
        Compensation testCompensation = new Compensation();
        testCompensation.setCompensationId("c0c2293d-16bd-4603-8e08-8888888888");
        testCompensation.setSalary(new BigDecimal("200000"));
        testCompensation.setEffectiveDate(LocalDate.now());
        testCompensation.setEmployee(testEmployee);
    	compensationRepository.save(testCompensation);
    	
    	// Execute
    	Compensation resultCompensation = employeeService.readCompensation(testEmployee.getEmployeeId()); 
    	
    	// Assert
    	assertEquals(testEmployee.getEmployeeId(), resultCompensation.getEmployee().getEmployeeId());
    	assertEquals(testEmployee.getFirstName(), resultCompensation.getEmployee().getFirstName());
    	assertEquals(testEmployee.getLastName(), resultCompensation.getEmployee().getLastName());
    	assertEquals(testEmployee.getDepartment(), resultCompensation.getEmployee().getDepartment());
    	assertEquals(testEmployee.getPosition(), resultCompensation.getEmployee().getPosition());
    	assertEquals(testCompensation.getSalary(), resultCompensation.getSalary());
    	assertEquals(testCompensation.getEffectiveDate(), resultCompensation.getEffectiveDate());
    }
}
