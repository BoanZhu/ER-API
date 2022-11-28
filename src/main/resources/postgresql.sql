DROP TABLE IF EXISTS attribute;
DROP SEQUENCE IF EXISTS attribute_seq;
CREATE SEQUENCE attribute_seq;

CREATE TABLE attribute (
                           id bigint NOT NULL DEFAULT NEXTVAL ('attribute_seq') ,
                           belong_obj_id bigint NOT NULL ,
                           belong_obj_type smallint NOT NULL ,
                           schema_id bigint NOT NULL ,
                           name varchar(255) NOT NULL ,
                           data_type varchar(50) NOT NULL ,
                           attribute_type smallint NOT NULL ,
                           is_primary boolean NOT NULL DEFAULT false ,
                           aim_port smallint NULL ,
                           is_delete smallint NOT NULL DEFAULT 0 ,
                           gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                           gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                           PRIMARY KEY (id)
);

DROP TABLE IF EXISTS entity;
DROP SEQUENCE IF EXISTS entity_seq;
CREATE SEQUENCE entity_seq;

CREATE TABLE entity (
                        id bigint NOT NULL DEFAULT NEXTVAL ('entity_seq') ,
                        name varchar(255) NOT NULL ,
                        schema_id bigint NOT NULL ,
                        entity_type smallint NOT NULL ,
                        belong_strong_entity_id bigint NULL ,
                        aim_port smallint NULL ,
                        is_delete smallint NOT NULL DEFAULT 0 ,
                        gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                        gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                        PRIMARY KEY (id)
);

DROP TABLE IF EXISTS relationship;
DROP SEQUENCE IF EXISTS relationship_seq;
CREATE SEQUENCE relationship_seq;

CREATE TABLE relationship (
                              id bigint NOT NULL DEFAULT NEXTVAL ('relationship_seq') ,
                              name varchar(50) NULL ,
                              schema_id bigint NOT NULL ,
                              is_delete smallint NOT NULL DEFAULT 0 ,
                              gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                              gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                              PRIMARY KEY (id)
);

DROP TABLE IF EXISTS relationship_edge;
DROP SEQUENCE IF EXISTS relationship_edge_seq;
CREATE SEQUENCE relationship_edge_seq;

CREATE TABLE relationship_edge (
                                   id bigint NOT NULL DEFAULT NEXTVAL ('relationship_edge_seq') ,
                                   relationship_id bigint NOT NULL ,
                                   schema_id bigint NOT NULL ,
                                   belong_obj_id bigint NOT NULL ,
                                   belong_obj_type smallint NOT NULL ,
                                   cardinality smallint NOT NULL ,
                                   is_key boolean NOT NULL DEFAULT false,
                                   port_at_relationship smallint NULL ,
                                   port_at_entity smallint NULL ,
                                   is_delete smallint NOT NULL DEFAULT 0 ,
                                   gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                                   gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                                   PRIMARY KEY (id)
);

DROP TABLE IF EXISTS schema;
DROP SEQUENCE IF EXISTS schema_seq;
CREATE SEQUENCE schema_seq;

CREATE TABLE schema (
                        id bigint NOT NULL DEFAULT NEXTVAL ('schema_seq') ,
                        name varchar(255) NOT NULL ,
                        is_delete smallint NOT NULL DEFAULT 0 ,
                        gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                        gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ,
                        PRIMARY KEY (id)
);

DROP TABLE IF EXISTS layout_info;
DROP SEQUENCE IF EXISTS layout_info_seq;
CREATE SEQUENCE layout_info_seq;

CREATE TABLE layout_info (
                             id bigint NOT NULL DEFAULT NEXTVAL ('layout_info_seq') ,
                             belong_obj_id bigint NOT NULL,
                             belong_obj_type smallint NOT NULL,
                             layout_x NUMERIC(8,3) NOT NULL ,
                             layout_y NUMERIC(8,3) NOT NULL ,
                             PRIMARY KEY (id)
);