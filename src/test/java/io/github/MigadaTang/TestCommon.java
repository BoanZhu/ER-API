package io.github.MigadaTang;

import io.github.MigadaTang.common.RDBMSType;

public class TestCommon {
    public static final boolean usePostgre = true;

    public static void setUp() throws Exception {
        if (usePostgre) {
            ER.initialize(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wh722", "wh722", "4jC@A3528>0N6");
        } else {
            ER.initialize();
        }
    }
}
