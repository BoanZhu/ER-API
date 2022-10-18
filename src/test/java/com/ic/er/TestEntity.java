package com.ic.er;


import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class TestEntity {

    private Entity entity;

    @Before
    public void setUp() {
        entity = new Entity(0L, "test entity", 10L, new ArrayList<>(), new Date(), new Date());
    }

    @Test
    public void testAddAttribute() {
        try {
            System.out.printf("name: %s, viewID: %d\n", entity.getName(), entity.getViewID());
            Attribute id = entity.addAttribute("id", DataType.INTEGER, 1, 0);
            Attribute age = entity.addAttribute("age", DataType.INTEGER, 0, 0);
            Attribute name = entity.addAttribute("name", DataType.INTEGER, 0, 0);
            Attribute CID = entity.addAttribute("CID", DataType.INTEGER, 0, 1);
            for (int i = entity.getAttributeList().size() - 1; i >= 0; i--) {
                System.out.printf("Attribute name: %s\n", entity.getAttributeList().get(i).getName());
            }
            System.out.println(age.getName());
            System.out.println("--------------------");
            entity.removeAttribute(age);
            for (int i = entity.getAttributeList().size() - 1; i >= 0; i--) {
                System.out.printf("Attribute name: %s\n", entity.getAttributeList().get(i).getName());
            }
        } catch (Throwable ex) {
            Assert.assertEquals(ex.getCause() instanceof NullPointerException, false);
        }
    }
}