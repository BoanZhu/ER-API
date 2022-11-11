package com.ic.er.dao;

import com.ic.er.common.BelongObjType;
import com.ic.er.entity.LayoutInfoDO;

import java.util.List;

public interface LayoutInfoMapper {

    LayoutInfoDO selectByID(Long ID);

    LayoutInfoDO selectByBelongObjID(Long relatedObjID, BelongObjType belongObjType);

    List<LayoutInfoDO> selectByLayoutInfo(LayoutInfoDO layoutInfoDO);

    int insert(LayoutInfoDO layoutInfoDO);

    int updateByID(LayoutInfoDO layoutInfoDO);

    int updateByObjID(LayoutInfoDO layoutInfoDO);

}
