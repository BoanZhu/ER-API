package io.github.MigadaTang;

import io.github.MigadaTang.bean.dto.transform.ColumnDTO;
import io.github.MigadaTang.bean.dto.transform.TableDTO;
import io.github.MigadaTang.common.BelongObjType;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.util.RandomUtils;

import java.util.*;

public class ParserUtil {

    public static Schema parseAttributeToRelationship(List<TableDTO> tableDTOList) throws ParseException {
        List<Entity> entityList = new ArrayList<>();
        List<Relationship> relationshipList = new ArrayList<>();
        List<ColumnDTO> foreignKeyList = new ArrayList<>();
        Schema schema = new Schema(RandomUtils.generateID(), "unknown", entityList, relationshipList, "unknown", null, null);

        List<TableDTO> tableGenerateByRelationship = new ArrayList<>();
        Map<Long, Entity> tableDTOEntityMap = new HashMap<>();
        boolean hasFkAsPk = false;
        boolean allPkIsFk = false;
        for (TableDTO table : tableDTOList) {
            List<Attribute> attributeList = new ArrayList<>();
            List<ColumnDTO> columnDTOList = table.getColumnDTOList();

            int pkIsFk = 0;
            int fkNum = 0;
            for (ColumnDTO columnDTO : table.getColumnDTOList()) {
                if (columnDTO.isForeign()) {
                    fkNum++;
                    if (columnDTO.isPrimary())
                        pkIsFk++;
                }
            }

            if (fkNum == table.getColumnDTOList().size())
                tableGenerateByRelationship.add(table);
            if (pkIsFk > 0)
                hasFkAsPk = true;
            if (pkIsFk == table.getPrimaryKey().size())
                allPkIsFk = true;

            Set<Long> foreignTableList = new HashSet<>();
            for (ColumnDTO column : columnDTOList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    LayoutInfo undefinedLayout = new LayoutInfo(RandomUtils.generateID(), column.getID(), BelongObjType.ATTRIBUTE,
                            -1.0, -1.0);
                    Attribute attribute = new Attribute(column.getID(), column.getBelongTo(), null, null
                            , column.getName(), null, column.isPrimary(), column.isNullable()
                            , -1, undefinedLayout, null, null);
                    attributeList.add(attribute);
                }
            }

            Entity entity;
            LayoutInfo undefinedLayout = new LayoutInfo(RandomUtils.generateID(), table.getId(), BelongObjType.ENTITY,
                    -1.0, -1.0);
            if (allPkIsFk) {
                entity = new Entity(table.getId(), table.getName(), null, EntityType.SUBSET, null
                        , attributeList, -1, undefinedLayout, null, null);
            } else if (hasFkAsPk) {
                entity = new Entity(table.getId(), table.getName(), null, EntityType.WEAK, null
                        , attributeList, -1, undefinedLayout, null, null);
            } else {
                entity = new Entity(table.getId(), table.getName(), null, EntityType.STRONG, null
                        , attributeList, -1, undefinedLayout, null, null);
            }

            tableDTOEntityMap.put(table.getId(), entity);
            entityList.add(entity);
        }

        // parser foreign key, (1-N)
        for (ColumnDTO foreignKey : foreignKeyList) {
            List<Attribute> attributeList = new ArrayList<>();
            List<RelationshipEdge> edgeList = new ArrayList<>();
            Long relationshipId = RandomUtils.generateID();
            LayoutInfo undefinedLayout = new LayoutInfo(RandomUtils.generateID(), relationshipId, BelongObjType.RELATIONSHIP,
                    -1.0, -1.0);
            Relationship relationship = new Relationship(relationshipId, "unknow", schema.getID(),
                    attributeList, edgeList, undefinedLayout, null, null);

            RelationshipEdge edgeToRelationship;
            if (foreignKey.isNullable()) {
                edgeToRelationship = new RelationshipEdge(RandomUtils.generateID(), relationship.getID(),
                        schema.getID(), tableDTOEntityMap.get(foreignKey.getBelongTo()), Cardinality.ZeroToOne,
                        -1, -1, null, null);
            } else {
                edgeToRelationship = new RelationshipEdge(RandomUtils.generateID(), relationship.getID(),
                        schema.getID(), tableDTOEntityMap.get(foreignKey.getBelongTo()), Cardinality.OneToOne,
                        -1, -1, null, null);
            }
            RelationshipEdge edgeFromRelationship = new RelationshipEdge(RandomUtils.generateID(), relationship.getID(),
                    schema.getID(), tableDTOEntityMap.get(foreignKey.getForeignKeyTable()), Cardinality.ZeroToMany,
                    -1, -1, null, null);
            edgeList.add(edgeFromRelationship);
            edgeList.add(edgeToRelationship);
            relationshipList.add(relationship);
        }

        // parser table generate by relationship
        for (TableDTO tableDTO : tableGenerateByRelationship) {
            List<RelationshipEdge> edgeList = new ArrayList<>();
            Long relationshipId = RandomUtils.generateID();
            LayoutInfo undefinedLayout = new LayoutInfo(RandomUtils.generateID(), relationshipId, BelongObjType.RELATIONSHIP,
                    -1.0, -1.0);
            Relationship relationship = new Relationship(relationshipId, "unknow", schema.getID(),
                    new ArrayList<>(), edgeList, undefinedLayout, null, null);
            Set<Long> foreignTableList = new HashSet<>();
            for (ColumnDTO column : tableDTO.getColumnDTOList()) {
                if (!foreignTableList.contains(column.getForeignKeyTable())) {
                    foreignKeyList.add(column);
                    foreignTableList.add(column.getForeignKeyTable());
                    RelationshipEdge edge = new RelationshipEdge(RandomUtils.generateID(), relationship.getID(),
                            schema.getID(), tableDTOEntityMap.get(column.getBelongTo()), Cardinality.ZeroToMany,
                            -1, -1, null, null);
                    edgeList.add(edge);
                }
            }
            relationshipList.add(relationship);
        }

        return schema;
    }


    public static Map<Long, TableDTO> parseRelationshipsToAttribute(List<Entity> entityList, List<Relationship> relationshipList) throws ParseException {

        Map<Long, TableDTO> tableDTOMap = new HashMap<>();
        List<Long> subsetMap = new ArrayList<>();
        for (Entity entity : entityList) {
            TableDTO tableDTO = new TableDTO();
            tableDTO.tranformEntity(entity);
            tableDTOMap.put(tableDTO.getId(), tableDTO);
            if (tableDTO.getTableType() == EntityType.SUBSET) {
                if (tableDTO.getBelongStrongTableID() == null) {
                    throw new ParseException("Weak entity does not belong to any entity.");
                }
                subsetMap.add(tableDTO.getId());
            }
        }

        for (Long tableId : subsetMap) {
            TableDTO weakEntity = tableDTOMap.get(tableId);
            TableDTO strongEntity = tableDTOMap.get(weakEntity.getBelongStrongTableID());
            List<ColumnDTO> strongPk = strongEntity.getPrimaryKey();

            List<ColumnDTO> weakPk = weakEntity.getPrimaryKey();
            List<ColumnDTO> weakColumns = weakEntity.getColumnDTOList();
            Map<Long, List<ColumnDTO>> weakFk = weakEntity.getForeignKey();
            List<ColumnDTO> newFk = new ArrayList<>();
            for (ColumnDTO columnDTO : strongPk) {
                ColumnDTO cloneC = columnDTO.getForeignClone(tableId, true, strongEntity.getName());
                weakPk.add(cloneC);
                weakColumns.add(cloneC);
                newFk.add(cloneC);
            }
            weakFk.put(strongEntity.getId(), newFk);
            weakEntity.setColumnDTOList(weakColumns);
            weakEntity.setPrimaryKey(weakPk);
            weakEntity.setForeignKey(weakFk);
        }

        // parse relation to new table or new attribute in existing table
        for (Relationship relationship : relationshipList) {
            List<RelationshipEdge> relationshipEdges = relationship.getEdgeList();
            List<Attribute> relationshipAttributeList = relationship.getAttributeList();

            if (relationshipEdges.size() == 1) {
                throw new ParseException("The relationship only reference to one table. Relationship ID: " + relationship.getID());
            }

            Map<Cardinality, List<Long>> cardinalityListMap = generateCardinalityMap(tableDTOMap, relationship);

            if (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 1) {
                parseOneToMany(cardinalityListMap, tableDTOMap, relationshipAttributeList);
            } else {
                List<Long> tableIdList = cardinalityListMap.get(Cardinality.OneToOne);
                tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToOne));
                tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToMany));
                tableIdList.addAll(cardinalityListMap.get(Cardinality.OneToMany));
                parseCardinalityWithNewTable(tableIdList, tableDTOMap, relationship.getName(), relationshipAttributeList);
            }
        }

        return tableDTOMap;
    }


    private static Map<Cardinality, List<Long>> generateCardinalityMap(Map<Long, TableDTO> tableDTOMap, Relationship relationship) throws ParseException {
        Map<Cardinality, List<Long>> cardinalityListMap = new HashMap<>();
        cardinalityListMap.put(Cardinality.OneToMany, new ArrayList<>());
        cardinalityListMap.put(Cardinality.OneToOne, new ArrayList<>());
        cardinalityListMap.put(Cardinality.ZeroToMany, new ArrayList<>());
        cardinalityListMap.put(Cardinality.ZeroToOne, new ArrayList<>());

        for (RelationshipEdge edge : relationship.getEdgeList()) {
            Long tableID = edge.getEntity().getID();
            if (!tableDTOMap.containsKey(tableID)) {
                throw new ParseException("The table relationship connected does not exist. Relationship ID: "
                        + relationship.getID() + ", Table id: " + tableID);
            }
            List<Long> cardinalityTableList = cardinalityListMap.get(edge.getCardinality());
            cardinalityTableList.add(tableID);
            cardinalityListMap.put(edge.getCardinality(), cardinalityTableList);
        }
        return cardinalityListMap;
    }


    private static void parseOneToMany(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, TableDTO> tableDTOMap, List<Attribute> attributeList) throws ParseException {
        Long mainTableId;
        boolean canBeZero = false;
        if (cardinalityListMap.get(Cardinality.OneToOne).size() == 1) {
            mainTableId = cardinalityListMap.get(Cardinality.OneToOne).get(0);
        } else {
            canBeZero = true;
            mainTableId = cardinalityListMap.get(Cardinality.ZeroToOne).get(0);
        }

        TableDTO mainTable = tableDTOMap.get(mainTableId);
        List<ColumnDTO> columnList = mainTable.getColumnDTOList();
        Map<Long, List<ColumnDTO>> mainTableForeignKey = mainTable.getForeignKey();

        List<Long> foreignTableIdList = cardinalityListMap.get(Cardinality.OneToMany);
        foreignTableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToMany));
        for (Long foreignTableId : foreignTableIdList) {
            TableDTO foreignTable = tableDTOMap.get(foreignTableId);
            if (foreignTable.getPrimaryKey().size() == 0) {
                throw new ParseException("Relationship fail to connect to the primary key of table : " + foreignTable.getName());
            }

            List<ColumnDTO> fkColumns = new ArrayList<>();
            for (ColumnDTO pk : foreignTable.getPrimaryKey()) {
                ColumnDTO cloneFk = pk.getForeignClone(mainTableId, false, foreignTable.getName());
                if (canBeZero) {
                    cloneFk.setNullable(true);
                }
                fkColumns.add(cloneFk);
                columnList.add(cloneFk);
            }

            mainTableForeignKey.put(foreignTableId, fkColumns);
        }

        for (Attribute attribute : attributeList) {
            ColumnDTO column = new ColumnDTO();
            column.transformAttribute(attribute);
            columnList.add(column);
        }
    }


    private static void parseCardinalityWithNewTable(List<Long> tableIdList, Map<Long, TableDTO> tableDTOMap, String reName, List<Attribute> attributeList) {

        StringBuilder tableName = new StringBuilder(reName.replace(' ', '_'));
        List<ColumnDTO> columnList = new ArrayList<>();
        Map<Long, List<ColumnDTO>> foreignKey = new HashMap<>();

        TableDTO newTable = new TableDTO(RandomUtils.generateID(), tableName.toString(), EntityType.STRONG, null,
                columnList, new ArrayList<>(), foreignKey);

        for (Long tableId : tableIdList) {
            TableDTO foreignTable = tableDTOMap.get(tableId);
            tableName.append("_").append(foreignTable.getName());

            List<ColumnDTO> fkColumns = new ArrayList<>();
            for (ColumnDTO pk : foreignTable.getPrimaryKey()) {
                ColumnDTO cloneFk = pk.getForeignClone(newTable.getId(), false, foreignTable.getName());

                // TODO nullable?
                fkColumns.add(cloneFk);
                columnList.add(cloneFk);
            }

            foreignKey.put(tableId, fkColumns);
        }


        for (Attribute attribute : attributeList) {
            ColumnDTO column = new ColumnDTO();
            column.transformAttribute(attribute);
            columnList.add(column);
        }

        newTable.setName(tableName.toString());
        tableDTOMap.put(newTable.getId(), newTable);
    }
}
