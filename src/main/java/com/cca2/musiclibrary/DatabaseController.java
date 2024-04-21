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
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

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

    /*
     * 
     * 
     */
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

    /*
     * 
     * 
     */
    public void addUserEntry(String email, String username, String password) {

        table = dynamoDB.getTable(tableName);

        Item item = new Item().withPrimaryKey("email", email).withString("user_name", username).withString("password",
                password);
        table.putItem(item);

        System.out.println("New user added successfully");

    }

    /*
     * 
     * 
     */
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

    /*
     * 
     * 
     */
    public void addToSubscriptions(String email, String subTitle) {

        table = dynamoDB.getTable(tableName);

        ArrayList<String> newList = new ArrayList<String>(getSubsTitlesByEmail(email));
        newList.add(subTitle);

        try {
            Item item = new Item()
                    .withPrimaryKey("email", email)
                    .withList("subscriptions", newList);

            table.putItem(item);
            System.out.println("Item added successfully.");
        } catch (Exception e) {
            System.err.println("Unable to add item: " + e.getMessage());
        }

    }

    /*
     * 
     * 
     */
    private List<String> getSubsTitlesByEmail(String email) {

        List<String> list = new ArrayList<String>();

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#email = :email")
                .withNameMap(new NameMap().with("#email", "email"))
                .withValueMap(new ValueMap().withString(":email", email)).withMaxResultSize(1);

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);

            for (Item item : items) {

                Subscription sub = new Subscription();
                sub.setEmail(item.getString("email"));
                sub.setSubscription(item.getList("subscriptions"));

                list = sub.getSubscription();

            }

        } catch (

        Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return list;
    }

    /*
     * 
     * 
     */
    public List<Song> getSubscriptionsByEmail(String email) {

        table = dynamoDB.getTable(tableName);

        List<Song> results = new ArrayList<Song>();

        List<String> subSongs = getSubsTitlesByEmail(email);

        for (String sub : subSongs) {

            results.add(getSongByTitle(sub));

        }

        return results;
    }

    /*
     * 
     * 
     */
    private Song getSongByTitle(String title) {

        table = dynamoDB.getTable("music");

        Song song = new Song();

        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("#title = :title")
                .withNameMap(new NameMap().with("#title", "title"))
                .withValueMap(new ValueMap().withString(":title", title));

        try {
            ItemCollection<ScanOutcome> items = table.scan(scanSpec);

            items.firstPage().forEach(item -> {

                song.setTitle(item.getString("title"));
                song.setYear(item.getString("year"));
                song.setArtist(item.getString("artist"));
                song.setWebUrl(item.getString("web_url"));
                song.setImgUrl(item.getString("image_url"));

            });

        } catch (Exception e) {
            System.err.println("Unable to scan the table:");
            System.err.println(e.getMessage());
        }

        return song;
    }

    /*
     * 
     * 
     */
    public void removeFromSubscribeList(String email, String songTitle) {

        table = dynamoDB.getTable(tableName);

        List<String> subSongs = getSubsTitlesByEmail(email);

        for (int i = 0; i < subSongs.size(); i++) {
            if (subSongs.get(i).equals(songTitle)) {
                subSongs.remove(i);

            }
        }

        try {
            Item item = new Item()
                    .withPrimaryKey("email", email)
                    .withList("subscriptions", subSongs);

            table.putItem(item);
            System.out.println("Item Removed successfully.");
        } catch (Exception e) {
            System.err.println("Unable to remove item: " + e.getMessage());
        }

    }
}
