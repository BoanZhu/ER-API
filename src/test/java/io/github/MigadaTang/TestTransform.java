package io.github.MigadaTang;

import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.Reverse;
import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.fail;


public class TestTransform {
    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void testRSToERModel() {
        Reverse reverse = new Reverse();
        try {
            Schema schema = reverse.relationSchemasToERModel(RDBMSType.POSTGRESQL, "db.doc.ic.ac.uk", "5432", "wt22",
                    "wt22", "22V**66+C5JPu", "image");
        } catch (ParseException | DBConnectionException | IOException e) {
            fail();
        }
    }
}
