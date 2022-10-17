package com.ic.er.service;

import com.ic.er.bean.dto.AttributeDTO;
import com.ic.er.bean.vo.AttributeVO;
import com.ic.er.common.ResultState;

import java.util.Date;

public class Attribute {
    private Long id;
    private Long entity_id;
    private Long view_id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(Long entity_id) {
        this.entity_id = entity_id;
    }

    public Long getView_id() {
        return view_id;
    }

    public void setView_id(Long view_id) {
        this.view_id = view_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public int getIs_primary() {
        return is_primary;
    }

    public void setIs_primary(int is_primary) {
        this.is_primary = is_primary;
    }

    public int getIs_foreign() {
        return is_foreign;
    }

    public void setIs_foreign(int is_foreign) {
        this.is_foreign = is_foreign;
    }

    public int getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(int is_delete) {
        this.is_delete = is_delete;
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }

    public Date getGmt_modified() {
        return gmt_modified;
    }

    public void setGmt_modified(Date gmt_modified) {
        this.gmt_modified = gmt_modified;
    }

    private String data_type;
    private int is_primary;
    private int is_foreign;
    private int is_delete;
    private Date gmt_create;
    private Date gmt_modified;

    public Attribute(Long id, Long entity_id, Long view_id, String name, String data_type, int is_primary, int is_foreign, int is_delete, Date gmt_create, Date gmt_modified) {
        this.id = id;
        this.entity_id = entity_id;
        this.view_id = view_id;
        this.name = name;
        this.data_type = data_type;
        this.is_primary = is_primary;
        this.is_foreign = is_foreign;
        this.is_delete = is_delete;
        this.gmt_create = gmt_create;
        this.gmt_modified = gmt_modified;
    }

    Long insertDB() {
        return Long(123);
    }

    ResultState deleteDB(AttributeDTO attributeDTO) {
        return null;
    }

    ResultState updateDB(AttributeDTO attributeDTO) {
        return null;
    }

}
