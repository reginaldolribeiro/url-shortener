package com.reginaldolribeiro.url_shortener.adapter.repository.user;

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
public class UserDynamoDbRepository {

    public static final String USER_TABLE = "User";

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbTable<UserEntity> userTable;

    public UserDynamoDbRepository(DynamoDbEnhancedClient dynamoDbEnhancedClient) {
        this.dynamoDbEnhancedClient = dynamoDbEnhancedClient;
        this.userTable = dynamoDbEnhancedClient.table(USER_TABLE, TableSchema.fromBean(UserEntity.class));
    }

    @CachePut(value = "userCache", key = "#userEntity.id", unless = "#result == null")
    public UserEntity save(UserEntity userEntity) {
        log.info("Saving User to database ....");
        if (userEntity == null) {
            throw new IllegalArgumentException("User entity cannot be null.");
        }
        try {
            userTable.putItem(userEntity);
            return userEntity;
        } catch (DynamoDbException e) {
            log.error("Error saving user with ID: {}", userEntity.getId(), e);
            throw new UserSaveDatabaseException("Failed to save user with ID: " + userEntity.getId(), e);
        }
    }

    @Cacheable(value = "userCache", key = "#id", unless = "#result == null")
    public Optional<UserEntity> findById(String id) {
        log.info("Searching UserID {} in database", id);
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("User ID cannot be null.");

        try {
            QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder()
                    .partitionValue(id)
                    .build());

            var results = userTable.query(r -> r.queryConditional(queryConditional));
            var userEntity = results.items().stream().findFirst();

            // Return empty Optional if no result to avoid caching null values
            return userEntity.isPresent() ? userEntity : Optional.empty();

        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", id, e);
            throw new UserSearchDatabaseException("Failed to search user with ID: " + id, e);
        }
    }

}
