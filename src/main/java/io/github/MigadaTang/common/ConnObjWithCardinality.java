package io.github.MigadaTang.common;

import io.github.MigadaTang.ERConnectableObj;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public
class ConnObjWithCardinality {
    ERConnectableObj connObj;
    Cardinality cardinality;
}
