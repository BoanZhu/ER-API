package com.ic.er.mapper;

import com.ic.er.ER;
import com.ic.er.TestCommon;
import com.ic.er.entity.ViewDO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class testViewMapper {
    @Before
    public void init() throws IOException, SQLException {
        ER.initialize(TestCommon.usePostgre);
    }


    @Test
    public void testQueryAllViews() {
        List<ViewDO> viewDOList = ER.viewMapper.selectAll();
        Assert.assertEquals(1, viewDOList.size());
    }

    @Test
    public void testQueryByID() {
        ViewDO viewDO = ER.viewMapper.selectByID(Long.valueOf(3));
        System.out.println(viewDO);
    }

    @Test
    public void testCreateView() {
        ViewDO viewDO = new ViewDO(Long.valueOf(2), "view3", "creator3", Long.valueOf(1), 0, new Date(), new Date());
        Assert.assertEquals(ER.viewMapper.insert(viewDO), 1);
    }

    @Test
    public void testDeleteView() {
        Assert.assertEquals(ER.viewMapper.deleteByID(Long.valueOf(2)), 1);
    }

    @Test
    public void testQueryView() {
        ViewDO viewDO = new ViewDO(null, "view1", null, null, 0, null, null);
        List<ViewDO> res = ER.viewMapper.selectByView(viewDO);
        System.out.println(res);

    }

    @Test
    public void testUpdateView() {
        ViewDO viewDO = new ViewDO(Long.valueOf(3), "view3update", "creator3update", Long.valueOf(1), 0, new Date(), new Date());
        Assert.assertEquals(ER.viewMapper.updateByID(viewDO), 1);
    }
}
