package io.github.MigadaTang.util;

import io.github.MigadaTang.*;
import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.ParseException;

import java.util.*;

public class ParserUtil {

    public static Schema parseAttributeToRelationship(List<Table> tableList) throws ParseException {
        Schema schema = ER.createSchema("reverseEng");

        Map<Long, Entity> tableDTOEntityMap = new HashMap<>();

        // table and keys need to be processed specially
        List<Column> foreignKeyList = new ArrayList<>();
        List<Table> tableGenerateByRelationship = new ArrayList<>();
        parseTableToEntity(tableList, tableDTOEntityMap, tableGenerateByRelationship, schema, foreignKeyList);


        // parser foreign key, (1-N)
        for (Column foreignKey : foreignKeyList) {
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
        for (Table table : tableGenerateByRelationship) {
            List<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : table.getColumnList()) {
                if (!foreignTableList.contains(column.getForeignKeyTable())) {
                    foreignTableList.add(column.getForeignKeyTable());
                    ConnObjWithCardinality connObjWithCardinality = new ConnObjWithCardinality(tableDTOEntityMap.get(column.getForeignKeyTable()), Cardinality.ZeroToMany);
                    connObjWithCardinalityList.add(connObjWithCardinality);
                }
            }
            schema.createNaryRelationship("unknow", connObjWithCardinalityList);
        }

        return schema;
    }

    private static void parseTableToEntity(List<Table> tableList, Map<Long, Entity> tableDTOEntityMap,
                                    List<Table> tableGenerateByRelationship, Schema schema,
                                    List<Column> foreignKeyList) throws ParseException {
        List<Table> possibleWeakEntitySet = new ArrayList<>();
        List<Table> possibleSubsetSet = new ArrayList<>();

        // parse strong entity
        for (Table strongEntity : tableList) {
            List<Column> columnList = strongEntity.getColumnList();

            int pkIsFk = 0;
            int fkNum = 0;
            Set<Long> fkTables = new HashSet<>();
            Long fkTableId = null;
            for (Column column : strongEntity.getColumnList()) {
                if (column.isForeign()) {
                    fkNum++;
                    fkTables.add(column.getForeignKeyTable());
                    if (column.isPrimary()) {
                        pkIsFk++;
                        fkTableId = column.getForeignKeyTable();
                    }
                }
            }

            if (pkIsFk == strongEntity.getPrimaryKey().size() && fkTables.size() == 1 && pkIsFk > 0) {
                strongEntity.setBelongStrongTableID(fkTableId);
                possibleSubsetSet.add(strongEntity);
                continue;
            }
            if (pkIsFk > 0 && fkTables.size() == 1) {
                possibleWeakEntitySet.add(strongEntity);
                strongEntity.setBelongStrongTableID(fkTableId);
                continue;
            }

            if (fkNum == strongEntity.getColumnList().size() && fkTables.size() > 1) {
                tableGenerateByRelationship.add(strongEntity);
                continue;
            }

            Entity entity = schema.addEntity(strongEntity.getName());

            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : columnList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
//                    entity.addAttribute(column.getName(), DataType.TEXT, column.isPrimary(), column.isNullable());
                }
            }

            tableDTOEntityMap.put(strongEntity.getId(), entity);
        }

        for (Table weakEntity : possibleWeakEntitySet) {
            if (!tableDTOEntityMap.containsKey(weakEntity.getBelongStrongTableID()))
                throw new ParseException("Api only support weak entity relies on strong entity for current version");

            Entity entity = schema.addWeakEntity(weakEntity.getName(), tableDTOEntityMap.get(weakEntity.getBelongStrongTableID()),
                    "unknow", Cardinality.OneToOne, Cardinality.ZeroToMany).getLeft();
            tableDTOEntityMap.put(weakEntity.getId(), entity);
            List<Column> columnList = weakEntity.getColumnList();
            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : columnList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable()) &&
                            !column.getForeignKeyTable().equals(weakEntity.getBelongStrongTableID())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
//                    entity.addAttribute(column.getName(), DataType.TEXT, column.isPrimary(), column.isNullable());
                }
            }
        }

        for (Table subset : possibleSubsetSet) {
            if (!tableDTOEntityMap.containsKey(subset.getBelongStrongTableID()))
                throw new ParseException("Api only support subset relies on strong entity for current version");

            Entity entity = schema.addSubset(subset.getName(), tableDTOEntityMap.get(subset.getBelongStrongTableID()));
            tableDTOEntityMap.put(subset.getId(), entity);
            List<Column> columnList = subset.getColumnList();
            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : columnList) {
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable()) &&
                            !column.getForeignKeyTable().equals(subset.getBelongStrongTableID())) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
//                    entity.addAttribute(column.getName(), DataType.TEXT, column.isPrimary(), column.isNullable());
                }
            }
        }
    }


    public static Map<Long, Table> parseRelationshipsToAttribute(List<Entity> entityList, List<Relationship> relationshipList) throws ParseException {

        Map<Long, Table> tableDTOMap = new HashMap<>();
        List<Long> subsetMap = new ArrayList<>();

        // parse all entities to table
        parseEntityToTable(entityList, tableDTOMap, subsetMap);

        // parse multi valued column
        parseMultiValuedColumn(tableDTOMap);

        // parse subset: add pk from strong entity to subset
        parseSubSet(tableDTOMap, subsetMap);

        // parse relation to new table or new attribute in existing table
        parseRelationships(relationshipList, tableDTOMap);

        return tableDTOMap;
    }


    private static void parseEntityToTable(List<Entity> entityList, Map<Long, Table> tableDTOMap, List<Long> subsetMap) {
        for (Entity entity : entityList) {
            Table table = new Table();
            table.tranformEntity(entity);
            tableDTOMap.put(table.getId(), table);
            if (table.getTableType() == EntityType.SUBSET) {
                subsetMap.add(table.getId());
            }
        }
    }


    private static void parseSubSet(Map<Long, Table> tableDTOMap, List<Long> subsetMap) {
        for (Long tableId : subsetMap) {
            Table subset = tableDTOMap.get(tableId);
            Table strongEntity = tableDTOMap.get(subset.getBelongStrongTableID());
            List<Column> strongPk = strongEntity.getPrimaryKey();

            List<Column> weakPk = subset.getPrimaryKey();
            List<Column> weakColumns = subset.getColumnList();
            Map<Long, List<Column>> weakFk = subset.getForeignKey();
            List<Column> newFk = new ArrayList<>();
            for (Column column : strongPk) {
                Column cloneC = column.getForeignClone(tableId, true, strongEntity.getName());
                weakPk.add(cloneC);
                weakColumns.add(cloneC);
                newFk.add(cloneC);
            }
            weakFk.put(strongEntity.getId(), newFk);
            subset.setColumnList(weakColumns);
            subset.setPrimaryKey(weakPk);
            subset.setForeignKey(weakFk);
        }
    }


    private static void parseMultiValuedColumn(Map<Long, Table> tableDTOMap) {
        for (Table table : tableDTOMap.values()) {
            List<Column> multiValuedColumns = table.getMultiValuedColumn();
            if (multiValuedColumns.size() == 0)
                continue;

            for (Column column : multiValuedColumns) {
                parseMultiValuedColumn(tableDTOMap, table, column);
            }
        }
    }


    private static void parseMultiValuedColumn(Map<Long, Table> tableDTOMap, Table table, Column column) {
        List<Column> columnList = new ArrayList<>();
        List<Column> fkList = new ArrayList<>();
        List<Column> pkList = new ArrayList<>();
        columnList.add(column);
        pkList.add(column);
        Long newTableId = RandomUtils.generateID();
        for (Column pk : table.getPrimaryKey()) {
            Column pkClone = pk.getForeignClone(newTableId, false, "");
            columnList.add(pkClone);
            fkList.add(pkClone);
            pkList.add(pkClone);
        }
        Map<Long, List<Column>> fk = new HashMap<>();
        fk.put(table.getId(), fkList);
        Table newT = new Table(newTableId, table.getName()+"_"+column.getName(), EntityType.STRONG,
                null, columnList, pkList, new ArrayList<>(), fk);
        tableDTOMap.put(newT.getId(), newT);
    }


    private static void parseRelationships(List<Relationship> relationshipList, Map<Long, Table> tableDTOMap) {
        // mapping the relationship to existing or new table
        Map<Long, Long> mapRelationshipToTable = new HashMap<>();
        List<Relationship> normalRelationship = new ArrayList<>();

        // parse weak entity and save all the relationships using id
        for (Relationship relationship : relationshipList) {
            List<RelationshipEdge> relationshipEdges = relationship.getEdgeList();
            // parse weak entity
            for (RelationshipEdge edge : relationshipEdges) {
                if (edge.getIsKey()) {
                    relationshipEdges.remove(edge);
                    parseWeakEntity(edge, relationshipEdges, tableDTOMap);
                    break;
                }
            }
            normalRelationship.add(relationship);
        }

        Queue<Relationship> relationshipQueue = generateRelationshipTopologySeq(normalRelationship);

        for (Relationship relationship : relationshipQueue) {
            List<RelationshipEdge> edgeList = relationship.getEdgeList();
            List<Attribute> attributeList = relationship.getAttributeList();
            Map<Cardinality, List<Long>> cardinalityListMap = analyzeCardinality(edgeList, mapRelationshipToTable);
            Table representTable;

            if (edgeList.size() == 1) {
                representTable = parseRelationshipWithSingleEntity(cardinalityListMap, tableDTOMap);
            } else {
                if (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 1) {
                    representTable = parseOneToMany(cardinalityListMap, tableDTOMap);
                } else {
                    if (edgeList.size() == 2 &&
                            (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 2)) {
                        representTable = parseTwoOneToOne(cardinalityListMap, tableDTOMap);
                    } else {
                        List<Long> tableIdList = cardinalityListMap.get(Cardinality.OneToOne);
                        tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToOne));
                        tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToMany));
                        tableIdList.addAll(cardinalityListMap.get(Cardinality.OneToMany));
                        representTable = parseCardinalityWithNewTable(tableIdList, tableDTOMap, relationship.getName());
                    }
                }
            }
            parseRelationshipAttribtue(representTable, attributeList, tableDTOMap);

            mapRelationshipToTable.put(relationship.getID(), representTable.getId());
        }
    }


    public static Queue<Relationship> generateRelationshipTopologySeq(List<Relationship> relationshipList) {
        // S is the relationship needs to parse first and R can be parsed when all of S it connected can be parsed
        Map<Long, List<Long>> RMapS = new HashMap<>();
        Map<Long, List<Long>> SMapR = new HashMap<>();
        Map<Long, Relationship> relationshipMap = new HashMap<>();
        Queue<Relationship> relationshipQueue = new LinkedList<>();
        Queue<Relationship> parsableRelationship = new LinkedList<>();

        for (Relationship relationship : relationshipList) {
            relationshipMap.put(relationship.getID(), relationship);
            int numConnected = 0;
            for (RelationshipEdge relationshipEdge : relationship.getEdgeList()) {
                if (relationshipEdge.getConnObjType() == BelongObjType.RELATIONSHIP) {
                    if (RMapS.containsKey(relationship.getID())) {
                        RMapS.get(relationship.getID()).add(relationshipEdge.getConnObj().getID());
                    } else {
                        List<Long> SList = new ArrayList<>();
                        SList.add(relationshipEdge.getConnObj().getID());
                        RMapS.put(relationship.getID(), SList);
                    }

                    if (SMapR.containsKey(relationshipEdge.getConnObj().getID())) {
                        SMapR.get(relationshipEdge.getConnObj().getID()).add(relationship.getID());
                    } else {
                        List<Long> RList = new ArrayList<>();
                        RList.add(relationship.getID());
                        SMapR.put(relationshipEdge.getConnObj().getID(), RList);
                    }
                    numConnected++;
                }
            }

            if (numConnected == 0) {
                parsableRelationship.offer(relationship);
            }
        }

        while (parsableRelationship.size() > 0) {
            Relationship relationship = parsableRelationship.poll();
            relationshipQueue.offer(relationship);

            if (SMapR.containsKey(relationship.getID())) {
                List<Long> allRConnectedToCurrS = SMapR.get(relationship.getID());
                for (Long id : allRConnectedToCurrS) {
                    if (RMapS.containsKey(id)) {
                        RMapS.get(id).remove(relationship.getID());
                        if (RMapS.get(id).size() == 0) {
                            Relationship currR = relationshipMap.get(id);
                            parsableRelationship.offer(currR);
                            RMapS.remove(id);
                        }
                    }
                }
            }
        }

        return relationshipQueue;
    }


    private static void parseWeakEntity(RelationshipEdge keyEdge, List<RelationshipEdge> edges, Map<Long, Table> tableMap) {
        Table weakEntity = tableMap.get(keyEdge.getConnObj().getID());
        for (RelationshipEdge edge : edges) {
            Table strongEntity = tableMap.get(edge.getConnObj().getID());
            List<Column> fkList = new ArrayList<>();
            for (Column strongPk : strongEntity.getPrimaryKey()) {
                Column clonePk = strongPk.getForeignClone(weakEntity.getId(), true, strongEntity.getName());
                fkList.add(clonePk);
                weakEntity.getPrimaryKey().add(clonePk);
                weakEntity.getColumnList().add(clonePk);
            }
            weakEntity.getForeignKey().put(strongEntity.getId(), fkList);
        }
    }


    private static Map<Cardinality, List<Long>> analyzeCardinality(List<RelationshipEdge> relationshipEdges, Map<Long, Long> mapRelationshipToTable) {
        Map<Cardinality, List<Long>> cardinalityListMap = new HashMap<>();
        cardinalityListMap.put(Cardinality.OneToMany, new ArrayList<>());
        cardinalityListMap.put(Cardinality.OneToOne, new ArrayList<>());
        cardinalityListMap.put(Cardinality.ZeroToMany, new ArrayList<>());
        cardinalityListMap.put(Cardinality.ZeroToOne, new ArrayList<>());

        for (RelationshipEdge edge : relationshipEdges) {
            Long tableId = edge.getConnObj().getID();
            if (edge.getConnObjType().equals(BelongObjType.RELATIONSHIP)) {
                tableId = mapRelationshipToTable.get(edge.getConnObj().getID());
            }
            cardinalityListMap.get(edge.getCardinality()).add(tableId);
        }
        return cardinalityListMap;
    }


    private static Table parseRelationshipWithSingleEntity(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, Table> tableDTOMap) {
        Table table = null;
        for (List<Long> ids : cardinalityListMap.values()) {
            if (ids.size() > 0) {
                table = tableDTOMap.get(ids.get(0));
                break;
            }
        }
        return table;
    }


    private static Table parseTwoOneToOne(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, Table> tableDTOMap) {
        Table importedTable;
        Table exportedTable;
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

        List<Column> fkList = new ArrayList<>();
        for (Column pk : exportedTable.getPrimaryKey()) {
            Column fk = pk.getForeignClone(importedTable.getId(), false, exportedTable.getName());
            if (canbeNull)
                fk.setNullable(true);
            importedTable.getColumnList().add(fk);
            fkList.add(fk);
        }
        importedTable.getForeignKey().put(exportedTable.getId(), fkList);
        return importedTable;
    }


    private static Table parseOneToMany(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, Table> tableDTOMap) {
        Long mainTableId;
        boolean canBeZero = false;
        if (cardinalityListMap.get(Cardinality.OneToOne).size() == 1) {
            mainTableId = cardinalityListMap.get(Cardinality.OneToOne).get(0);
        } else {
            canBeZero = true;
            mainTableId = cardinalityListMap.get(Cardinality.ZeroToOne).get(0);
        }

        Table mainTable = tableDTOMap.get(mainTableId);
        List<Column> columnList = mainTable.getColumnList();
        Map<Long, List<Column>> mainTableForeignKey = mainTable.getForeignKey();

        List<Long> foreignTableIdList = cardinalityListMap.get(Cardinality.OneToMany);
        foreignTableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToMany));
        for (Long foreignTableId : foreignTableIdList) {
            Table foreignTable = tableDTOMap.get(foreignTableId);

            List<Column> fkColumns = new ArrayList<>();
            for (Column pk : foreignTable.getPrimaryKey()) {
                Column cloneFk = pk.getForeignClone(mainTableId, false, foreignTable.getName());
                if (canBeZero) {
                    cloneFk.setNullable(true);
                }
                fkColumns.add(cloneFk);
                columnList.add(cloneFk);
            }

            mainTableForeignKey.put(foreignTableId, fkColumns);
        }
        return mainTable;
    }


    private static Table parseCardinalityWithNewTable(List<Long> tableIdList, Map<Long, Table> tableDTOMap, String reName) {

        StringBuilder tableName = new StringBuilder(reName.replace(' ', '_'));
        List<Column> columnList = new ArrayList<>();
        List<Column> pkList = new ArrayList<>();
        Map<Long, List<Column>> foreignKey = new HashMap<>();

        Table newTable = new Table(RandomUtils.generateID(), tableName.toString(), EntityType.STRONG, null,
                columnList, pkList, new ArrayList<>(), foreignKey);

        for (Long tableId : tableIdList) {
            Table foreignTable = tableDTOMap.get(tableId);
            tableName.append("_").append(foreignTable.getName());

            List<Column> fkColumns = new ArrayList<>();
            for (Column pk : foreignTable.getPrimaryKey()) {
                Column cloneFk = pk.getForeignClone(newTable.getId(), true, foreignTable.getName());
                cloneFk.setNullable(false);
                fkColumns.add(cloneFk);
                columnList.add(cloneFk);
                pkList.add(cloneFk);
            }

            foreignKey.put(tableId, fkColumns);
        }

        newTable.setName(tableName.toString());
        tableDTOMap.put(newTable.getId(), newTable);
        return newTable;
    }


    private static void parseRelationshipAttribtue(Table table, List<Attribute> attributeList, Map<Long, Table> tableDTOMap) {
        for (Attribute attribute : attributeList) {
            Column column = new Column();
            if (attribute.getAttributeType() == AttributeType.Optional) {
                column.transformAttribute(attribute, true);
                table.getColumnList().add(column);
            } else if (attribute.getAttributeType() == AttributeType.Mandatory) {
                column.transformAttribute(attribute, false);
                table.getColumnList().add(column);
            } else {
                column.transformAttribute(attribute, false);
                parseMultiValuedColumn(tableDTOMap, table, column);
            }
        }
    }
}
