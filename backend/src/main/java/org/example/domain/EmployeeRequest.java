package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {
    private String id;
    private String name;
    private String timestamp;
    private String when;
    private String why;
    private String where;
    private int amount;
}
