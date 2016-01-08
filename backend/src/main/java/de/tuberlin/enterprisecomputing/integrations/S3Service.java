package de.tuberlin.enterprisecomputing.integrations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.*;

@Service
public class S3Service {

    private final String S3_BUCKET = "reimbursement-docs1";
    //private final String S3_BUCKET = "reimbursement-docs";
    private AmazonS3 s3client;

    public S3Service() {
        s3client = new AmazonS3Client(new ProfileCredentialsProvider("enterprise-computing-ws16").getCredentials());
        //s3client = new AmazonS3Client(new ProfileCredentialsProvider().getCredentials());
        s3client.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }


    public void storeFile(final String fileName, final File file) {
        s3client.putObject(new PutObjectRequest(S3_BUCKET, fileName, file));
    }

    public String generateURL(final String fileName) {
        GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(S3_BUCKET, fileName);
        urlRequest.setMethod(HttpMethod.GET);
        Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.MONTH, 1);
        Date nextMonth = calendar.getTime();
        urlRequest.setExpiration(nextMonth);
        URL s = s3client.generatePresignedUrl(urlRequest);
        return s.toString();
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
