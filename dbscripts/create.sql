CREATE TABLE event_types
(
  type_id integer NOT NULL,
  type_name character varying(12),
  CONSTRAINT event_types_pkey PRIMARY KEY (type_id)
);

CREATE TABLE events
(
  id serial NOT NULL,
  company character varying(100),
  type_id smallint,
  CONSTRAINT events_pkey PRIMARY KEY (id),
  CONSTRAINT events_type_id_fkey FOREIGN KEY (type_id)
      REFERENCES event_types (type_id) MATCH SIMPLE
      ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT events_id_type_id_key UNIQUE (id, type_id)
);