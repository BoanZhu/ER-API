package com.ic.er;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ic.er.Exception.ERException;
import com.ic.er.common.ViewDeserializer;
import com.ic.er.entity.ViewDO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.ResultState;
import com.ic.er.common.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.exceptions.PersistenceException;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
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
            this.update();
        }
        return entity;
    }

    public boolean deleteEntity(Entity entity) {
        this.entityList.remove(entity);
        if (ER.useDB) {
            entity.deleteDB();
            this.update();
        }
        return false;
    }

    public Relationship createRelationship(String relationshipName, Entity firstEntity, Entity secondEntity,
                                           Cardinality cardinality) {
        Relationship relationship = new Relationship(0L, relationshipName, this.ID,
                firstEntity, secondEntity, cardinality, null, new Date(), new Date());
        this.getRelationshipList().add(relationship);
        if (ER.useDB) {
            this.update();
        }
        return relationship;
    }

    public boolean deleteRelationship(Relationship relationship) {
        this.getRelationshipList().remove(relationship);
        if (ER.useDB) {
            relationship.deleteDB();
            this.update();
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
        return TransListFormFromDB(ER.viewMapper.selectAll());
    }

    public String ToJSON() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(this);
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

    private static View TransformFromDB(ViewDO ViewDO) {
        List<Entity> entityList = Entity.queryByEntity(null);
        List<Relationship> relationshipList = Relationship.queryByRelationship(null);
        return new View(ViewDO.getID(), ViewDO.getName(), entityList, relationshipList, ViewDO.getCreator(),
                ViewDO.getGmtCreate(), ViewDO.getGmtModified());
    }

    private static List<View> TransListFormFromDB(List<ViewDO> doList) {
        List<View> ret = new ArrayList<>();
        for (ViewDO ViewDO : doList) {
            ret.add(TransformFromDB(ViewDO));
        }
        return ret;
    }

    public static List<View> queryByView(ViewDO ViewDO) {
        List<ViewDO> viewDOList = ER.viewMapper.selectByView(ViewDO);
        return TransListFormFromDB(viewDOList);
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

    public void update() {
        int ret = ER.viewMapper.updateByID(new ViewDO(this.ID, this.name, this.creator, 0L, 0, this.gmtCreate, new Date()));
        if (ret == 0) {
            throw new ERException(String.format("cannot find Attribute with ID: %d", this.ID));
        }
    }
}