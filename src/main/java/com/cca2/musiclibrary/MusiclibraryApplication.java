package com.cca2.musiclibrary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@SpringBootApplication
public class MusiclibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusiclibraryApplication.class, args);
	}

	@Bean
	public DynamoDbClient dynamoDBClient() {
		Region region = Region.US_EAST_1;
		DynamoDbClient ddb = DynamoDbClient.builder().region(region).build();

		return ddb;
	}

}
