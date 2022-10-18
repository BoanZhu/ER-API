package com.ic.er.service;

import com.ic.er.bean.dto.AttributeDTO;
import com.ic.er.bean.dto.EntityDTO;
import com.ic.er.bean.vo.EntityVO;
import com.ic.er.common.DataType;
import com.ic.er.common.ResultState;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Entity {
    private Long ID;
    private String name;
    private Long viewID;
    private List<Attribute> attributeList;
    private Date gmtCreate;
    private Date gmtModified;

    public Entity(Long ID, String name, Long viewID) {
        this.ID = ID;
        this.name = name;
        this.viewID = viewID;
        this.attributeList = new ArrayList<>();
        this.gmtCreate = new Date(System.currentTimeMillis());
        this.gmtModified = new Date(System.currentTimeMillis());
    }

    public Attribute addAttribute(String attributeName, DataType dataType,
                        int isPrimary, int isForeign) {
        Attribute attribute = new Attribute(0L, this.ID, this.viewID, attributeName, dataType, isPrimary, isForeign);
        this.attributeList.add(attribute);
        // todo attribute insert DB
        return attribute;
    }

    public boolean deleteAttribute(Attribute attribute) {
        this.attributeList.remove(attribute);
        // todo attribute.deleteDB();
        return false;
    }

    public static List<Attribute> queryByAttribute() {
        // call mapper
        return null;
    }

    ResultState deleteDB(EntityDTO entityDTO) {
        return null;
    }

    ResultState updateDB(EntityDTO entityDTO) {
        return null;
    }
}
