package com.mindex.challenge.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.intThat;

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

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.EmployeeService;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EmployeeServiceImplTest {

    private String employeeUrl;
    private String employeeIdUrl;
    private String reportingStructureUrl;

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
    public void testReadEmployeeNotFound() { 
        // Arrange
    	String testEmployeeId = "Bad id";

        // Execute
        HttpStatus readStatus = restTemplate.getForEntity(employeeIdUrl, Employee.class, testEmployeeId).getStatusCode();

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, readStatus);
    }
    
    @Test
    public void testUpdateEmployeeNotFound()
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
    public void testReadReportingStructureNotFound() { 
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

    // TODO: Compensation Tests
}
