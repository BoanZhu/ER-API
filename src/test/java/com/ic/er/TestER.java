package com.ic.er;

import com.ic.er.common.Cardinality;
import com.ic.er.common.DataType;
import com.ic.er.exception.ERException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TestER {

    @Before
    public void setUp() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void createViewTest() {
        View testView = ER.createView("testView", "wt22");
        Assert.assertNotNull(ER.queryViewByID(testView.getID()));
    }

    @Test
    public void deleteViewTest() {
        View testView = ER.createView("testView", "wt22");
        Assert.assertNotEquals(ER.queryAllView().size(), 0);
        ER.deleteView(testView);
        assertThrows(ERException.class, () -> ER.queryViewByID(testView.getID()));
    }

    @Test
    public void drawER() throws IOException {
        View example = ER.createView("BranchAccountMovement", "tw");

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

        View view = ER.loadFromJSON(jsonString);
        Assert.assertNotNull(view);
    }
}
