package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ic.er.Exception.ERException;
import com.ic.er.common.ViewDeserializer;
import com.ic.er.entity.ViewDO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Getter;
import org.apache.ibatis.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@JsonDeserialize(using = ViewDeserializer.class)
public class View {
    @JsonIgnore
    private Long ID;
    private String name;
    private List<Entity> entityList;
    private List<Relationship> relationshipList;
    private String creator;
    @JsonIgnore
    private Date gmtCreate;
    @JsonIgnore
    private Date gmtModified;

    protected View(Long ID, String name, List<Entity> entityList, List<Relationship> relationshipList, String creator, Date gmtCreate, Date gmtModified) {
        this.ID = ID;
        this.name = name;
        this.entityList = entityList;
        this.relationshipList = relationshipList;
        this.creator = creator;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        if (this.ID == 0) {
            if (ER.useDB) {
                insertDB();
            } else {
                this.ID = Utils.generateID();
            }
        }
    }

    public Entity addEntity(String entityName) {
        Entity entity = new Entity(0L, entityName, this.ID, new ArrayList<>(), null, new Date(), new Date());
        this.entityList.add(entity);
        if (ER.useDB) {
            this.updateInfo(null);
        }
        return entity;
    }

    public boolean deleteEntity(Entity entity) {
        this.entityList.remove(entity);
        if (ER.useDB) {
            entity.deleteDB();
            this.updateInfo(null);
        }
        return false;
    }

    public Relationship createRelationship(String relationshipName, Entity firstEntity, Entity secondEntity,
                                           Cardinality cardinality) {
        Relationship relationship = new Relationship(0L, relationshipName, this.ID,
                firstEntity, secondEntity, cardinality, null, new Date(), new Date());
        this.getRelationshipList().add(relationship);
        if (ER.useDB) {
            this.updateInfo(null);
        }
        return relationship;
    }

    public boolean deleteRelationship(Relationship relationship) {
        this.getRelationshipList().remove(relationship);
        if (ER.useDB) {
            relationship.deleteDB();
            this.updateInfo(null);
        }
        return false;
    }

    private void insertDB() {
        try {
            ViewDO viewDO = new ViewDO(0L, this.name, this.creator, 0L, 0, this.gmtCreate, this.gmtModified);
            int ret = ER.viewMapper.insert(viewDO);
            if (ret == 0) {
                throw new ERException("insertDB fail");
            }
            this.ID = viewDO.getID();
        } catch (PersistenceException e) {
            throw new ERException("insertDB fail", e);
        }
    }

    public static List<View> queryAll() {
        return Trans.TransViewListFromDB(ER.viewMapper.selectAll());
    }

    public String ToJSON() {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json;
        try {
            json = ow.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return json;
    }

    public static View loadFromJSON(String json) throws ERException {
        try {
            View view = new ObjectMapper().readValue(json, View.class);
            System.out.println(view.toString());
            return view;
        } catch (JsonProcessingException e) {
            throw new ERException(String.format("loadFromJSON fail, error: %s", e.getMessage()));
        }
    }


    public static List<View> queryByView(ViewDO ViewDO) {
        List<ViewDO> viewDOList = ER.viewMapper.selectByView(ViewDO);
        return Trans.TransViewListFromDB(viewDOList);
    }

    public static View queryByID(Long ID) {
        List<View> viewDOList = queryByView(new ViewDO(ID));
        if (viewDOList.size() == 0) {
            throw new ERException(String.format("View with ID: %d not found ", ID));
        } else {
            return viewDOList.get(0);
        }
    }

    protected void deleteDB() {
        ER.viewMapper.deleteByID(this.ID);
    }

    public void updateInfo(String name) {
        if (name != null) {
            this.name = name;
        }
        int ret = ER.viewMapper.updateByID(new ViewDO(this.ID, this.name, this.creator, 0L, 0, this.gmtCreate, new Date()));
        if (ret == 0) {
            throw new ERException(String.format("cannot find Attribute with ID: %d", this.ID));
        }
    }
}