package io.github.MigadaTang.dao;

import io.github.MigadaTang.common.RelatedObjType;
import io.github.MigadaTang.entity.LayoutInfoDO;

import java.util.List;

public interface LayoutInfoMapper {

    LayoutInfoDO selectByID(Long ID);

    LayoutInfoDO selectByRelatedObjID(Long relatedObjID, RelatedObjType relatedObjType);

    List<LayoutInfoDO> selectByLayoutInfo(LayoutInfoDO layoutInfoDO);

    int insert(LayoutInfoDO layoutInfoDO);

    int updateByID(LayoutInfoDO layoutInfoDO);

    int updateByObjID(LayoutInfoDO layoutInfoDO);

}
