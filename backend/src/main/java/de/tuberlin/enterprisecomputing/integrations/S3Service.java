package de.tuberlin.enterprisecomputing.integrations;

import org.springframework.stereotype.Service;

@Service
public class S3Service {

    private static final String S3_BUCKET = "reimbursement-files";

    public S3Service() {
        //TODO initialize S3 client
    }

    public void storeFile(final String fileName, final Byte[] file) {
        //TODO
    }

    public Byte[] getFile(final String fileName) {
        //TODO
        return null;
    }
}
