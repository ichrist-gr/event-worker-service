package gr.ichrist.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.ichrist.model.dto.EventDto;
import gr.ichrist.model.entity.ExportEvent;
import io.debezium.outbox.reactive.quarkus.internal.DebeziumOutboxHandler;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

@ApplicationScoped
public class EventService {
    private final DebeziumOutboxHandler debeziumOutboxHandler;
    private final ObjectMapper objectMapper;

    @Inject
    public EventService(DebeziumOutboxHandler debeziumOutboxHandler, ObjectMapper objectMapper) {
        this.debeziumOutboxHandler = debeziumOutboxHandler;
        this.objectMapper = objectMapper;
    }

    @WithTransaction
    public Uni<RestResponse<Object>> handleEvent(EventDto eventDto) {
        Log.info("Publishing event: " + eventDto.eventId());

        ExportEvent exportEvent = new ExportEvent(objectMapper.valueToTree(eventDto));
        return debeziumOutboxHandler.persistToOutbox(exportEvent)
                .onItem()
                .transform(object -> RestResponse.ok());
    }
}
