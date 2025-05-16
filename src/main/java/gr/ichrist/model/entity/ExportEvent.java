package gr.ichrist.model.entity;

import com.fasterxml.jackson.databind.JsonNode;
import io.debezium.outbox.quarkus.ExportedEvent;

import java.time.Instant;
import java.util.Map;

public class ExportEvent implements ExportedEvent<String, JsonNode> {
    private final JsonNode jsonNode;

    public ExportEvent(JsonNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    @Override
    public String getAggregateId() {
        return this.jsonNode.get("eventId").asText();
    }

    @Override
    public String getAggregateType() {
        return this.jsonNode.get("topic").asText();
    }

    @Override
    public String getType() {
        return this.jsonNode.get("eventType").asText();
    }

    @Override
    public Instant getTimestamp() {
        return Instant.parse(this.jsonNode.get("eventTime").asText());
    }

    @Override
    public JsonNode getPayload() {
        return this.jsonNode;
    }

    @Override
    public Map<String, Object> getAdditionalFieldValues() {
        return Map.of(
                "event_status", this.jsonNode.get("eventStatus").asText(),
                "description", this.jsonNode.get("description").asText(),
                "priority", this.jsonNode.get("priority").asText(),
                "source", this.jsonNode.get("source").asText()
        );
    }
}
