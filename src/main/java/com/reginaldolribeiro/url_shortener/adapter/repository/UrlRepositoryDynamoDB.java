package com.reginaldolribeiro.url_shortener.adapter.repository;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

@Service
public class UrlRepositoryDynamoDB {

    private final DynamoDbClient dynamoDbClient;

    public UrlRepositoryDynamoDB(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }

    public void save(UrlMappings urlMappings){
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortUrlId", AttributeValue.builder().s(urlMappings.getShortUrlId()).build());
        item.put("longUrl", AttributeValue.builder().s(urlMappings.getLongUrl()).build());
        item.put("createdDate", AttributeValue.builder().s(urlMappings.getCreatedDate()).build());
        item.put("userId", AttributeValue.builder().s(urlMappings.getUserId()).build());
        item.put("clicks", AttributeValue.builder().n(String.valueOf(urlMappings.getClicks())).build());
        item.put("isActive", AttributeValue.builder().bool(urlMappings.isActive()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName("UrlMappings")
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

}
