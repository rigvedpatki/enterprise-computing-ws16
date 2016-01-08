package de.tuberlin.enterprisecomputing.integrations;

import org.springframework.stereotype.Service;
import com.amazonaws.services.simpleemail.*;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.*;

@Service
public class MailService {

    //Declare Email addresses of manager and employee
    private final String MANAGER = "rigved.patki@gmail.com";
    private final String EMPLOYEE = "rigved.patki@outlook.com";
    //commented for local testing
    // private final String MANAGER = "ec2015manager@gmail.com";
    // private final String EMPLOYEE = "ec2015employee@gmail.com";
    //declare client
    private AmazonSimpleEmailServiceClient client;


    public MailService() {
        // Initialise SES client, set region
        client = new AmazonSimpleEmailServiceClient(new ProfileCredentialsProvider("enterprise-computing-ws16").getCredentials());
        //client = new AmazonSimpleEmailServiceClient(new ProfileCredentialsProvider().getCredentials());
        Region REGION = Region.getRegion(Regions.EU_WEST_1);
        client.setRegion(REGION);



       
    }

    public void sendMail(final String to) {
        // send email
        if (to.equals(MANAGER)){
        	
            SendEmailRequest managerRequest = new SendEmailRequest()
                    .withSource(EMPLOYEE)
                    .withDestination(new Destination().withToAddresses(new String[]{MANAGER}))
                    .withMessage(new Message().withSubject(new Content().withData(this.managerEmail()[0]))
                            .withBody(new Body().withText(new Content().withData(this.managerEmail()[1]))));
            
            client.sendEmail(managerRequest);
        }
        else if (to.equals(EMPLOYEE)){
        	
        	 SendEmailRequest employeeRequest = new SendEmailRequest()
                     .withSource(MANAGER)
                     .withDestination(new Destination().withToAddresses(new String[]{EMPLOYEE}))
                     .withMessage(new Message().withSubject(new Content().withData(this.employeeEmail()[0]))
                             .withBody(new Body().withText(new Content().withData(this.employeeEmail()[1]))));
        	 
            client.sendEmail(employeeRequest);
        }
    }

    // Returns Email template for the manager
    private String[] managerEmail() {
        String subject = "New reimbursement submission!";
        String body = String
                .format("This is an automatically generated E-mail! Do not respond!"
                        + "\n\nAn employee has made a new submission for a reimbursement!"
                        + "\nYou can accept/decline the reimbursement at:"
                        + "\nhttp://reimbursement-frontend.mybluemix.net/manager"
                );
        String[] mail = {subject, body};
        return mail;
    }

    // Returns Email template for the employee
    private String[] employeeEmail() {
        String subject = "Answer to reimbursement submission!";
        String body = String
                .format("This is an automatically generated E-mail! Do not respond!"
                        + "\n\nThe manager has answered to your reimbursement submission!"
                        + "\nYou can see the status to your reimbursement at:"
                        + "\nhttp://reimbursement-frontend.mybluemix.net/employee"
                );
        String[] mail = {subject, body};
        return mail;
    }
}
