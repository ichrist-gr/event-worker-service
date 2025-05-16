# Project Overview
This project demonstrates how to implement the Outbox Pattern using **Quarkus**, **PostgreSQL**, **Apache Kafka**, and **Debezium**. 
It showcases how a simple insert into the database triggers an event-driven flow — with Debezium capturing the change and forwarding it to Kafka automatically. 
A clean and reliable way to decouple event publishing from your core application logic.

## Configuration information:

[`docker-compose.yml`](./src/main/docker/docker-compose.yml) : This file sets up the essential components needed for
implementing the Debezium Outbox Pattern using PostgreSQL and Kafka.

The Debezium Connect service runs the Debezium connector that streams database changes to Kafka. It depends on Kafka and
PostgreSQL being up and running. This service connects to Kafka at kafka:9092 and uses dedicated Kafka topics for
storing configuration, offsets, and status information, named my_connect_configs, my_connect_offsets, and
my_connect_statuses respectively. The key and value converters are set to handle plain strings and JSON without schemas,
simplifying payload handling. Debezium Connect exposes a REST API on port 8083 to manage connectors, and it loads the
PostgreSQL connector configuration from a mounted JSON file named debezium-postgres-connector.json.

[`debezium-postgres-connector.json`](./src/main/docker/debezium-postgres-connector.json) : This JSON file configures the
Debezium PostgreSQL connector, which captures changes from your Postgres database and streams them to Kafka

The connector is set to only capture changes from the public.outbox_event table, with no initial snapshot (snapshot.mode
set to never). It automatically creates a publication called debezium_publication but only for the filtered table.
The EventRouter transform reads the aggregate_type field in each event and routes the event to a Kafka topic named after
that value (using ${routedByValue}).
For serialization, keys are simple strings and values are JSON without schema info. Finally, connector offsets (tracking
what data it has processed) are stored in a local file /kafka/connect/connect.offsets

## Steps to Set Up:

1. **Navigate to Docker Folder**
    - Open your terminal and **cd** into the `src/main/docker` directory.
      ```bash
      cd src/main/docker
      ```

2. **Start Docker Containers**
    - Run the following command to bring up the Docker containers in detached mode (`-d`).
      ```bash
      docker-compose up -d
      ```
    - This will start the necessary containers for your environment (such as Kafka, Zookeeper, and others).

3. **Wait for Containers to Start**
    - Allow a few moments for all the Docker containers to initialize properly.
    - When the docker container starts, then start also your application

4. **Configure Debezium PostgreSQL Connector**
    - When the service is up, open **Git Bash** and execute the following `curl` command to create the Debezium
      PostgreSQL connector:
      ```bash
      curl -i -X POST \
           -H "Content-Type: application/json" \
           http://localhost:8083/connectors \
           -d @debezium-postgres-connector.json
      ```
    - This command posts the JSON configuration (`debezium-postgres-connector.json`) to Kafka Connect to create the
      Debezium connector.

5. **Check Connector Status**
    - After a few seconds, check the status of the connector by running the following command:
      ```bash
      curl http://localhost:8083/connectors/postgres-connector/status
      ```
    - This will return the current status of your connector (e.g., whether it is running successfully or there is an
      error).

## Run the application:

1. **Trigger the API**

You can trigger the event by sending a `POST` request like the example below:
```bash
   curl --location 'http://localhost:8080/event/v1/event' \
     --header 'Content-Type: application/json' \
     --data '{
       "eventId": "45654",
       "description": "A sample order triggered by an API call.",
       "eventType": "ORDER",
       "eventStatus": "PENDING",
       "eventTime": "2025-05-09T10:15:30Z",
       "priority": "High",
       "source": "service",
       "topic": "custom_topic",
       "payload": "{\"orderId\": \"45654\", \"orderStatus\": \"PENDING\"}"
     }'
   ```

2. **Verify Topic Creation in Kafka**
    - To verify that the Kafka topic has been created for capturing changes, you can check the topics in your Kafka
      management tool.
        - **Note**: Kafka topics are lazily created. This means that the topic is created only after a message is
          published to it.
        - You may need to publish a test message to Kafka for the topic to appear.
---

## Extra Commands and Information:

### 1. **Show Current WAL Level in PostgreSQL:**

- Run the following SQL to check the current write-ahead log (WAL) level in your PostgreSQL instance:
  ```sql
  SHOW wal_level;
  ```

### 2. **Delete the Connector:**

- If you need to delete the Debezium PostgreSQL connector, you can use the following `curl` command:
  ```bash
  curl -X DELETE http://localhost:8083/connectors/postgres-connector
  ```

---