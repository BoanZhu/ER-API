package com.ic.er.dao;

import com.ic.er.common.RelatedObjType;
import com.ic.er.entity.LayoutInfoDO;

import java.util.List;

public interface LayoutInfoMapper {

    LayoutInfoDO selectByID(Long ID);

    LayoutInfoDO selectByRelatedObjID(Long relatedObjID, RelatedObjType relatedObjType);

    List<LayoutInfoDO> selectByLayoutInfo(LayoutInfoDO layoutInfoDO);

    int insert(LayoutInfoDO layoutInfoDO);

    int updateByID(LayoutInfoDO layoutInfoDO);

    int updateByObjID(LayoutInfoDO layoutInfoDO);

}
