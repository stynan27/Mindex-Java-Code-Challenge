package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.intThat;

import java.math.BigDecimal;
import java.time.LocalDate;

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
    public void testCreateReadUpdate() {
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
    public void testCreateEmployeeReturnsCreated() {
    	// Arrange
        Employee testEmployee = new Employee();
        testEmployee.setFirstName("Doe");
        testEmployee.setLastName("John");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        
        // Execute
        HttpStatus createStatus = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getStatusCode();

        // Assert
        assertEquals(HttpStatus.CREATED, createStatus);
    }
    
    @Test
    public void testCreateCompensationReturnsCreated() {
    	// Arrange
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        testEmployee.setFirstName("Pete");
        testEmployee.setLastName("Best");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer II");
        
        Compensation testCompensation = new Compensation();
        BigDecimal testSalary = new BigDecimal("200000.00");
        testCompensation.setSalary(testSalary);
        testCompensation.setEffectiveDate(LocalDate.now());
        testCompensation.setEmployee(testEmployee);
        
        // Execute
        HttpStatus createStatus = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class, testEmployee.getEmployeeId()).getStatusCode();

        // Assert
        assertEquals(HttpStatus.CREATED, createStatus);
    }
    
    @Test
    public void testCreateCompensationReturnsNotFound() {
    	// Arrange
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("Bad_Id");
        
        Compensation testCompensation = new Compensation();
        BigDecimal testSalary = new BigDecimal("200000.00");
        testCompensation.setSalary(testSalary);
        testCompensation.setEffectiveDate(LocalDate.now());
        testCompensation.setEmployee(testEmployee);
        
        // Execute
        HttpStatus createStatus = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class, testEmployee.getEmployeeId()).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, createStatus);
    }
    
    @Test
    public void testCreateCompensationReturnsConflict() {
    	// Arrange
        Employee testEmployee = new Employee();
        testEmployee.setEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        
        Compensation testCompensation = new Compensation();
        BigDecimal testSalary = new BigDecimal("200000.00");
        testCompensation.setSalary(testSalary);
        testCompensation.setEffectiveDate(LocalDate.now());
        testCompensation.setEmployee(testEmployee);
        
        // Execute
        HttpStatus createStatus = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class, testEmployee.getEmployeeId()).getStatusCode();
        HttpStatus createStatus2 = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class, testEmployee.getEmployeeId()).getStatusCode();
        
        // Assert
        assertEquals(HttpStatus.CREATED, createStatus);
        assertEquals(HttpStatus.CONFLICT, createStatus2);
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
    public void testReadEmployeeReturnsNotFound() { 
        // Arrange
    	String testEmployeeId = "Bad id";

        // Execute
        HttpStatus readStatus = restTemplate.getForEntity(employeeIdUrl, Employee.class, testEmployeeId).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, readStatus);
    }
    
    @Test
    public void testUpdateEmployeeReturnsNotFound()
    {
        // Arrange
    	String testEmployeeId = "Bad id";
    	Employee testEmployee = new Employee();
    	
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Execute
        HttpStatus readStatus = restTemplate.exchange(employeeIdUrl, 
        		HttpMethod.PUT,
        		new HttpEntity<Employee>(testEmployee, headers),
        		Employee.class, 
        		testEmployeeId).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, readStatus);
    }
    
    @Test
    public void testReadReportingStructureReturnsNotFound() { 
        // Arrange
    	String testEmployeeId = "Bad id";

        // Execute
        HttpStatus readStatus = restTemplate.getForEntity(reportingStructureUrl, ReportingStructure.class, testEmployeeId).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, readStatus);
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
    public void testReadCompensationReturnsNotFoundOnBadEmployeeId() { 
        // Arrange
    	String testEmployeeId = "Bad id";

        // Execute
        HttpStatus readStatus = restTemplate.getForEntity(compensationUrl, Compensation.class, testEmployeeId).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, readStatus);
    }
    
    @Test
    public void testReadCompensationReturnsNotFoundOnNoCompensation() { 
        // Arrange
    	String testEmployeeId = "03aa1462-ffa9-4978-901b-7c001562cf6f";

        // Execute
        HttpStatus readStatus = restTemplate.getForEntity(compensationUrl, Compensation.class, testEmployeeId).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, readStatus);
    }
    
}
