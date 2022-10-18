package com.ic.er.service;

import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class Attribute {
    private Long ID;
    private Long entityID;
    private Long viewID;
    private String name;
    private DataType dataType;
    private int isPrimary;
    private int isForeign;
    private Date gmtCreate;
    private Date gmtModified;

    public Attribute(Long ID, Long entityID, Long viewID, String name, DataType dataType,
                     int isPrimary, int isForeign) {
        this.ID = ID;
        this.entityID = entityID;
        this.viewID = viewID;
        this.name = name;
        this.dataType = dataType;
        this.isPrimary = isPrimary;
        this.isForeign = isForeign;
        this.gmtCreate = new Date(System.currentTimeMillis());
        this.gmtModified = new Date(System.currentTimeMillis());
    }

    Long insertDB() {
        return null;
    }

    ResultState deleteDB() {
        return null;
    }

    ResultState updateDB() {
        return null;
    }

    public static List<Attribute> queryByAttribute() {
        // call mapper
        return null;
    }

}
