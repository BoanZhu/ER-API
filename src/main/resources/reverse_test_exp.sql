create table new_entity0 (
                             e0_pk int NOT NULL,
                             e0_c1 varchar NOT NULL,
                             e0_c2 varchar NOT NULL,
                             CONSTRAINT pk_new_entity0 PRIMARY KEY (e0_pk)
);

create table new_entity1 (
                             e1_pk int NOT NULL,
                             e1_c1 varchar NOT NULL,
                             CONSTRAINT pk_new_entity1 PRIMARY KEY (e1_pk)
);

create table new_entity2 (
                             e2_pk int NOT NULL,
                             e2_c1 varchar NOT NULL,
                             CONSTRAINT pk_new_entity2 PRIMARY KEY (e2_pk)
);

create table weak_entity (
                             e0_pk int NOT NULL,
                             we_pk int NOT NULL,
                             we_c1 varchar NOT NULL,
                             CONSTRAINT pk_weak_entity PRIMARY KEY (e0_pk, we_pk),
                             CONSTRAINT fk_weak_entity FOREIGN KEY (e0_pk) references new_entity0
);

create table subset (
                        e0_pk int NOT NULL,
                        sub_c1 varchar NOT NULL,
                        CONSTRAINT pk_subset PRIMARY KEY (e0_pk),
                        CONSTRAINT fk_subset FOREIGN KEY (e0_pk) references new_entity0
);

create table nary_relation (
                               e0_pk int NOT NULL,
                               e1_pk int NOT NULL,
                               e2_pk int NOT NULL,
                               CONSTRAINT fk_rel1 FOREIGN KEY (e0_pk) references new_entity0,
                               CONSTRAINT fk_rel2 FOREIGN KEY (e1_pk) references new_entity1,
                               CONSTRAINT fk_rel3 FOREIGN KEY (e2_pk) references new_entity2
)

