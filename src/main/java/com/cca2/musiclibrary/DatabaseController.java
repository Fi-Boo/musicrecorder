package com.cca2.musiclibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
// import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
// import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
// import com.amazonaws.services.dynamodbv2.model.KeyType;
// import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
// import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;

public class DatabaseController {

    private AmazonDynamoDB client;
    private DynamoDB dynamoDB;
    private String tableName;
    private Table table;

    public DatabaseController(String tableName) {

        this.tableName = tableName;
        this.client = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(
                        new AwsClientBuilder.EndpointConfiguration(
                                "http://dynamodb.us-east-1.amazonaws.com",
                                Regions.US_EAST_1.getName()))
                .build();

        this.dynamoDB = new DynamoDB(client);

    }

    // public List<User> checkUserByLogin(String email, String password) {

    // List<User> results = new ArrayList<User>();

    // table = dynamoDB.getTable("login");

    // ScanSpec scanSpec = new ScanSpec()
    // .withFilterExpression("#email = :email AND #password = :password")
    // .withNameMap(new NameMap().with("#email", "email").with("#password",
    // "password"))
    // .withValueMap(new ValueMap().withString(":email",
    // email).withString(":password", password));

    // try {
    // ItemCollection<ScanOutcome> items = table.scan(scanSpec);

    // for (Item item : items) {

    // User user = new User();
    // user.setEmail(item.getString("email"));
    // user.setUsername(item.getString("user_name"));
    // user.setPassword(item.getString("password"));
    // results.add(user);

    // System.out.println("match found");
    // }

    // } catch (Exception e) {
    // System.err.println("Unable to scan the table:");
    // System.err.println(e.getMessage());
    // }

    // return results;
    // }

    public List<User> getDatabaseListByEmail(String email) {

        List<User> results = new ArrayList<User>();

        table = dynamoDB.getTable(tableName);

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#email = :email")
                .withNameMap(new NameMap().with("#email", "email"))
                .withValueMap(new ValueMap().withString(":email", email));

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);

            for (Item item : items) {

                User user = new User();
                user.setEmail(item.getString("email"));
                user.setUsername(item.getString("user_name"));
                user.setPassword(item.getString("password"));
                results.add(user);

                System.out.println("Scan Outcome: match found");
            }

        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return results;
    }

    public void addUserEntry(String email, String username, String password) {

        table = dynamoDB.getTable(tableName);

        Item item = new Item().withPrimaryKey("email", email).withString("user_name", username).withString("password",
                password);
        table.putItem(item);
    }

    public List<Song> getSongs2(String title, String year, String artist) {

        System.out.println(title);
        System.out.println(year);
        System.out.println(artist);

        table = dynamoDB.getTable(tableName);

        List<Song> resultList = new ArrayList<>();

        // Map<String, String> expressionAttributeNames = new HashMap<>();
        // expressionAttributeNames.put("#title", "title");
        // expressionAttributeNames.put("#year", "year");
        // expressionAttributeNames.put("#artist", "artist");

        // Map<String, Object> expressionAttributeValues = new HashMap<>();
        // expressionAttributeValues.put(":title", title);
        // expressionAttributeValues.put(":year", year);
        // expressionAttributeValues.put(":artist", artist);

        // StringBuilder filterExpressionBuilder = new StringBuilder();

        // if (artist != null && !artist.isEmpty()) {
        // filterExpressionBuilder.append("#title = :title");
        // }
        // if (title != null && !title.isEmpty()) {
        // if (filterExpressionBuilder.length() > 0) {
        // filterExpressionBuilder.append(" AND ");
        // }
        // filterExpressionBuilder.append("#year = :year");
        // }
        // if (year != null && !year.isEmpty()) {
        // if (filterExpressionBuilder.length() > 0) {
        // filterExpressionBuilder.append(" AND ");
        // }
        // filterExpressionBuilder.append("#artist = :artist");
        // }

        // ScanSpec scanSpec = new ScanSpec();

        // if (filterExpressionBuilder.length() > 0) {
        // scanSpec.withFilterExpression(filterExpressionBuilder.toString())
        // .withNameMap(expressionAttributeNames)
        // .withValueMap(expressionAttributeValues);
        // }

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#artist = :artist")
                .withNameMap(new NameMap().with("#artist", "artist"))
                .withValueMap(new ValueMap().withString(":artist", artist));

        ItemCollection<ScanOutcome> items = table.scan(scanSpec);

        for (Item item : items) {

            Song song = new Song();
            song.setTitle(item.getString("title"));
            song.setYear(item.getString("year"));
            song.setArtist(item.getString("artist"));
            song.setWebUrl(item.getString("web_url"));
            song.setImgUrl(item.getString("image_url"));
            resultList.add(song);
        }

        return resultList;
    }

    public List<Song> getSongs(String title, String year, String artist) {

        table = dynamoDB.getTable(tableName);

        List<Song> resultList = new ArrayList<>();

        Map<String, String> expressionAttributeNames = new HashMap<>();
        Map<String, Object> expressionAttributeValues = new HashMap<>();

        StringBuilder filterExpressionBuilder = new StringBuilder();

        if (title != null && !title.isEmpty()) {
            filterExpressionBuilder.append("#title = :title");
            expressionAttributeNames.put("#title", "title");
            expressionAttributeValues.put(":title", title);
        }
        if (year != null && !year.isEmpty()) {
            if (filterExpressionBuilder.length() > 0) {
                filterExpressionBuilder.append(" AND ");
            }
            filterExpressionBuilder.append("#year = :year");
            expressionAttributeNames.put("#year", "year");
            expressionAttributeValues.put(":year", year);
        }
        if (artist != null && !artist.isEmpty()) {
            if (filterExpressionBuilder.length() > 0) {
                filterExpressionBuilder.append(" AND ");
            }
            filterExpressionBuilder.append("#artist = :artist");
            expressionAttributeNames.put("#artist", "artist");
            expressionAttributeValues.put(":artist", artist);
        }

        ScanSpec scanSpec = new ScanSpec();

        if (filterExpressionBuilder.length() > 0) {
            scanSpec.withFilterExpression(filterExpressionBuilder.toString())
                    .withNameMap(expressionAttributeNames)
                    .withValueMap(expressionAttributeValues);
        }

        ItemCollection<ScanOutcome> items = table.scan(scanSpec);

        for (Item item : items) {

            Song song = new Song();
            song.setTitle(item.getString("title"));
            song.setYear(item.getString("year"));
            song.setArtist(item.getString("artist"));
            song.setWebUrl(item.getString("web_url"));
            song.setImgUrl(item.getString("image_url"));
            resultList.add(song);
        }

        return resultList;
    }

}
