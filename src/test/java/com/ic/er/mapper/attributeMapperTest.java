package com.ic.er.mapper;

import com.ic.er.ER;
import com.ic.er.entity.AttributeDO;
import com.ic.er.common.DataType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

import java.io.IOException;
import java.util.List;


/**
 * @author jie
 * @data 18/10/2022
 */
public class attributeMapperTest {
    @Before
    public void init() throws Exception {
        ER.connectDB();
        ER.createTables();
    }

    @Test
    public void insertAttribute() {

        Long entityID = Long.valueOf(456);

        Long viewID = Long.valueOf(789);

        String name = "a";

        String dataType = "int";

        int isPrimary = 0;

        int isForeign = 1;

        int isDelete = 0;

        Date gmtCreate = new Date();

        Date gmtModified = new Date();


        AttributeDO attributeDO = new AttributeDO(0L, entityID, viewID, name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);
        AttributeDO attributeDO2 = new AttributeDO(0L, entityID, viewID, name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);
        AttributeDO attributeDO3 = new AttributeDO(0L, entityID, viewID, name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);

        int ret = ER.attributeMapper.insert(attributeDO);
        int ret2 = ER.attributeMapper.insert(attributeDO2);
        int ret3 = ER.attributeMapper.insert(attributeDO3);
        System.out.printf("ret: %d, ID: %d\n", ret, attributeDO.getID());
        System.out.printf("ret2: %d, ID: %d\n", ret2, attributeDO2.getID());
        System.out.printf("ret3: %d, ID: %d\n", ret3, attributeDO3.getID());
    }

    @Test
    public void selectByAttributeTest() {
        Long entityID = 456L;

        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        List<AttributeDO> attributeDOList = ER.attributeMapper.selectByAttribute(attributeDO);
        Assert.assertEquals(attributeDOList.size(), 1);
        Assert.assertEquals(attributeDOList.get(0).getEntityID(), entityID);

        attributeDOList = ER.attributeMapper.selectByAttribute(new AttributeDO(attributeDO.getID()));
        Assert.assertEquals(attributeDOList.size(), 1);
        Assert.assertEquals(attributeDOList.get(0).getEntityID(), entityID);
    }

    @Test
    public void selectByIDTest() {
        Long entityID = 456L;
        Long newEntityID = 789L;

        // create
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        Assert.assertEquals(attributeDO.getEntityID(), entityID);

        AttributeDO aDo = ER.attributeMapper.selectByID(attributeDO.getID());
        Assert.assertNotNull(aDo);
    }

    @Test
    public void updateByIDTest() {
        Long entityID = 456L;
        Long newEntityID = 789L;

        // create
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        Assert.assertEquals(attributeDO.getEntityID(), entityID);

        // update
        attributeDO.setEntityID(newEntityID);
        ER.attributeMapper.updateByID(attributeDO);

        // query
        List<AttributeDO> attributeDOList = ER.attributeMapper.selectByAttribute(attributeDO);
        Assert.assertEquals(attributeDOList.size(), 1);
        Assert.assertEquals(attributeDOList.get(0).getEntityID(), newEntityID);
    }

    @Test
    public void deleteByIDTest() {
        Long entityID = 456L;

        // create
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        Assert.assertEquals(attributeDO.getEntityID(), entityID);

        // delete
        ER.attributeMapper.deleteByID(attributeDO.getID());

        // query to verify
        List<AttributeDO> attributeDOList = ER.attributeMapper.selectByAttribute(attributeDO);
        Assert.assertEquals(attributeDOList.size(), 0);
    }

}


