package de.tuberlin.enterprisecomputing.integrations;

import org.springframework.stereotype.Service;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;

import de.tuberlin.enterprisecomputing.domain.EmployeeRequest;


import com.amazonaws.regions.*;

@Service
public class MailService {

    //Declare Email addresses of manager and employee
    private final static String MANAGER = "ec2015manager@gmail.com";
    private final static String EMPLOYEE = "ec2015employee@gmail.com";
    //declare client
    private AmazonSimpleEmailServiceClient client;


    public MailService() {
        // Initialise SES client, set region
        //client = new AmazonSimpleEmailServiceClient(new ProfileCredentialsProvider("enterprise-computing-ws16").getCredentials());
        client = new AmazonSimpleEmailServiceClient();
        Region REGION = Region.getRegion(Regions.EU_WEST_1);
        client.setRegion(REGION);
    }
    public void sendMail(final String to, String requestId, String status){
    	if (to.equals(EMPLOYEE)) {

            SendEmailRequest employeeRequest = new SendEmailRequest()
                    .withSource(MANAGER)
                    .withDestination(new Destination().withToAddresses(EMPLOYEE))
                    .withMessage(new Message().withSubject(new Content().withData(this.employeeEmail(requestId, status)[0]))
                            .withBody(new Body().withText(new Content().withData(this.employeeEmail(requestId, status)[1]))));

            client.sendEmail(employeeRequest);
        }
    }
    public void sendMail(final String to, EmployeeRequest request) {
        // send email
        if (to.equals(MANAGER)) {

            SendEmailRequest managerRequest = new SendEmailRequest()
                    .withSource(EMPLOYEE)
                    .withDestination(new Destination().withToAddresses(MANAGER))
                    .withMessage(new Message().withSubject(new Content().withData(this.managerEmail(request)[0]))
                            .withBody(new Body().withText(new Content().withData(this.managerEmail(request)[1]))));

            client.sendEmail(managerRequest);
        } 
    }

    // Returns Email template for the manager
    private String[] managerEmail(EmployeeRequest request) {
        String subject = "New reimbursement submission added";
        String body = "This is an automatically generated E-mail! Do not respond!"
                + "\n\n Employee "+ request.getName()+" has made a new submission for a reimbursement!"
                + "\n Reimbursement Id: " + request.getRequestId()
                + "\n Status: " + request.getStatus()
                + "\nYou can accept/decline the reimbursement at:"
                + "\nhttp://reimbursement-frontend.mybluemix.net/manager";
        return new String[]{subject, body};
    }

    // Returns Email template for the employee
    private String[] employeeEmail(String requestId, String status) {
        String subject = "Answer to reimbursement submission!";
        String body = "This is an automatically generated E-mail! Do not respond!"
                + "\n\nThe manager has answered to your reimbursement submission!"
                + "\n Reimbursement Id: " + requestId
                + "\n Status: " + status
                + "\nYou can modifiy your reimbursement at:"
                + "\nhttp://reimbursement-frontend.mybluemix.net/employee";
        return new String[]{subject, body};
    }
}
