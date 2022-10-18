package com.ic.er;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ic.er.bean.entity.RelationshipDO;
import com.ic.er.bean.entity.ViewDO;
import com.ic.er.bean.vo.ViewVO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class View {
    private Long ID;
    private String name;
    private List<Entity> entityList;
    private List<Relationship> relationshipList;
    private String creator;
    private Date gmtCreate;
    private Date gmtModified;
    public View(Long ID, String name, List<Entity> entityList, List<Relationship> relationshipList, String creator, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.entityList = entityList;
        this.relationshipList = relationshipList;
        this.creator = creator;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            if (ER.useDB) {
                this.ID = insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
    }

    public static View createView(String name, String creator) {
        return new View(0L, name, new ArrayList<>(), new ArrayList<>(), creator, new Date(), new Date());
    }

    public Entity addEntity(String entityName) {
        Entity entity = new Entity(0L, entityName, this.ID, new ArrayList<>(), new Date(), new Date());
        this.entityList.add(entity);
        this.setGmtModified(new Date(System.currentTimeMillis()));
        if (ER.useDB) {
            this.updateDB();
        }
        return entity;
    }

    public boolean removeEntity(Entity entity) {
        if (ER.useDB) {
            entity.deleteDB();
        }
        this.entityList.remove(entity);
        this.setGmtModified(new Date(System.currentTimeMillis()));
        if (ER.useDB) {
            this.updateDB();
        }
        return false;
    }

    public Relationship createRelationship(String relationshipName, Long firstEntityID, Long secondEntityID,
                                           Cardinality cardinality) {
        Relationship relationship = new Relationship(0L, relationshipName, this.ID,
                firstEntityID, secondEntityID, 0L, 0L, cardinality,
                 new Date(System.currentTimeMillis()), new Date(System.currentTimeMillis()));
        this.getRelationshipList().add(relationship);
        if (ER.useDB) {
            this.updateDB();
        }
        return relationship;
    }

    public boolean removeRelationship(Relationship relationship) {
        if (ER.useDB) {
            relationship.deleteDB();
        }
        this.getRelationshipList().remove(relationship);
        this.setGmtModified(new Date(System.currentTimeMillis()));
        if (ER.useDB) {
            this.updateDB();
        }
        return false;
    }

    Long insertDB() {
        return ER.viewMapper.insert(new ViewDO(
                0L,
                this.name,
                this.creator,
                0L,
                0,
                this.gmtCreate,
                this.gmtModified
        ));
    }

    public static List<View> queryAll() {
        return null;
    }

    public String ToJSON() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(this);
        return json;
    }

    ViewVO queryById() {
        return null;
    }

    public static View TransformFromDB(ViewDO ViewDO) {
        List<Entity> entityList = Entity.queryByEntity(null);
        List<Relationship> relationshipList = Relationship.queryByRelationship(null);
        return new View(ViewDO.getId(), ViewDO.getName(), entityList, relationshipList, ViewDO.getCreator(),
                ViewDO.getGmt_create(), ViewDO.getGmt_modified());
    }

    public static List<View> TransListFormFromDB(List<ViewDO> doList) {
        List<View> ret = new ArrayList<>();
        for (ViewDO ViewDO : doList) {
            ret.add(TransformFromDB(ViewDO));
        }
        return ret;
    }

    public static List<View> queryByView(ViewDO ViewDO) {
        List<com.ic.er.bean.entity.ViewDO> viewDOList = ER.viewMapper.selectByView(ViewDO);
        return TransListFormFromDB(viewDOList);
    }

    public ResultState delete() {
        int res = ER.viewMapper.deleteById(this.ID);
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }

    ResultState updateDB() {
        int res = ER.viewMapper.updateById(this.ID);
        if (res == 0) {
            return ResultState.ok();
        } else {
            return ResultState.build(1, "db error");
        }
    }
}