package com.reginaldolribeiro.url_shortener.adapter.repository;

import com.reginaldolribeiro.url_shortener.adapter.helper.DateTimeHelper;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UrlDynamoDBRepository {

    public static final String URL_MAPPINGS = "UrlMappings";
    private final DynamoDbClient dynamoDbClient;
    private final UserRepositoryPort userRepositoryPort;

    public UrlDynamoDBRepository(DynamoDbClient dynamoDbClient, UserRepositoryPort userRepositoryPort) {
        this.dynamoDbClient = dynamoDbClient;
        this.userRepositoryPort = userRepositoryPort;
    }

    public void save(UrlEntity urlEntity) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("shortUrlId", AttributeValue.builder().s(urlEntity.shortUrlId()).build());
        item.put("longUrl", AttributeValue.builder().s(urlEntity.longUrl()).build());
        item.put("createdDate", AttributeValue.builder().s(DateTimeHelper.toString(urlEntity.createdDate())).build());
        item.put("userId", AttributeValue.builder().s(urlEntity.userId()).build());
        item.put("clicks", AttributeValue.builder().n(String.valueOf(urlEntity.clicks())).build());
        item.put("isActive", AttributeValue.builder().bool(urlEntity.isActive()).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(URL_MAPPINGS)
                .item(item)
                .build();

        dynamoDbClient.putItem(request);
    }

    public Optional<Url> findByShortenedUrl(String shortenedUrl) {
        var partitionKey = "shortUrlId";
        var partitionKeyRequest = ":shortUrlId";
        var sortKey = "userId";

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":shortUrlId", AttributeValue.builder().s(shortenedUrl).build());

        var queryRequest = QueryRequest.builder()
                .tableName(URL_MAPPINGS)
                .keyConditionExpression(partitionKey + " = " + partitionKeyRequest)
                .expressionAttributeValues(expressionAttributeValues)
                .build();

        try {
            var result = dynamoDbClient.query(queryRequest);
            if (result.hasItems() && !result.items().isEmpty()) {
                var urlEntity = mapFromDatabase(result.items().getFirst());
                var user = userRepositoryPort.get("9b8a2db5-e50a-481f-9e9a-828c37e721c1")
                        .orElseThrow(() -> new UserNotFoundException("User " + urlEntity.userId() + " not found."));

                return Optional.of(
                        UrlEntity.fromMapping(
                                urlEntity.shortUrlId(),
                                urlEntity.longUrl(),
                                urlEntity.createdDate(),
                                user,
                                urlEntity.clicks(),
                                urlEntity.isActive()
                        )
                );
            }
        } catch (DynamoDbException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
            throw new UrlSearchDatabaseException(e.getMessage());
        }

        return Optional.empty();
    }

    /*public Optional<Url> findByShortenedUrl(String shortenedUrl) {
        String partitionKey = "shortUrlId";
        String sortKey = "userId";

        Map<String, AttributeValue> keyToGet = new HashMap<>();
        keyToGet.put(partitionKey, AttributeValue.builder().s(shortenedUrl).build());
//        keyToGet.put(sortKey, AttributeValue.builder().s("9b8a2db5-e50a-481f-9e9a-828c37e721c1").build());

        var request = GetItemRequest.builder()
                .key(keyToGet)
                .tableName(URL_MAPPINGS)
                .build();

        try {
            Map<String, AttributeValue> returnedItem = dynamoDbClient.getItem(request).item();

            if (returnedItem != null && !returnedItem.isEmpty()) {

                UrlEntity urlMappings = mapToUrlMappings(returnedItem);
                var user = userRepositoryPort.get(urlMappings.getUserId())
                        .orElseThrow(() -> new UserNotFoundException("User " + urlMappings.getUserId() + " not found."));

                var url = Url.create(urlMappings.getShortUrlId(), urlMappings.getLongUrl(), user);

                return Optional.of(url);

            } else {
                System.out.format("No item found with the key %s!\n", partitionKey);
            }
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return Optional.empty();
    }*/

    private UrlEntity mapFromDatabase(Map<String, AttributeValue> item) {
        return new UrlEntity(
                item.get("shortUrlId").s(),
                item.get("longUrl").s(),
                LocalDateTime.parse(item.get("createdDate").s()), // Assuming ISO-8601 format
                item.get("userId").s(),
                Integer.parseInt(item.get("clicks").n()),
                item.get("isActive").bool()
        );
    }

}
