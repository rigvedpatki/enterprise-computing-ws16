package de.tuberlin.enterprisecomputing.integrations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.*;

@Service
public class S3Service {

    private static final String S3_BUCKET = "reimbursement-files";
    AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());

    public void storeFile(final String fileName, final File file) {
        s3client.putObject(new PutObjectRequest(S3_BUCKET, fileName, file));
    }

    public byte[] getFile(final String fileName) throws IOException {
        // get s3Object
        S3Object object = s3client.getObject(new GetObjectRequest(S3_BUCKET, fileName));
        // convert objectData to byte[]
        InputStream objectData = object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectData);
        return bytes;
    }
}
