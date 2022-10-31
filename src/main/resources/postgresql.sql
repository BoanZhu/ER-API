DROP TABLE IF EXISTS attribute;
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE SEQUENCE attribute_seq;

CREATE TABLE attribute (
                           id bigint NOT NULL DEFAULT NEXTVAL ('attribute_seq'),
                           entity_id bigint NOT NULL,
                           view_id bigint NOT NULL,
                           name varchar(255) NOT NULL,
                           data_type varchar(50) NOT NULL,
                           is_primary smallint NOT NULL DEFAULT 0,
                           is_delete smallint NOT NULL DEFAULT 0,
                           gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (id)
);

DROP TABLE IF EXISTS entity;
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE SEQUENCE entity_seq;

CREATE TABLE entity (
                        id bigint NOT NULL DEFAULT NEXTVAL ('entity_seq'),
                        name varchar(255) NOT NULL,
                        view_id bigint NOT NULL,
                        is_delete smallint NOT NULL DEFAULT 0,
                        gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        PRIMARY KEY (id)
);

DROP TABLE IF EXISTS relationship;
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE SEQUENCE relationship_seq;

CREATE TABLE relationship (
                              id bigint NOT NULL DEFAULT NEXTVAL ('relationship_seq'),
                              name varchar(50) NULL,
                              view_id bigint NOT NULL,
                              first_entity_id bigint NOT NULL,
                              second_entity_id bigint NOT NULL,
                              first_cardinality smallint NOT NULL,
                              second_cardinality smallint NOT NULL,
                              is_delete smallint NOT NULL DEFAULT 0,
                              gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              PRIMARY KEY (id)
);

DROP TABLE IF EXISTS view;
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE SEQUENCE view_seq;

CREATE TABLE view (
                      id bigint NOT NULL DEFAULT NEXTVAL ('view_seq'),
                      name varchar(255) NOT NULL,
                      creator varchar(255) NULL DEFAULT NULL,
                      parent_id bigint NULL DEFAULT 0,
                      is_delete smallint NOT NULL DEFAULT 0,
                      gmt_create timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      gmt_modified timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      PRIMARY KEY (id)
);

DROP TABLE IF EXISTS layout_info;
-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE SEQUENCE layout_info_seq;

CREATE TABLE layout_info (
                             id bigint NOT NULL DEFAULT NEXTVAL ('layout_info_seq'),
                             related_obj_id bigint NOT NULL,
                             related_obj_type smallint NOT NULL,
                             layout_x NUMERIC(8,3) NOT NULL,
                             layout_y NUMERIC(8,3) NOT NULL,
                             width NUMERIC(8,3) NOT NULL,
                             height NUMERIC(8,3) NOT NULL,
                             PRIMARY KEY (id)
);