package com.ic.er.dao;

import com.ic.er.bean.entity.GraphInfoDO;
import java.util.List;

public interface GraphInfoMapper {

    GraphInfoDO selectById(Long id);

    GraphInfoDO selectByRelatedObjId(Long relatedObjId);

    GraphInfoDO selectByGraphInfo(GraphInfoDO graphInfoDO);

    int insert(GraphInfoDO graphInfoDO);

    int deleteById(Long id);

    int deleteByObjId(long relatedObjId);

    int updateById(GraphInfoDO graphInfoDO);

    int updateByObjId(GraphInfoDO graphInfoDO);

}
