// Copyright 2012-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

package com.cca2.musiclibrary.dynamoDB;

import java.util.Iterator;

import org.springframework.core.io.ClassPathResource;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class MusicLoadData {

    public static void main(String[] args) throws Exception {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://dynamodb.us-east-1.amazonaws.com",
                                Regions.US_EAST_1.getName()))
                .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        Table table = dynamoDB.getTable("music");

        ClassPathResource resource = new ClassPathResource("a2.json");

        JsonParser parser = new JsonFactory().createParser(resource.getInputStream());

        JsonNode rootNode = new ObjectMapper().readTree(parser);
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;

        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();

            String artist = currentNode.path("artist").asText();
            String title = currentNode.path("title").asText();
            int year = currentNode.path("year").asInt();
            String webURL = currentNode.path("web_url").asText();
            String imageURL = currentNode.path("img_url").asText();

            try {
                table.putItem(new Item().withPrimaryKey("artist", artist, "title", title).withInt("year", year)
                        .withString("web_url", webURL).withString("image_url", imageURL));
                System.out.println("PutItem succeeded: " + artist + " " + title);

            } catch (Exception e) {
                System.err.println("Unable to add movie: " + artist + " " + title);
                System.err.println(e.getMessage());
                break;
            }
        }
        parser.close();
    }
}