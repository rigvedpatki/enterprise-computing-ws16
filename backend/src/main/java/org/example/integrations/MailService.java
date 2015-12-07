package org.example.integrations;

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
}
