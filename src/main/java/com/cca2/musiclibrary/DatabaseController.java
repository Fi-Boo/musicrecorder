package com.cca2.musiclibrary;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class DatabaseController {

    DynamoDB dynamoDB;
    Table table;

    public DatabaseController() {

        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://dynamodb.us-east-1.amazonaws.com",
                                Regions.US_EAST_1.getName()))
                .build();

        dynamoDB = new DynamoDB(client);

    }

    public List<User> checkUserByLogin(String email, String password) {

        List<User> results = new ArrayList<User>();

        table = dynamoDB.getTable("login");

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#email = :email AND #password = :password")
                .withNameMap(new NameMap().with("#email", "email").with("#password", "password"))
                .withValueMap(new ValueMap().withString(":email", email).withString(":password", password));

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);

            for (Item item : items) {

                User user = new User();
                user.setEmail(item.getString("email"));
                user.setUsername(item.getString("user_name"));
                user.setPassword(item.getString("password"));
                results.add(user);

                System.out.println("match found");
            }

        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return results;
    }

}
