package com.reginaldolribeiro.url_shortener.adapter.repository.user;


import com.reginaldolribeiro.url_shortener.app.domain.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class UserEntity implements Serializable {

    private String id;
    private String name;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean active;

    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @DynamoDbSortKey
    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public static User fromMapping(String id,
                                   String name,
                                   String email,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt,
                                   boolean active) {
        return new User.Builder()
                .id(UUID.fromString(id))
                .name(name)
                .email(email)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .active(active)
                .build();
    }

}
