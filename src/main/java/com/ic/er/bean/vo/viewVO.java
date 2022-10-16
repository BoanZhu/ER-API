package com.ic.er.bean.vo;

import com.ic.er.bean.entity.Attribute;
import com.ic.er.bean.entity.Entity;
import com.ic.er.bean.entity.Relationship;
import java.util.List;

/**
 * @description input parameter for view service
 * @author wendi
 * @date 16/10/2022
 */
public class viewVO {

    private Long id;

    private String name;

    private String creator;

    private Long parent_id;

    private List<Entity> entityList;

    private List<Relationship> relationshipList;

    private List<Attribute> attributeList;
}
