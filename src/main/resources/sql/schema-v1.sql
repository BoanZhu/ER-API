DROP TABLE IF EXISTS `attribute`;
CREATE TABLE attribute (
    id bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the attribute',
    entity_id bigint NOT NULL COMMENT 'related entity id',
    view_id bigint NOT NULL COMMENT 'related view id',
    name varchar(255) NOT NULL COMMENT 'attribute name',
    data_type varchar(50) NOT NULL COMMENT 'attribute type',
    is_primary tinyint NOT NULL DEFAULT 0 COMMENT '0-not a primary key, 1-primary key, default 0',
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
    `first_cardinality` smallint NOT NULL COMMENT '0-unknown, 1-0:1, 2-0:N, 3-1:1, 4-1:N',
    `second_cardinality` smallint NOT NULL COMMENT '0-unknown, 1-0:1, 2-0:N, 3-1:1, 4-1:N',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `view`;
CREATE TABLE `view` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'uuid of the ER model',
    `name` varchar(255) NOT NULL COMMENT 'name of the ER model',
    `creator` varchar(255) NULL DEFAULT NULL COMMENT 'name of the ER model',
    `parent_id` bigint NULL DEFAULT 0 COMMENT 'parent view id',
    `is_delete` tinyint NOT NULL DEFAULT 0 COMMENT '0-undeleted，1-delete，default 0',
    `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
    `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Modified time',
    PRIMARY KEY (`id`)
);

DROP TABLE IF EXISTS `layout_info`;
CREATE TABLE `layout_info` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'layout id',
    `related_obj_id` bigint NOT NULL COMMENT 'related object id',
    `related_obj_type` smallint NOT NULL COMMENT 'type of the related object, 0: Unknown, 1: Attribute, 2: Entity, 3: Relationship',
    `layout_x` NUMERIC(8,3) NOT NULL COMMENT 'x position on the view',
    `layout_y` NUMERIC(8,3) NOT NULL COMMENT 'y position on the view',
    `width` NUMERIC(8,3) NOT NULL COMMENT 'the width of object',
    `height` NUMERIC(8,3) NOT NULL COMMENT 'the height of object',
    PRIMARY KEY (`id`)
);