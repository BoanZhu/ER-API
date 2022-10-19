package com.ic.er.bean.vo;

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

    private Long parentId;

    private List<EntityVO> entityList;

    private List<RelationshipVO> relationshipDOList;

    private ResultState resultState;
}
