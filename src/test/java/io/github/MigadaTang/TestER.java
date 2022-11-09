package io.github.MigadaTang;

import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.DataType;
import io.github.MigadaTang.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestER {

    @Before
    public void setUp() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void createSchemaTest() {
        Schema testSchema = ER.createSchema("testSchema", "wt22");
        Assert.assertNotNull(ER.querySchemaByID(testSchema.getID()));
    }

    @Test
    public void deleteSchemaTest() {
        Schema testSchema = ER.createSchema("testSchema", "wt22");
        Assert.assertNotEquals(ER.queryAllSchema().size(), 0);
        ER.deleteSchema(testSchema);
        assertThrows(ERException.class, () -> ER.querySchemaByID(testSchema.getID()));
    }

    @Test
    public void drawER() throws IOException {
        Schema example = ER.createSchema("BranchAccountMovement", "");

        Entity branch = example.addEntity("branch");
        branch.addAttribute("sortcode", DataType.INT, true, false);
        branch.addAttribute("bname", DataType.VARCHAR, false, false);
        branch.addAttribute("cash", DataType.DOUBLE, false, false);

        Entity account = example.addEntity("account");
        account.addAttribute("no", DataType.INT, true, false);
        account.addAttribute("type", DataType.CHAR, false, false);
        account.addAttribute("cname", DataType.VARCHAR, false, false);
        account.addAttribute("rate", DataType.DOUBLE, false, true);

        Entity movement = example.addEntity("movement");
        movement.addAttribute("mid", DataType.INT, true, false);
        movement.addAttribute("amount", DataType.DOUBLE, false, false);
        movement.addAttribute("tdate", DataType.DATETIME, false, false);

        Relationship holds = example.createRelationship("holds", account, branch, Cardinality.OneToOne, Cardinality.ZeroToMany);
        Relationship has = example.createRelationship("has", account, movement, Cardinality.ZeroToMany, Cardinality.OneToOne);

        String jsonString = example.toJSON();
        FileWriter myWriter = new FileWriter(String.format("%s.json", example.getName()));
        myWriter.write(jsonString);
        myWriter.close();
    }

    @Test
    public void loadFromJSONTest() throws IOException {
        String jsonString = Files.readString(Path.of("BranchAccountMovement.json"), Charset.defaultCharset());
        Schema schema = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(schema);
    }
}
