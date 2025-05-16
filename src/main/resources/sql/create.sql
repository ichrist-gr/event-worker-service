create table outbox_event
(
    timestamp            timestamp(6) with time zone not null,
    id                   uuid                        not null
        primary key,
    tracing_span_context varchar(256),
    aggregate_id         varchar(255)                not null,
    aggregate_type       varchar(255)                not null,
    description          varchar(255),
    event_status         varchar(255),
    payload              varchar(8000),
    priority             varchar(255),
    source               varchar(255),
    type                 varchar(255)                not null
);