package com.ic.er.bean.vo;

import com.ic.er.bean.entity.RelationshipDO;
import com.ic.er.common.ResultState;

import java.util.List;

/**
 * @description input parameter for view service
 * @author wendi
 * @date 16/10/2022
 */
public class ViewVO {

    private String name;

    private String creator;

    private Long parent_id;

    private List<EntityVO> entityList;

    private List<RelationshipDO> relationshipDOList;

    private ResultState resultState;
}
