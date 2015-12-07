package org.example.integrations;

import org.example.domain.EmployeeRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DynamoDBService {

    public DynamoDBService() {
        //TODO initialize DynamoDB client
    }

    public String createRequestEntry(final EmployeeRequest request) {
        //TODO
        return "request_id";
    }

    public List<EmployeeRequest> getAllRequests() {
        //TODO
        return new ArrayList<>();
    }

    public EmployeeRequest getRequestById(final String id) {
        //TODO
        return null;
    }

    public void updateRequest(final String id, final EmployeeRequest request) {
        //TODO
    }

    public void setStatus(final String id, final String status) {
        //TODO
    }

    ;
}
