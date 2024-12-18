package com.reginaldolribeiro.url_shortener.adapter.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;

import java.net.URI;

@Configuration
@Slf4j
public class DynamoDbConfig {

    @Value("${aws.dynamodb.endpoint:}")
    private String dynamoDbEndpoint;

    @Value("${aws.dynamodb.region:us-west-2}")
    private String region;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        DynamoDbClientBuilder builder = DynamoDbClient.builder().region(Region.of(region));

        if (isLocal()) {
            builder.endpointOverride(URI.create(dynamoDbEndpoint));
        }

        return builder.build();
    }

    private boolean isLocal() {
        return dynamoDbEndpoint != null && !dynamoDbEndpoint.isEmpty();
    }
}
