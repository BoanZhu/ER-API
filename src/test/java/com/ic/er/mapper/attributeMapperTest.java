package com.ic.er.mapper;

import com.ic.er.ER;
import com.ic.er.TestCommon;
import com.ic.er.common.DataType;
import com.ic.er.entity.AttributeDO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;


/**
 * @author jie
 * @data 18/10/2022
 */
public class attributeMapperTest {
    @Before
    public void init() throws Exception {
        ER.initialize(TestCommon.usePostgre);
    }

    @Test
    public void insertAttribute() {

        Long entityID = Long.valueOf(456);

        Long viewID = Long.valueOf(789);

        String name = "a";

        String dataType = "int";

        Boolean isPrimary = true;
        Boolean nullable = false;

        int isDelete = 0;

        Date gmtCreate = new Date();

        Date gmtModified = new Date();


        AttributeDO attributeDO = new AttributeDO(0L, entityID, viewID, name, DataType.INT, isPrimary, nullable, isDelete, gmtCreate, gmtModified);
        AttributeDO attributeDO2 = new AttributeDO(0L, entityID, viewID, name, DataType.INT, isPrimary, nullable, isDelete, gmtCreate, gmtModified);
        AttributeDO attributeDO3 = new AttributeDO(0L, entityID, viewID, name, DataType.INT, isPrimary, nullable, isDelete, gmtCreate, gmtModified);

        int ret = ER.attributeMapper.insert(attributeDO);
        int ret2 = ER.attributeMapper.insert(attributeDO2);
        int ret3 = ER.attributeMapper.insert(attributeDO3);
    }

    @Test
    public void selectByAttributeTest() {
        Long entityID = 456L;

        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, false, true, 0, null, null);
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
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, false, true, 0, null, null);
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
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, false, true, 0, null, null);
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
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, false, true, 0, null, null);
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


