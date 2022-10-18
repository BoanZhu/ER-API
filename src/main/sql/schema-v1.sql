--CREATE DATABASE amazingER;

DROP TABLE IF EXISTS `attributeDO`;
CREATE TABLE attributeDO (
    id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the attributeDO',
    entity_id bigint NOT NULL COMMENT 'related entityDO id',
    view_id bigint NOT NULL COMMENT 'related viewDO id',
    name varchar(255) NOT NULL COMMENT 'attributeDO name',
    data_type varchar(50) NOT NULL COMMENT 'attributeDO type',
    is_primary tinyint NOT NULL DEFAULT 0 COMMENT '0-not a primary key, 1-primary key, default 0',
    is_foreign tinyint NOT NULL DEFAULT 0 COMMENT '0-not a foreign key, 1-foreign key, default 0',
    is_delete tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    gmt_create datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    gmt_modified datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `entityDO`;
CREATE TABLE `entityDO` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the entityDO',
    `name` varchar(255) NOT NULL COMMENT 'attributeDO name',
    `view_id` bigint NOT NULL COMMENT 'related viewDO id',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `relationshipDO`;
CREATE TABLE `relationshipDO` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the relationshipDO between entities',
    `name` varchar(50) NULL COMMENT 'the name of relation',
    `view_id` bigint NOT NULL COMMENT 'related viewDO id',
    `first_entity_id` bigint NOT NULL COMMENT 'the first entityDO in the relationshipDO',
    `second_entity_id` bigint NOT NULL COMMENT 'the second entityDO in the relationshipDO',
    `first_attribute_id` bigint NOT NULL COMMENT 'the first attributeDO in the relationshipDO',
    `second_attribute_id` bigint NOT NULL COMMENT 'the second attributeDO in the relationshipDO',
    `cardinality` smallint NOT NULL COMMENT '0-one to one, 1-one to many, 2-many to many, 3-many to one',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create times',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `viewDO`;
CREATE TABLE `viewDO` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the ER model',
    `name` varchar(255) NOT NULL COMMENT 'name of the ER model',
    `creator` varchar(255) NULL DEFAULT NULL COMMENT 'name of the ER model',
    `parent_id` bigint NULL DEFAULT NULL COMMENT 'parent viewDO id',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);