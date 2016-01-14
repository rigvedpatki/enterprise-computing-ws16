package de.tuberlin.enterprisecomputing.integrations;

import java.io.File;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.springframework.stereotype.Service;
import com.amazonaws.HttpMethod;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;


@Service
public class S3Service {

    private final static String S3_BUCKET = "reimbursement-docs";
    private AmazonS3 s3client;

    public S3Service() {
        //s3client = new AmazonS3Client(new ProfileCredentialsProvider("enterprise-computing-ws16").getCredentials());
        s3client = new AmazonS3Client();
        s3client.setRegion(Region.getRegion(Regions.EU_WEST_1));
    }


    public void storeFile(final String fileName, final File file) {
        s3client.putObject(new PutObjectRequest(S3_BUCKET, fileName, file));
    }

    public String generateURL(final String documentName, final boolean fileAttached) {
        if (fileAttached) {
            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(S3_BUCKET, documentName);
            urlRequest.setMethod(HttpMethod.GET);
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MONTH, 1);
            Date nextMonth = calendar.getTime();
            urlRequest.setExpiration(nextMonth);
            URL url = s3client.generatePresignedUrl(urlRequest);
            return url.toString();
        } else {
            return "No File Attached";
        }
    }
}
