package de.tuberlin.enterprisecomputing.domain;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {

     private String requestId;
     private String name;
     private String timestamp;
     private String when;
     private String why;
     private String where;
     private int amount;
     //added URL link to the document stored to be displayed in the front end jade table.
     private String document;
     //status of the request
     private String status;

}
