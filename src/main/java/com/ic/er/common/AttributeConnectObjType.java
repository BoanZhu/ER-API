package com.ic.er.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AttributeConnectObjType {
    ENTITY(0),
    RELATIONSHIP(1);

    private final Integer value;
}
