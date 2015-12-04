package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeRequest {

    private String id;
    private String name;
    private String timestamp;
    private String when;
    private String why;
    private String where;
    private int amount;
    
    public EmployeeRequest(String id, String name, String timestamp, String when, String why, String where, int amount) {
    	this.id = id;
    	this.name = name;
    	this.timestamp = timestamp;
    	this.when = when;
    	this.why = why;
    	this.where = where;
    	this.amount = amount;
    }
}
