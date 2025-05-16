package gr.ichrist.resource;

import gr.ichrist.model.dto.EventDto;
import gr.ichrist.service.EventService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/event/v1/")
public class EventResource {
    private final EventService eventService;

    @Inject
    public EventResource(EventService eventService) {
        this.eventService = eventService;
    }

    @POST
    @Path("/event")
    @Consumes(MediaType.APPLICATION_JSON)
    public Uni<RestResponse<Object>> createEvent(@Valid EventDto eventDto) {
        return eventService.handleEvent(eventDto);
    }
}
