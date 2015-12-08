package de.tuberlin.enterprisecomputing.integrations;

import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final String FROM = "user@mail.com";

    public MailService(){
        //TODO initialize SES client
    }

    public void sendMail(final String to, final String content){
        //TODO send email
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
