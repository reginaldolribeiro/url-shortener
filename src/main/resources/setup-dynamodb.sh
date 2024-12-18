#!/bin/bash

# Set AWS Region and Profile (customize these if needed)
AWS_REGION="us-east-1"
AWS_PROFILE="developer"  # Replace "developer" with your AWS profile name if needed

echo "Starting DynamoDB table creation and data insertion..."

# Function to create a table and wait until it is active, then insert sample data
create_table_and_insert_data() {
    local TABLE_NAME=$1
    local CREATE_COMMAND=$2
    local SAMPLE_DATA_COMMAND=$3

    echo "Creating table: $TABLE_NAME..."
    eval "$CREATE_COMMAND"

    echo "Waiting for table $TABLE_NAME to become ACTIVE..."
    aws dynamodb wait table-exists --table-name "$TABLE_NAME" --region "$AWS_REGION" --profile "$AWS_PROFILE"

    echo "Describing table: $TABLE_NAME..."
    aws dynamodb describe-table --table-name "$TABLE_NAME" --region "$AWS_REGION" --profile "$AWS_PROFILE"

    echo "Inserting sample data into table: $TABLE_NAME..."
    eval "$SAMPLE_DATA_COMMAND"
}

# Create 'User' table and insert sample data
create_table_and_insert_data "User" \
    "aws dynamodb create-table \
        --table-name User \
        --attribute-definitions AttributeName=id,AttributeType=S AttributeName=email,AttributeType=S \
        --key-schema AttributeName=id,KeyType=HASH AttributeName=email,KeyType=RANGE \
        --billing-mode PAY_PER_REQUEST \
        --region $AWS_REGION \
        --profile $AWS_PROFILE" \
    "aws dynamodb put-item \
        --table-name User \
        --item '{
            \"id\": {\"S\": \"user123\"},
            \"email\": {\"S\": \"user@example.com\"},
            \"name\": {\"S\": \"John Doe\"},
            \"createdAt\": {\"S\": \"2023-10-01T10:00:00Z\"},
            \"updateAt\": {\"S\": \"2023-10-01T10:00:00Z\"},
            \"active\": {\"BOOL\": true}
        }' \
        --region $AWS_REGION \
        --profile $AWS_PROFILE"

# Create 'UrlMappings' table and insert sample data
create_table_and_insert_data "UrlMappings" \
    "aws dynamodb create-table \
        --table-name UrlMappings \
        --attribute-definitions AttributeName=shortUrlId,AttributeType=S AttributeName=userId,AttributeType=S \
        --key-schema AttributeName=shortUrlId,KeyType=HASH AttributeName=userId,KeyType=RANGE \
        --billing-mode PAY_PER_REQUEST \
        --region $AWS_REGION \
        --profile $AWS_PROFILE" \
    "aws dynamodb put-item \
        --table-name UrlMappings \
        --item '{
            \"shortUrlId\": {\"S\": \"2cnbJVQ\"},
            \"userId\": {\"S\": \"123e4567-e89b-12d3-a456-426614174000\"},
            \"longUrl\": {\"S\": \"https://example.com\"},
            \"createdAt\": {\"S\": \"2023-10-01T10:00:00Z\"},
            \"updatedAt\": {\"S\": \"2023-10-01T10:00:00Z\"},
            \"clicks\": {\"N\": \"5\"},
            \"active\": {\"BOOL\": true}
        }' \
        --region $AWS_REGION \
        --profile $AWS_PROFILE"

echo "DynamoDB tables and data setup completed successfully!"