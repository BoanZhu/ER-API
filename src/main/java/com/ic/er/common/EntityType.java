package com.ic.er.common;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EntityType {
    NORMAL(0),
    WEAKENTITY(1),
    SUBSET(2);

    private final Integer value;
}
