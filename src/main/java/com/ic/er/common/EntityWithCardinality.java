package com.ic.er.common;

import com.ic.er.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public
class EntityWithCardinality {
    Entity entity;
    Cardinality cardinality;
}
