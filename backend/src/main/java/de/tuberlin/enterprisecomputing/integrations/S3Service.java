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

    public S3Service() {
        //TODO initialize S3 client
    }

    public void storeFile(final String fileName, final File file) {
        //TODO convert Byte[] to File
    	String bucketName     = S3_BUCKET;
    	String keyName        = fileName;
    	File currentFile 	  = file;
        s3client.putObject(new PutObjectRequest(bucketName, keyName, currentFile));
    }

    public byte[] getFile(final String fileName) throws IOException {
        //TODO get s3Object
    	S3Object object = s3client.getObject(new GetObjectRequest(S3_BUCKET, fileName));
    	//TODO convert objectData to byte[]
    	InputStream objectData = object.getObjectContent();
    	byte[] bytes = IOUtils.toByteArray(objectData);
    	return bytes;
    }
}
