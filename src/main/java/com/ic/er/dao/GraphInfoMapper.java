package com.ic.er.dao;

import com.ic.er.entity.GraphInfoDO;

public interface GraphInfoMapper {

    GraphInfoDO selectByID(Long ID);

    GraphInfoDO selectByRelatedObjID(Long relatedObjID);

    GraphInfoDO selectByGraphInfo(GraphInfoDO graphInfoDO);

    int insert(GraphInfoDO graphInfoDO);

    int deleteByID(Long ID);

    int deleteByObjID(long relatedObjID);

    int updateByID(GraphInfoDO graphInfoDO);

    int updateByObjID(GraphInfoDO graphInfoDO);

}
