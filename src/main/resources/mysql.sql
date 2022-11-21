DROP TABLE IF EXISTS attribute;
CREATE TABLE attribute (
                           id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the attribute',
                           belong_obj_id bigint NOT NULL COMMENT 'related entity id',
                           belong_obj_type smallint NOT NULL COMMENT 'the type of obj the attribute belongs to',
                           schema_id bigint NOT NULL COMMENT 'related schema id',
                           name varchar(255) NOT NULL COMMENT 'attribute name',
                           data_type varchar(50) NOT NULL COMMENT 'data type of this attribute',
                           attribute_type smallint NOT NULL COMMENT 'type of attribute, 0-unknown, 1-mandatory, 2-optional, 3-multi-valued, 4-both optional and multi-valued',
                           is_primary tinyint NOT NULL DEFAULT 0 COMMENT '0-not a primary key, 1-primary key, default 0',
                           aim_port smallint NULL COMMENT 'the port index of another side of the edge to connect attribute',
                           is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
                           gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                           gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
                           PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS entity;
CREATE TABLE entity (
                          id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the entity',
                          name varchar(255) NOT NULL COMMENT 'attribute name',
                          schema_id bigint NOT NULL COMMENT 'related schema id',
                          entity_type smallint NOT NULL COMMENT 'the type of the entity, 0-unknown, 1-strong entity, 2-weak entity, 3-subset',
                          belong_strong_entity_id bigint NULL COMMENT 'the strong entity this entity belongs to, if this is a subset or weak entity',
                          aim_port smallint NULL COMMENT 'the port index of the entity connect to this sub-entity',
                          is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
                          gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                          gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
                          PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS relationship;
CREATE TABLE relationship (
                                id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the relationship between entities',
                                name varchar(50) NULL COMMENT 'the name of relation',
                                schema_id bigint NOT NULL COMMENT 'related schema id',
                                is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
                                gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                                gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
                                PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS relationship_edge;
CREATE TABLE relationship_edge (
                                     id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the edge',
                                     relationship_id bigint NOT NULL COMMENT 'related schema id',
                                     schema_id bigint NOT NULL COMMENT 'related schema id',
                                     belong_obj_id bigint NOT NULL COMMENT 'belong_obj_id of the other end',
                                     belong_obj_type smallint NOT NULL COMMENT 'belong_obj_type of the other end',
                                     cardinality smallint NOT NULL COMMENT 'look here cardinality, 0-unknown, 1-0:1, 2-0:N, 3-1:1, 4-1:N',
                                     port_at_relationship smallint NULL COMMENT 'the port index of relationship the edge connects to',
                                     port_at_entity smallint NULL COMMENT 'the port index of entity the edge connects to',
                                     is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
                                     gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                                     gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
                                     PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS schema;
CREATE TABLE schema (
                          id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the ER model',
                          name varchar(255) NOT NULL COMMENT 'name of the ER model',
                          creator varchar(255) NULL DEFAULT NULL COMMENT 'name of the ER model',
                          parent_id bigint NULL DEFAULT 0 COMMENT 'parent schema id',
                          is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
                          gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
                          gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
                          PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS layout_info;
CREATE TABLE layout_info (
                               id bigint NOT NULL AUTO_INCREMENT COMMENT 'layout id',
                               belong_obj_id bigint NOT NULL COMMENT 'belong object id',
                               belong_obj_type smallint NOT NULL COMMENT 'type of the belong object, 0: Unknown, 1: Attribute, 2: Entity, 3: Relationship',
                               layout_x NUMERIC(8,3) NOT NULL COMMENT 'x position on the schema',
                               layout_y NUMERIC(8,3) NOT NULL COMMENT 'y position on the schema',
                               width NUMERIC(8,3) NOT NULL COMMENT 'the width of object',
                               height NUMERIC(8,3) NOT NULL COMMENT 'the height of object',
                               PRIMARY KEY (`id`)
);