package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import com.reginaldolribeiro.url_shortener.adapter.helper.DateTimeHelper;
import com.reginaldolribeiro.url_shortener.app.domain.Url;
import com.reginaldolribeiro.url_shortener.app.exception.UserNotFoundException;
import com.reginaldolribeiro.url_shortener.app.port.UserRepositoryPort;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UrlDynamoDBRepository {

    public static final String URL_MAPPINGS = "UrlMappings";
    private final DynamoDbClient dynamoDbClient;
    private final UserRepositoryPort userRepositoryPort;

    public UrlDynamoDBRepository(DynamoDbClient dynamoDbClient, UserRepositoryPort userRepositoryPort) {
        this.dynamoDbClient = dynamoDbClient;
        this.userRepositoryPort = userRepositoryPort;
    }

    public void save(UrlEntity urlEntity) {
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("shortUrlId", AttributeValue.builder().s(urlEntity.shortUrlId()).build());
            item.put("longUrl", AttributeValue.builder().s(urlEntity.longUrl()).build());
            item.put("createdAt", AttributeValue.builder().s(DateTimeHelper.toString(urlEntity.createdAt())).build());
            item.put("updatedAt", AttributeValue.builder().s(DateTimeHelper.toString(urlEntity.updatedAt())).build());
            item.put("userId", AttributeValue.builder().s(urlEntity.userId()).build());
            item.put("clicks", AttributeValue.builder().n(String.valueOf(urlEntity.clicks())).build());
            item.put("isActive", AttributeValue.builder().bool(urlEntity.isActive()).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(URL_MAPPINGS)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
        } catch (DynamoDbException e){
            log.error("Error saving URL with ID: {}", urlEntity.shortUrlId(), e);
            throw new UrlSaveDatabaseException("Failed to save URL with ID: " + urlEntity.shortUrlId(), e);
        }
    }

    public Optional<Url> findByShortenedUrl(String shortenedUrl) {
        var partitionKey = "shortUrlId";
        var partitionKeyRequest = ":shortUrlId";
        var sortKey = "userId";

        try {
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(":shortUrlId", AttributeValue.builder().s(shortenedUrl).build());

            var queryRequest = QueryRequest.builder()
                    .tableName(URL_MAPPINGS)
                    .keyConditionExpression(partitionKey + " = " + partitionKeyRequest)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build();

            var result = dynamoDbClient.query(queryRequest);
            if (result.hasItems() && !result.items().isEmpty()) {
                var urlEntity = mapFromDatabase(result.items().getFirst());
                var user = userRepositoryPort.findById(urlEntity.userId())
                        .orElseThrow(() -> new UserNotFoundException("User " + urlEntity.userId() + " not found."));

                return Optional.of(
                        UrlEntity.fromMapping(
                                urlEntity.shortUrlId(),
                                urlEntity.longUrl(),
                                urlEntity.createdAt(),
                                urlEntity.updatedAt(),
                                user,
                                urlEntity.clicks(),
                                urlEntity.isActive()
                        )
                );
            }
        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", shortenedUrl, e);
            throw new UrlSearchDatabaseException("Failed to search URL with ID: " + shortenedUrl, e);
        }

        return Optional.empty();
    }

    private UrlEntity mapFromDatabase(Map<String, AttributeValue> item) {
        return new UrlEntity(
                item.get("shortUrlId").s(),
                item.get("longUrl").s(),
                LocalDateTime.parse(item.get("createdAt").s()), // Assuming ISO-8601 format
                LocalDateTime.parse(item.get("updatedAt").s()), // Assuming ISO-8601 format
                item.get("userId").s(),
                Integer.parseInt(item.get("clicks").n()),
                item.get("isActive").bool()
        );
    }

}
