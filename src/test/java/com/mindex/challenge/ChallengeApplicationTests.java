package com.mindex.challenge;

import static org.junit.Assert.assertEquals;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeApplicationTests {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;
    private String compensationUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
        reportingStructureUrl = employeeIdUrl + "/reporting-structure";
        compensationUrl = employeeIdUrl + "/compensation";
    }

	@Test
	public void contextLoads() {
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
