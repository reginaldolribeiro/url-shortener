package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Optional;

@Service
@Slf4j
public class UrlDynamoDbRepository {

    public static final String URL_MAPPINGS = "UrlMappings";

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<UrlEntity> urlTable;

    public UrlDynamoDbRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.urlTable = dynamoDbEnhancedClient.table(URL_MAPPINGS, TableSchema.fromBean(UrlEntity.class));
    }

    @CachePut(value = "urlCache", key = "#urlEntity.shortUrlId", unless = "#result == null")
    public UrlEntity save(UrlEntity urlEntity) {
        log.info("Saving URL to database ....");

        if (urlEntity == null) {
            throw new IllegalArgumentException("Url cannot be null.");
        }

        try {
            urlTable.putItem(urlEntity);
            return urlEntity;
        } catch (DynamoDbException e) {
            log.error("Error saving URL with ID: {}", urlEntity.getShortUrlId(), e);
            throw new UrlSaveDatabaseException("Failed to save URL with ID: " + urlEntity.getShortUrlId(), e);
        }
    }

    @Cacheable(value = "urlCache", key = "#id", unless = "#result == null")
    public Optional<UrlEntity> findByShortenedUrl(String id) {
        log.info("Searching for {} in the database", id);
        if(id == null || id.isBlank())
            throw new IllegalArgumentException("Url cannot be null.");

        try {
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(id)
                    .build());

            var results = urlTable.query(r -> r.queryConditional(queryConditional));
            var urlEntity = results.items().stream().findFirst();

            return urlEntity.isPresent() ? urlEntity : Optional.empty();

        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", id, e);
            throw new UrlSearchDatabaseException("Failed to search URL with ID: " + id, e);
        }
    }

}
