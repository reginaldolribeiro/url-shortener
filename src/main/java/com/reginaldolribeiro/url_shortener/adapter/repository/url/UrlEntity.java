package com.reginaldolribeiro.url_shortener.adapter.repository.url;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.io.Serializable;
import java.time.LocalDateTime;

@DynamoDbBean
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class UrlEntity implements Serializable {

    private String shortUrlId;
    private String longUrl;

//    @JsonSerialize(using = LocalDateSerializer.class)
//    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String userId;
    private int clicks;
    private boolean active;

    @DynamoDbPartitionKey
    public String getShortUrlId() {
        return shortUrlId;
    }

    @DynamoDbSortKey
    public String getUserId() {
        return userId;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public int getClicks() {
        return clicks;
    }

    public boolean isActive() {
        return active;
    }
}