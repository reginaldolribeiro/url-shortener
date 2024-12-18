# 📦 URL Shortener

A simple service to shorten URLs using Java, Spring Boot, Redis, and AWS DynamoDB.

---
## ✅ Functional Requirements

1. **Create Shortened URL**:  
   Given a long URL, the system should generate a shortened URL.

2. **Redirect to Long URL**:  
   Given a shortened URL, the system should redirect to the original long URL.

---

## ⚙️ Non-Functional Requirements

1. **Low Latency**:  
   The system should respond quickly to requests for creating and retrieving shortened URLs.

2. **High Availability**:  
   The system should be reliable and available with minimal downtime.

---

## 🚀 Prerequisites

Before running the project, ensure you have the following installed:

- **Docker & Docker Compose**: [Install Docker](https://docs.docker.com/get-docker/)
- **Maven**: [Install Maven](https://maven.apache.org/install.html)
- **Java 21**: [Install Java](https://adoptopenjdk.net/)
- **AWS CLI**: [Install AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/install-cliv2.html)
- **AWS Credentials**: You need access to AWS DynamoDB with appropriate permissions.  
    ⚠️ **Note**: Using AWS DynamoDB may incur costs. Ensure you are aware of pricing details before proceeding.



### ⚙️ Set Up DynamoDB Tables

**1. Make the Script Executable and Run It**:  
This script creates the required DynamoDB tables and inserts sample data.

```bash
   chmod +x src/main/resources/setup-dynamodb.sh
   ./src/main/resources/setup-dynamodb.sh
```

**2. Verify the Setup:**  
Access the DynamoDB section in the AWS Console and check if the tables (User and UrlMappings) and records were created successfully.
---

## 🛠️ Run the App Locally in Your Terminal

### 1️⃣ Start Docker Services

Run the following Docker Compose command to bring up the required services (Redis):

```bash
docker compose -f src/infra/docker-compose-local.yml up -d
```

### 2️⃣ Start the Spring Boot Project

You can run the Spring Boot project using Maven with the necessary environment variables.

Option 1: Inline Environment Variables
```aiignore
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.arguments="\
    --AWS_REGION=us-east-1 \
    --AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID} \
    --AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY} \
    --NEW_RELIC_LICENSE_KEY=${NEW_RELIC_LICENSE_KEY}"
```
Option 2: Export Environment Variables
```aiignore
export AWS_REGION=us-east-1
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export NEW_RELIC_LICENSE_KEY=your-new-relic-key

./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

## 🐳 Run the App Using Docker Compose

### 1️⃣ Export Environment Variables
```aiignore
export AWS_ACCESS_KEY_ID=your-access-key
export AWS_SECRET_ACCESS_KEY=your-secret-key
export AWS_REGION=us-east-1
export NEW_RELIC_LICENSE_KEY=your-new-relic-key
```

### 2️⃣ Start the Docker Services
```docker compose -f src/infra/docker-compose.yml up --build```

### 3️⃣ Verify the Server is Running

Check the server’s health status by visiting:
```
http://localhost:8080/api/actuator/health
```

## 📚 Project Dependencies
- **Spring Boot:** Java framework for building web applications.
- **Redis:** In-memory data store for caching.
- **AWS DynamoDB:** NoSQL database for storing shortened URLs.