package io.github.MigadaTang.common;

import io.github.MigadaTang.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public
class EntityWithCardinality {
    Entity entity;
    Cardinality cardinality;
}
