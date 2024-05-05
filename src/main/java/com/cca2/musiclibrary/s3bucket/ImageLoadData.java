package com.cca2.musiclibrary.s3bucket;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.springframework.core.io.ClassPathResource;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ImageLoadData {
    public static void main(String[] args) throws Exception {

        ArrayList<String> imageURLs = getImgURLFromFile("a2.json");

        for (String url : imageURLs) {
            addToS3(url);
        }

    }

    private static void addToS3(String url) throws MalformedURLException, IOException {

        Regions clientRegion = Regions.US_EAST_1;
        String imageUrl = url;
        String bucketName = "cca2.artists";
        String key = imageUrl.substring(87, imageUrl.length());

        try {

            // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .build();

            try (InputStream inputStream = new URL(imageUrl).openStream()) {
                // Read input stream fully to determine its size
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, bytesRead);
                }

                // Get the content length
                byte[] data = baos.toByteArray();
                long contentLength = data.length;

                // Upload the file to S3 with the correct content length
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentType("image/jpeg");
                metadata.setContentLength(contentLength);
                metadata.addUserMetadata("artist", key);

                PutObjectRequest request = new PutObjectRequest(bucketName, key,
                        new ByteArrayInputStream(data), metadata);
                s3Client.putObject(request);

                System.out.println(key + " uploaded to S3 Bucket Successfully.");
            }

        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

    }

    private static ArrayList<String> getImgURLFromFile(String string) throws JsonParseException, IOException {

        HashSet<String> imageURLs = new HashSet<String>();
        ArrayList<String> imageLinks = new ArrayList<String>();

        ClassPathResource resource = new ClassPathResource("a2.json");

        JsonParser parser = new JsonFactory().createParser(resource.getInputStream());

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;
        int counter = 0;

        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();

            String imageURL = currentNode.path("img_url").asText();
            imageURLs.add(imageURL);

        }

        for (String img : imageURLs) {
            imageLinks.add(img);
            counter++;

        }

        System.out.println(counter + " URLs added to the list.");
        return imageLinks;

    }
}