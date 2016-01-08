package de.tuberlin.enterprisecomputing;

import de.tuberlin.enterprisecomputing.integrations.S3Service;
import de.tuberlin.enterprisecomputing.domain.EmployeeRequest;
import de.tuberlin.enterprisecomputing.integrations.DynamoDBService;
import de.tuberlin.enterprisecomputing.integrations.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Controller
@Slf4j
public class EmployeeRequestController {

    final static String MANAGER_EMAIL = "maxim.tschumak@gmail.com";
    final static String EMPLOYEE_EMAIL = "maxim.tschumak@gmail.com";
    //commented for local testing
    //final static String MANAGER_EMAIL = "ec2015manager@gmail.com";
    //final static String EMPLOYEE_EMAIL = "ec2015employee@gmail.com";

    @Autowired
    DynamoDBService dynamo;
    @Autowired
    MailService mail;
    @Autowired
    S3Service s3;

    @RequestMapping(path = "/requests/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    EmployeeRequest getRequest(@PathVariable String id) {
        return dynamo.getRequestById(id);
    }

    @RequestMapping(path = "/requests/{id}", method = RequestMethod.PUT)
    public void updateRequest(@PathVariable String id, @RequestBody EmployeeRequest request) {
        dynamo.updateRequest(id, request);
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    public
    @ResponseBody
    List<EmployeeRequest> getRequests() {
        return dynamo.getAllRequests();
    }

    @RequestMapping(path = "/requests", method = RequestMethod.POST)
    public ResponseEntity<String> createRequest(@RequestParam("name") String name, @RequestParam("where") String where,
                                                @RequestParam("why") String why, @RequestParam("when") String when,
                                                @RequestParam("amount") int amount,
                                                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        //fill the request object with values
        final EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setName(name);
        employeeRequest.setWhere(where);
        employeeRequest.setWhy(why);
        employeeRequest.setWhen(when);
        employeeRequest.setAmount(amount);

        final boolean fileAttached = file != null && !file.isEmpty();

        // store employee request
        final String requestId = dynamo.createRequestEntry(employeeRequest, fileAttached);

        // store the request in the db and store the binary in s3 bucket
        // some help: https://spring.io/guides/gs/uploading-files/
        if (fileAttached) {
            byte[] bytes = file.getBytes();
            File localFile = new File(requestId);
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(localFile));
            stream.write(bytes);
            stream.close();
            // upload the locally stored file to S3
            s3.storeFile(localFile.getName(), localFile);
        }
        
        //create a message and send to the manager
        mail.sendMail(MANAGER_EMAIL);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/requests/{id}/status", method = RequestMethod.PUT)
    public ResponseEntity<String> putStatus(@PathVariable String id, @RequestParam("status") String status) {
        dynamo.setStatus(id, status);
        //create a message and send to the employee
        mail.sendMail(EMPLOYEE_EMAIL);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
