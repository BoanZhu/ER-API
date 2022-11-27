package io.github.MigadaTang.mapper;

import io.github.MigadaTang.ER;
import io.github.MigadaTang.TestCommon;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author wendi
 * @data 15/10/2022
 */
public class entityMapperTest {
    @BeforeClass
    public static void init() throws Exception {
        TestCommon.setUp();
    }

    @Test
    public void insertEntity() {

//        long id = Long.valueOf(456);
//        String name = "b";
//        long schemaID = Long.valueOf(2234);
//        Date create = new Date();
//        Date modify = new Date();
//        int isDelete = 0;
//
//        EntityDO entity = new EntityDO(id, name, schemaID, isDelete, create, modify);

//        ER.entityMapper.insert(entity);
    }

    @Test
    public void insertEntityTest() {

//        long id = 12334;
//        String name = "b";
//        long schemaID = 789;
//        Date create = new Date();
//        Date modify = new Date();
//        int isDelete = 0;
//
//        EntityDO entity = new EntityDO(id, name, schemaID, isDelete, create, modify);
//
//        ER.entityMapper.insert(entity);
    }

    @Test
    public void selectByEntityTest() {

//        long id = Long.valueOf(12334);
//        String name = null;
//        long schemaID = Long.valueOf(789);
//        Date create = null;
//        Date modify = null;
//        int isDelete = 0;
//
//        EntityDO entity = new EntityDO(id, name, schemaID, isDelete, create, modify);
//
//        ER.entityMapper.selectByEntity(entity);
    }

    @Test
    public void selectByIDTest() {
        Long id = Long.valueOf(123);

        ER.entityMapper.selectByID(id);
    }


    @Test
    public void updateByIDTest() {

//        long id = Long.valueOf(123);
//        String name = "b";
//        long schemaID = Long.valueOf(456);
//        Date create = new Date();
//        Date modify = new Date();
//        int isDelete = 0;
//
//        EntityDO entityDo = new EntityDO(id, name, schemaID, isDelete, create, modify);
//
//        ER.entityMapper.updateByID(entityDo);
    }

    @Test
    public void deleteByIDTest() {

        long id = Long.valueOf(123);

        ER.entityMapper.deleteByID(id);
    }
}
