--CREATE DATABASE amazingER;

DROP TABLE IF EXISTS `attribute`;
CREATE TABLE attribute (
    id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the attribute',
    entity_id bigint NOT NULL COMMENT 'related entity id',
    view_id bigint NOT NULL COMMENT 'related view id',
    name varchar(255) NOT NULL COMMENT 'attribute name',
    data_type varchar(50) NOT NULL COMMENT 'attribute type',
    is_primary tinyint NOT NULL DEFAULT 0 COMMENT '0-not a primary key, 1-primary key, default 0',
    is_foreign tinyint NOT NULL DEFAULT 0 COMMENT '0-not a foreign key, 1-foreign key, default 0',
    is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `entity`;
CREATE TABLE `entity` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the entity',
    `name` varchar(255) NOT NULL COMMENT 'attribute name',
    `view_id` bigint NOT NULL COMMENT 'related view id',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `relationship`;
CREATE TABLE `relationship` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the relationship between entities',
    `name` varchar(50) NULL COMMENT 'the name of relation',
    `view_id` bigint NOT NULL COMMENT 'related view id',
    `first_entity_id` bigint NOT NULL COMMENT 'the first entity in the relationship',
    `second_entity_id` bigint NOT NULL COMMENT 'the second entity in the relationship',
    `first_attribute_id` bigint NOT NULL COMMENT 'the first attribute in the relationship',
    `second_attribute_id` bigint NOT NULL COMMENT 'the second attribute in the relationship',
    `cardinality` smallint NOT NULL COMMENT '0-one to one, 1-one to many, 2-many to many, 3-many to one',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create times',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `view`;
CREATE TABLE `view` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the ER model',
    `name` varchar(255) NOT NULL COMMENT 'name of the ER model',
    `creator` varchar(255) NULL DEFAULT NULL COMMENT 'name of the ER model',
    `parent_id` bigint NULL DEFAULT NULL COMMENT 'parent view id',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);