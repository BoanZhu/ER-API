package com.ic.er.mapper;

import com.ic.er.ER;
import com.ic.er.bean.entity.AttributeDO;
import com.ic.er.bean.entity.EntityDO;
import com.ic.er.common.DataType;
import com.ic.er.dao.AttributeMapper;
import com.ic.er.dao.EntityMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;


/**
 *
 * @author jie
 * @data 18/10/2022
 *

 *
 */
public class attributeMapperTest {
    @Before
    public void init() throws IOException {
        try {
            ER.connectDB();
            ER.createTables();
        } catch (SQLException e) {
            System.out.println(e);
            throw new RuntimeException(e);
        }
    }

    @Test
    public void insertAttribute() {

        Long entityId = Long.valueOf(456);

        Long viewId = Long.valueOf(789);

        String name = "a";

        String dataType = "int";

        int isPrimary = 0;

        int isForeign = 1;

        int isDelete = 0;

        Date gmtCreate = new Date();

        Date gmtModified = new Date();


        AttributeDO attributeDO = new AttributeDO(0L, entityId, viewId, name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);
        AttributeDO attributeDO2 = new AttributeDO(0L, entityId, viewId, name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);
        AttributeDO attributeDO3 = new AttributeDO(0L, entityId, viewId, name, DataType.INTEGER, isPrimary, isForeign, isDelete, gmtCreate, gmtModified);

        int ret = ER.attributeMapper.insert(attributeDO);
        int ret2 = ER.attributeMapper.insert(attributeDO2);
        int ret3 = ER.attributeMapper.insert(attributeDO3);
        System.out.printf("ret: %d, ID: %d\n", ret, attributeDO.getId());
        System.out.printf("ret2: %d, ID: %d\n", ret2, attributeDO2.getId());
        System.out.printf("ret3: %d, ID: %d\n", ret3, attributeDO3.getId());
    }
    @Test
    public void selectByAttributeTest() {
        Long entityId = 456L;

        AttributeDO attributeDO = new AttributeDO(0L, entityId, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        List<AttributeDO> attributeDOList = ER.attributeMapper.selectByAttribute(attributeDO);
        Assert.assertEquals(attributeDOList.size(), 1);
        Assert.assertEquals(attributeDOList.get(0).getEntityId(), entityId);

        attributeDOList = ER.attributeMapper.selectByAttribute(new AttributeDO(attributeDO.getId()));
        Assert.assertEquals(attributeDOList.size(), 1);
        Assert.assertEquals(attributeDOList.get(0).getEntityId(), entityId);
    }
    @Test
    public void selectByIdTest(){
        Long entityID = 456L;
        Long newEntityID = 789L;

        // create
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        Assert.assertEquals(attributeDO.getEntityId(), entityID);

        AttributeDO aDo = ER.attributeMapper.selectById(attributeDO.getId());
        Assert.assertNotNull(aDo);
    }
    @Test
    public void updateByIdTest(){
        Long entityID = 456L;
        Long newEntityID = 789L;

        // create
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        Assert.assertEquals(attributeDO.getEntityId(), entityID);

        // update
        attributeDO.setEntityId(newEntityID);
        ER.attributeMapper.updateById(attributeDO);

        // query
        List<AttributeDO> attributeDOList = ER.attributeMapper.selectByAttribute(attributeDO);
        Assert.assertEquals(attributeDOList.size(), 1);
        Assert.assertEquals(attributeDOList.get(0).getEntityId(), newEntityID);
    }
    @Test
    public void deleteByIdTest(){
        Long entityID = 456L;

        // create
        AttributeDO attributeDO = new AttributeDO(0L, entityID, 789L, "abc", DataType.VARCHAR, 1, 1, 0, null, null);
        int ret = ER.attributeMapper.insert(attributeDO);
        Assert.assertEquals(ret, 1);
        Assert.assertEquals(attributeDO.getEntityId(), entityID);

        // delete
        ER.attributeMapper.deleteById(attributeDO.getId());

        // query to verify
        List<AttributeDO> attributeDOList = ER.attributeMapper.selectByAttribute(attributeDO);
        Assert.assertEquals(attributeDOList.size(), 0);
    }

}


