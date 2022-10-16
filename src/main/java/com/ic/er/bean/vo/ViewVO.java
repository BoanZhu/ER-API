package com.ic.er.bean.vo;

import com.ic.er.bean.entity.Attribute;
import com.ic.er.bean.entity.Entity;
import com.ic.er.bean.entity.Relationship;
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

    private List<Relationship> relationshipList;

    private ResultState resultState;
}
