package com.reginaldolribeiro.url_shortener.adapter.repository.user;

import com.reginaldolribeiro.url_shortener.adapter.helper.DateTimeHelper;
import com.reginaldolribeiro.url_shortener.app.domain.User;
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
public class UserDatabaseRepository implements UserRepositoryPort {

    public static final String USER_TABLE = "User";

    private final DynamoDbClient dynamoDbClient;

    public UserDatabaseRepository(DynamoDbClient dynamoDbClient) {
        this.dynamoDbClient = dynamoDbClient;
    }


    @Override
    public Optional<User> findById(String userId) {
        var partitionKey = "id";
        var partitionKeyRequest = ":id";

        try {
            Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
            expressionAttributeValues.put(partitionKeyRequest, AttributeValue.builder().s(userId).build());

            var queryRequest = QueryRequest.builder()
                    .tableName(USER_TABLE)
                    .keyConditionExpression(partitionKey + " = " + partitionKeyRequest)
                    .expressionAttributeValues(expressionAttributeValues)
                    .build();

            var result = dynamoDbClient.query(queryRequest);
            if (result.hasItems() && !result.items().isEmpty()) {
                var userEntity = mapFromDatabase(result.items().getFirst());
                return Optional.of(
                        UserEntity.fromMapping(userEntity.id(),
                                userEntity.name(),
                                userEntity.email(),
                                userEntity.createdAt(),
                                userEntity.updatedAt(),
                                userEntity.active())
                );
            }
        } catch (DynamoDbException e) {
            log.error("Error finding user with ID: {}", userId, e);
            throw new UserSearchDatabaseException("Failed to search user with ID: " + userId, e);
        }

        return Optional.empty();
    }

    @Override
    public void save(User user) {
        var userEntity = new UserEntity(user.getId().toString(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.isActive());
        try {
            Map<String, AttributeValue> item = new HashMap<>();
            item.put("id", AttributeValue.builder().s(userEntity.id()).build());
            item.put("name", AttributeValue.builder().s(userEntity.name()).build());
            item.put("email", AttributeValue.builder().s(userEntity.email()).build());
            item.put("createdAt", AttributeValue.builder().s(DateTimeHelper.toString(userEntity.createdAt())).build());
            item.put("updatedAt", AttributeValue.builder().s(DateTimeHelper.toString(userEntity.updatedAt())).build());
            item.put("active", AttributeValue.builder().bool(userEntity.active()).build());

            PutItemRequest request = PutItemRequest.builder()
                    .tableName(USER_TABLE)
                    .item(item)
                    .build();

            dynamoDbClient.putItem(request);
        } catch (DynamoDbException e) {
            log.error("Error saving user with ID: {}", userEntity.id(), e);
            throw new UserSaveDatabaseException("Failed to save user with ID: " + userEntity.id(), e);
        }
    }

    private UserEntity mapFromDatabase(Map<String, AttributeValue> item) {
        return new UserEntity(
                item.get("id").s(),
                item.get("name").s(),
                item.get("email").s(),
                LocalDateTime.parse(item.get("createdAt").s()), // Assuming ISO-8601 format
                LocalDateTime.parse(item.get("updatedAt").s()), // Assuming ISO-8601 format
                item.get("active").bool()
        );
    }

}
