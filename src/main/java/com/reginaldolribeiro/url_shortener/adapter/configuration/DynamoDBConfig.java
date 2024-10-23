package com.reginaldolribeiro.url_shortener.adapter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Service
public class DynamoDBConfig {

    @Bean
    public DynamoDbClient dynamoDbClient(){
        return DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
    }
}
