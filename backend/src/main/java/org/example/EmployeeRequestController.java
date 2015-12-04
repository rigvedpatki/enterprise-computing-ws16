package org.example;

import org.example.domain.EmployeeRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeRequestController {

    @RequestMapping(path = "/requests/{id}", method = RequestMethod.GET)
    public EmployeeRequest getRequest(@PathVariable String id) {
        //TODO return the particular request
        return new EmployeeRequest("asd", "asd", "asd", "aasd", "123", "213", 123);
    }

    @RequestMapping(path = "/requests", method = RequestMethod.GET)
    public List<EmployeeRequest> getRequests() {
        //TODO return all requests from the db
        return new ArrayList<>();
    }

    @RequestMapping(path = "/requests", method = RequestMethod.POST)
    public void putRequest(@RequestParam("name") String name, /*add other values as request params*/
                           @RequestParam(value = "file", required = false) MultipartFile file) {
        //TODO store the request in the db and store the binary in s3 bucket
        // some help: https://spring.io/guides/gs/uploading-files/
        return;
    }

    @RequestMapping(path = "/requests/{id}/status", method = RequestMethod.PUT)
    public void putStatus(@PathVariable String id, @RequestParam("status") String status){
        //TODO update the status of request (rewrite)
        return;
    }
}
