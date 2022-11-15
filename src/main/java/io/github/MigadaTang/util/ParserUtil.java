package io.github.MigadaTang.util;

import io.github.MigadaTang.*;
import io.github.MigadaTang.bean.dto.transform.ColumnDTO;
import io.github.MigadaTang.bean.dto.transform.TableDTO;
import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.ParseException;

import java.util.*;

public class ParserUtil {

    public static Schema parseAttributeToRelationship(List<TableDTO> tableDTOList) throws ParseException {
        Schema schema = ER.createSchema("reverseEng", "unknow");

        Map<Long, Entity> tableDTOEntityMap = new HashMap<>();

        // table and keys need to be processed specially
        List<ColumnDTO> foreignKeyList = new ArrayList<>();
        List<TableDTO> tableGenerateByRelationship = new ArrayList<>();
        parseEntity(tableDTOList, tableDTOEntityMap, tableGenerateByRelationship, schema, foreignKeyList);


        // parser foreign key, (1-N)
        for (ColumnDTO foreignKey : foreignKeyList) {
            Entity curEntity = tableDTOEntityMap.get(foreignKey.getBelongTo());
            Entity pointToEntity = tableDTOEntityMap.get(foreignKey.getForeignKeyTable());

            if (foreignKey.isNullable()) {
                schema.createRelationship("unknow", curEntity, pointToEntity, Cardinality.ZeroToOne,
                        Cardinality.OneToMany);
            } else {
                schema.createRelationship("unknow", curEntity, pointToEntity, Cardinality.OneToOne,
                        Cardinality.OneToMany);
            }
        }

        // parser table generate by relationship
        for (TableDTO tableDTO : tableGenerateByRelationship) {
            List<EntityWithCardinality> entityWithCardinalityList = new ArrayList<>();
            Set<Long> foreignTableList = new HashSet<>();
            for (ColumnDTO column : tableDTO.getColumnDTOList()) {
                if (!foreignTableList.contains(column.getForeignKeyTable())) {
                    foreignTableList.add(column.getForeignKeyTable());
                    EntityWithCardinality entityWithCardinality = new EntityWithCardinality(tableDTOEntityMap.get(tableDTO.getId()), Cardinality.ZeroToMany);
                    entityWithCardinalityList.add(entityWithCardinality);
                }
            }
            schema.createNaryRelationship("unknow", entityWithCardinalityList);
        }

        return schema;
    }

    private static void parseEntity(List<TableDTO> tableDTOList, Map<Long, Entity> tableDTOEntityMap,
                                          List<TableDTO> tableGenerateByRelationship, Schema schema,
                                          List<ColumnDTO> foreignKeyList) throws ParseException {
        List<TableDTO> possibleWeakEntitySet = new ArrayList<>();
        List<TableDTO> possibleSubsetSet = new ArrayList<>();

        // parse strong entity
        for (TableDTO strongEntity : tableDTOList) {
            List<ColumnDTO> columnDTOList = strongEntity.getColumnDTOList();

            int pkIsFk = 0;
            int fkNum = 0;
            Set<Long> fkTables = new HashSet<>();
            for (ColumnDTO columnDTO : strongEntity.getColumnDTOList()) {
                if (columnDTO.isForeign()) {
                    fkNum++;
                    fkTables.add(columnDTO.getForeignKeyTable());
                    if (columnDTO.isPrimary())
                        pkIsFk++;
                }
            }

            if (pkIsFk == strongEntity.getPrimaryKey().size() && fkTables.size() == 1) {
                possibleSubsetSet.add(strongEntity);
                continue;
            }
            if (pkIsFk > 0 && fkTables.size() == 1) {
                possibleWeakEntitySet.add(strongEntity);
                continue;
            }

            if (fkNum == strongEntity.getColumnDTOList().size() && fkTables.size() > 1) {
                tableGenerateByRelationship.add(strongEntity);
                continue;
            }

            Entity entity = schema.addEntity(strongEntity.getName());

            Set<Long> foreignTableList = new HashSet<>();
            for (ColumnDTO column : columnDTOList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    entity.addAttribute(column.getName(), DataType.TEXT, column.isPrimary(), column.isNullable());
                }
            }

            tableDTOEntityMap.put(strongEntity.getId(), entity);
        }

        for (TableDTO weakEntity : possibleWeakEntitySet) {
            if (!tableDTOEntityMap.containsKey(weakEntity.getBelongStrongTableID()))
                throw new ParseException("Api only support weak entity relies on strong entity for current version");

            Entity entity = schema.addWeakEntity(weakEntity.getName(), tableDTOEntityMap.get(weakEntity.getBelongStrongTableID()),
                    "unknow", Cardinality.OneToOne, Cardinality.ZeroToMany).getLeft();
            tableDTOEntityMap.put(weakEntity.getId(), entity);
            List<ColumnDTO> columnDTOList = weakEntity.getColumnDTOList();
            Set<Long> foreignTableList = new HashSet<>();
            for (ColumnDTO column : columnDTOList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable()) &&
                            !column.getForeignKeyTable().equals(weakEntity.getBelongStrongTableID())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    entity.addAttribute(column.getName(), DataType.TEXT, column.isPrimary(), column.isNullable());
                }
            }
        }

        for (TableDTO subset : possibleSubsetSet) {
            if (!tableDTOEntityMap.containsKey(subset.getBelongStrongTableID()))
                throw new ParseException("Api only support subset relies on strong entity for current version");

            Entity entity = schema.addWeakEntity(subset.getName(), tableDTOEntityMap.get(subset.getBelongStrongTableID()),
                    "unknow", Cardinality.OneToOne, Cardinality.ZeroToMany).getLeft();
            tableDTOEntityMap.put(subset.getId(), entity);
            List<ColumnDTO> columnDTOList = subset.getColumnDTOList();
            Set<Long> foreignTableList = new HashSet<>();
            for (ColumnDTO column : columnDTOList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable()) &&
                            !column.getForeignKeyTable().equals(subset.getBelongStrongTableID())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    entity.addAttribute(column.getName(), DataType.TEXT, column.isPrimary(), column.isNullable());
                }
            }
        }
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

            // weak entity
            if (relationshipEdges.size() == 2) {
                if (!tableDTOMap.containsKey(relationshipEdges.get(0).getSchemaID()) ||
                        !tableDTOMap.containsKey(relationshipEdges.get(1).getSchemaID())) {
                    throw new ParseException("The table which relationship reference to does not exist. Relationship ID: " + relationship.getID());
                }
                TableDTO firstTable = tableDTOMap.get(relationshipEdges.get(0).getEntity().getID());
                TableDTO secondTable = tableDTOMap.get(relationshipEdges.get(1).getEntity().getID());
                if (firstTable.getTableType() == EntityType.WEAK && firstTable.getBelongStrongTableID().equals(secondTable.getId())) {
                    parseWeakEntity(firstTable, secondTable);
                    continue;
                } else if (secondTable.getTableType() == EntityType.WEAK && secondTable.getBelongStrongTableID().equals(firstTable.getId())) {
                    parseWeakEntity(secondTable, firstTable);
                    continue;
                }
            }

            Map<Cardinality, List<Long>> cardinalityListMap = generateCardinalityMap(tableDTOMap, relationship);

            if (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 1) {
                parseOneToMany(cardinalityListMap, tableDTOMap, relationshipAttributeList);
            } else {
                if (relationship.getEdgeList().size() == 2 &&
                        (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 2)) {
                    parseTwoOneToOne(cardinalityListMap, tableDTOMap, relationshipAttributeList);
                } else {
                    List<Long> tableIdList = cardinalityListMap.get(Cardinality.OneToOne);
                    tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToOne));
                    tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToMany));
                    tableIdList.addAll(cardinalityListMap.get(Cardinality.OneToMany));
                    parseCardinalityWithNewTable(tableIdList, tableDTOMap, relationship.getName(), relationshipAttributeList);
                }
            }
        }

        return tableDTOMap;
    }


    private static void parseTwoOneToOne(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, TableDTO> tableDTOMap, List<Attribute> relationshipAttributeList) throws ParseException {
        TableDTO importedTable;
        TableDTO exportedTable;
        boolean canbeNull = false;
        if (cardinalityListMap.get(Cardinality.OneToOne).size() == 2) {
            importedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.OneToOne).get(0));
            exportedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.OneToOne).get(1));
        } else if (cardinalityListMap.get(Cardinality.ZeroToOne).size() == 2) {
            importedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.OneToOne).get(0));
            exportedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.ZeroToOne).get(1));
            canbeNull = true;
        } else {
            importedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.OneToOne).get(0));
            exportedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.ZeroToOne).get(0));
        }

        List<ColumnDTO> fkList = new ArrayList<>();
        for (ColumnDTO pk : exportedTable.getPrimaryKey()) {
            ColumnDTO fk = pk.getForeignClone(importedTable.getId(), false, exportedTable.getName());
            if (canbeNull)
                fk.setNullable(true);
            importedTable.getColumnDTOList().add(fk);
            fkList.add(fk);
        }
        importedTable.getForeignKey().put(exportedTable.getId(), fkList);
    }


    private static void parseWeakEntity(TableDTO weakEntity, TableDTO strongEntity) {
        List<ColumnDTO> weakColumns = weakEntity.getColumnDTOList();
        List<ColumnDTO> weakPk = weakEntity.getPrimaryKey();
        Map<Long, List<ColumnDTO>> weakFk = weakEntity.getForeignKey();
        List<ColumnDTO> newFk = new ArrayList<>();

        for (ColumnDTO strongPkColumn : strongEntity.getPrimaryKey()) {
            ColumnDTO weakFkColumn = strongPkColumn.getForeignClone(weakEntity.getId(), true, strongEntity.getName());
            weakPk.add(weakFkColumn);
            newFk.add(weakFkColumn);
            weakColumns.add(weakFkColumn);
        }
        weakFk.put(strongEntity.getId(), newFk);
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
                if (tableIdList.size() == 2)
                    cloneFk.setNullable(false);
                else
                    cloneFk.setNullable(true);
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
