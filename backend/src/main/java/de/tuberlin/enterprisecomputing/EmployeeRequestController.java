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
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Slf4j
public class EmployeeRequestController {

    final static String NO_DOCUMENT = "NO DOCUMENT";
    final static String MANAGER_EMAIL = "ec2015manager@gmail.com";
    final static String EMPLOYEE_EMAIL = "ec2015employee@gmail.com";

    @Autowired
    DynamoDBService dynamo;
    @Autowired
    MailService mail;
    @Autowired
    S3Service s3;

    @RequestMapping(path = "/requests/{requestId}", method = RequestMethod.GET)
    public
    @ResponseBody
    EmployeeRequest getRequest(@PathVariable String requestId) {
        return dynamo.getRequestById(requestId);
    }

    @RequestMapping(path = "/requests/{requestId}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateRequest(@PathVariable String requestId, @RequestParam("where") String where,
                                                @RequestParam("why") String why, @RequestParam("when") String when,
                                                @RequestParam("amount") int amount, @RequestParam(value = "fileName", required = false) String fileName,
                                                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        //filling the updated values
        final EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setWhere(where);
        employeeRequest.setWhy(why);
        employeeRequest.setWhen(when);
        employeeRequest.setAmount(amount);
        //Generating the current timestamp
        Long currentTimeStamp = new Date().getTime();
        employeeRequest.setTimestamp(currentTimeStamp.toString());
        //checking if the file is attached
        final boolean fileAttached = file != null && !file.isEmpty();
        if (fileAttached) {
            //getting the extension of the file
            String[] ext = fileName.split("\\.");
            String documentName = requestId + "." + ext[1];
            employeeRequest.setDocumentName(documentName);
            byte[] bytes = file.getBytes();
            File localFile = new File(employeeRequest.getDocumentName());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(localFile));
            stream.write(bytes);
            stream.close();

            // upload the locally stored file to S3
            s3.storeFile(localFile.getName(), localFile);
        }
        String docLink = s3.generateURL(employeeRequest.getDocumentName(), fileAttached);
        employeeRequest.setDocumentLink(docLink);
        //setting the status as modified
        employeeRequest.setStatus("MODIFIED");
        //System.out.println("In Update status : " + employeeRequest.toString());
        //if file not attached then do nothing.
        dynamo.updateRequest(requestId, employeeRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
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
                                                @RequestParam("amount") int amount, @RequestParam(value = "fileName", required = false) String fileName,
                                                @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {


        //fill the request object with values
        final EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setName(name);
        employeeRequest.setWhere(where);
        employeeRequest.setWhy(why);
        employeeRequest.setWhen(when);
        employeeRequest.setAmount(amount);
        //Generating Random UUID as requestId
        String uniqueRequestId = UUID.randomUUID().toString();
        employeeRequest.setRequestId(uniqueRequestId);
        //Generating the current timestamp
        Long currentTimeStamp = new Date().getTime();
        employeeRequest.setTimestamp(currentTimeStamp.toString());
        //Setting the status of document
        employeeRequest.setStatus("UNCHECKED");
        final boolean fileAttached = file != null && !file.isEmpty();
        employeeRequest.setDocumentLink(NO_DOCUMENT);
        employeeRequest.setDocumentName(NO_DOCUMENT);

        // store the request in the db and store the binary in s3 bucket
        // some help: https://spring.io/guides/gs/uploading-files/
        if (fileAttached) {
            //setting the document
            //getting the extension of the file
            String[] ext = fileName.split("\\.");
            String documentName = employeeRequest.getRequestId() + "." + ext[1];
            employeeRequest.setDocumentName(documentName);
            String docLink = s3.generateURL(employeeRequest.getDocumentName(), true);
            employeeRequest.setDocumentLink(docLink);
            byte[] bytes = file.getBytes();
            File localFile = new File(employeeRequest.getDocumentName());
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(localFile));
            stream.write(bytes);
            stream.close();
            // upload the locally stored file to S3
            s3.storeFile(localFile.getName(), localFile);
        }

        // store employee request
        dynamo.createRequestEntry(employeeRequest);

        //create a message and send to the manager
        mail.sendMail(MANAGER_EMAIL, employeeRequest);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(path = "/requests/{id}/status", method = RequestMethod.POST)
    public ResponseEntity<String> setStatus(@PathVariable String id, @RequestParam("status") String status) {
        dynamo.setStatus(id, status);
        //create a message and send to the employee
        mail.sendMail(EMPLOYEE_EMAIL, id, status);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
