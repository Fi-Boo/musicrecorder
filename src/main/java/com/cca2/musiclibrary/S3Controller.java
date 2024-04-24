package com.cca2.musiclibrary;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

public class S3Controller {

    private final AmazonS3 s3Client;

    public S3Controller() {
        // Initialize the Amazon S3 client
        this.s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new ProfileCredentialsProvider())
                .build();

    }

    public InputStream getStream(String bucketName, String fileName) throws IOException {
        try {
            // Get the S3 object
            S3Object s3Object = s3Client.getObject(bucketName, fileName);
            // Return the input stream of the S3 object
            return s3Object.getObjectContent();
        } catch (AmazonServiceException e) {
            throw new IOException("Error downloading file from S3", e);
        }
    }

}
