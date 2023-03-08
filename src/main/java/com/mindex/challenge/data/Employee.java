package com.mindex.challenge.data;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Employee")
public class Employee {
	@Id
    private String employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    
    // @DBRef Allows us to "eager" load directReports so we don't need to query separately when creating our ReportStructure
    // minimizes overhead of retrieving each employee separately by reducing number of round trips to database
    // can lead to N+1 query problems (multiple queries executed to retrieve related documents (employees + each employee in directReports))
    // these problems can be mitigated with optimized retrieval strategies (i.e. MongoDB data Aggregations specifying graph lookup and say max data depth)
    @DBRef
    private List<Employee> directReports;

    public Employee() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(List<Employee> directReports) {
        this.directReports = directReports;
    }
}
