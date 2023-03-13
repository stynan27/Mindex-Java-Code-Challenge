package com.mindex.challenge;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.assertEquals;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBootstrapTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testJohnLennonBootstrap() {
        Employee employee = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");
        assertNotNull(employee);
        assertEquals("John", employee.getFirstName());
        assertEquals("Lennon", employee.getLastName());
        assertEquals("Development Manager", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }
    
    @Test
    public void testPaulMcCartneyBootstrap() {
        Employee employee = employeeRepository.findByEmployeeId("b7839309-3348-463b-a7e3-5de1c168beb3");
        assertNotNull(employee);
        assertEquals("Paul", employee.getFirstName());
        assertEquals("McCartney", employee.getLastName());
        assertEquals("Developer I", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }
    
    @Test
    public void testRingoStarrBootstrap() {
        Employee employee = employeeRepository.findByEmployeeId("03aa1462-ffa9-4978-901b-7c001562cf6f");
        assertNotNull(employee);
        assertEquals("Ringo", employee.getFirstName());
        assertEquals("Starr", employee.getLastName());
        assertEquals("Developer V", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }
    
    @Test
    public void testPeteBestBootstrap() {
        Employee employee = employeeRepository.findByEmployeeId("62c1084e-6e34-4630-93fd-9153afb65309");
        assertNotNull(employee);
        assertEquals("Pete", employee.getFirstName());
        assertEquals("Best", employee.getLastName());
        assertEquals("Developer II", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }
    
    @Test
    public void testGeorgeHarrisonBootstrap() {
        Employee employee = employeeRepository.findByEmployeeId("c0c2293d-16bd-4603-8e08-638a9d18b22c");
        assertNotNull(employee);
        assertEquals("George", employee.getFirstName());
        assertEquals("Harrison", employee.getLastName());
        assertEquals("Developer III", employee.getPosition());
        assertEquals("Engineering", employee.getDepartment());
    }
}