package com.ic.er.common;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum Cardinality {
    Unknown(0),
    OneToOne(1),
    OneToMany(2),
    ManyToOne(3),
    ManyToMany(4);

    private final Integer value;
}
