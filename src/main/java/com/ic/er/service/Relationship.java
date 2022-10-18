package com.ic.er.service;

import com.ic.er.bean.dto.RelationshipDTO;
import com.ic.er.bean.vo.RelationshipVO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.ResultState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Relationship {
    private Long ID;
    private String name;
    private Long viewID;
    private Long firstEntityID;
    private Long secondEntityID;
    private Long firstAttributeID;
    private Long secondAttributeID;
    private Cardinality cardinality;
    private int isDelete;
    private Date gmtCreate;
    private Date gmtModified;

    RelationshipVO insertDB() {
        return null;
    }

    ResultState deleteDB() {
        return null;
    }

    ResultState updateDB() {
        return null;
    }
}
