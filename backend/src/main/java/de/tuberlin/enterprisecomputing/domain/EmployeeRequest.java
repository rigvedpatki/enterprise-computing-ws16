package de.tuberlin.enterprisecomputing.domain;

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
    private String documentLink;
    // Name of the Document
    private String documentName;
    //status of the request
    private String status;
}
