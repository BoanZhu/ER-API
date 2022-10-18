package com.ic.er.service;

import com.ic.er.bean.dto.ViewDTO;
import com.ic.er.bean.vo.ViewVO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.ResultState;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class View {
    private Long ID;
    private String name;
    private List<Entity> entityList;
    private List<Relationship> RelationshipList;
    private String creator;
    private Date gmtCreate;
    private Date gmtModified;


    public View(String name, String creator) {
        this.name = name;
        this.creator = creator;
        this.gmtCreate = new Date(System.currentTimeMillis());
        this.gmtModified = new Date(System.currentTimeMillis());
    }
    public View(Long ID, String name, String creator) {
        this.ID = ID;
        this.name = name;
        this.creator = creator;
        this.gmtCreate = new Date(System.currentTimeMillis());
        this.gmtModified = new Date(System.currentTimeMillis());
    }

    public Entity addEntity(String entityName) {
        Entity entity = new Entity(0L, entityName, this.ID);
        this.entityList.add(entity);
        this.setGmtModified(new Date(System.currentTimeMillis()));

        // todo if enables database, then insert to db
        return entity;
    }

    public boolean removeEntity(Entity entity) {
        this.entityList.remove(entity);
        this.setGmtModified(new Date(System.currentTimeMillis()));

        // todo deleteDB();
        return false;
    }

    public Relationship createRelationship(String relationshipName, Long firstEntityID, Long secondEntityID,
                                           Cardinality cardinality) {
        Relationship relationship = new Relationship(0L, relationshipName, this.ID,
                firstEntityID, secondEntityID, 0L, 0L, cardinality,
                0, new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
        this.getRelationshipList().add(relationship);
        return relationship;
    }

    public boolean removeRelationship(Relationship relationship) {
        this.getRelationshipList().remove(relationship);
        this.setGmtModified(new Date(System.currentTimeMillis()));

        // todo deleteDB();
        return false;
    }

    ViewVO insertDB(ViewDTO view) {
        return null;
    }

    ResultState deleteDB(ViewDTO view) {
        return null;
    }

    List<ViewVO> queryAll() {
        return null;
    }

    ViewVO queryById() {
        return null;
    }

    ResultState update(ViewDTO name) {
        return null;
    }
}