package org.example;

import org.example.domain.EmployeeRequest;
import org.example.integrations.DynamoDBService;
import org.example.integrations.MailService;
import org.example.integrations.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class EmployeeRequestController {

    final static String MANAGER_EMAIL = "manager@mail.com";
    final static String EMPLOYEE_EMAIL = "employee@mail.com";

    @Autowired
    DynamoDBService dynamo;
    @Autowired
    MailService mail;
    @Autowired
    S3Service s3;

    @RequestMapping(path = "/requests/{id}", method = RequestMethod.GET)
    public EmployeeRequest getRequest(@PathVariable String id) {
        return dynamo.getRequestById(id);
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    public List<EmployeeRequest> getRequests() {
        return dynamo.getAllRequests();
    }

    @RequestMapping(path = "/requests", method = RequestMethod.POST)
    public void putRequest(@RequestParam("name") String name, /*add other values as request params*/
                           @RequestParam(value = "file", required = false) MultipartFile file) {
        final EmployeeRequest request = new EmployeeRequest();
        //TODO fill the request object with values


        final String requestId = dynamo.createRequestEntry(request);
        //TODO store the request in the db and store the binary in s3 bucket
        // some help: https://spring.io/guides/gs/uploading-files/
        // s3.storeFile(requestId, ..


        //TODO create a message and send the to the manager
        // mail.sendMail(...
        return;
    }

    @RequestMapping(path = "/requests/{id}/status", method = RequestMethod.PUT)
    public void putStatus(@PathVariable String id, @RequestParam("status") String status) {
        dynamo.setStatus(id, status);
        //TODO create a message and send the to the employee
        // mail.sendMail(...
    }
}
