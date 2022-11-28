package io.github.MigadaTang.util;

import io.github.MigadaTang.*;
import io.github.MigadaTang.common.Cardinality;
import io.github.MigadaTang.common.ConnObjWithCardinality;
import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.exception.ParseException;

import java.util.*;

public class ParserUtil {

    public static Schema parseAttributeToRelationship(List<Table> tableList) throws ParseException {
        Schema schema = ER.createSchema("reverseEng");

        Map<Long, Entity> tableDTOEntityMap = new HashMap<>();

        // table and keys need to be processed specially
        List<Column> foreignKeyList = new ArrayList<>();
        List<Table> tableGenerateByRelationship = new ArrayList<>();
        parseEntity(tableList, tableDTOEntityMap, tableGenerateByRelationship, schema, foreignKeyList);


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

    private static void parseEntity(List<Table> tableList, Map<Long, Entity> tableDTOEntityMap,
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
        for (Entity entity : entityList) {
            Table table = new Table();
            table.tranformEntity(entity);
            tableDTOMap.put(table.getId(), table);
            if (table.getTableType() == EntityType.SUBSET) {
                if (table.getBelongStrongTableID() == null) {
                    throw new ParseException("Weak entity does not belong to any entity.");
                }
                subsetMap.add(table.getId());
            }
        }

        for (Long tableId : subsetMap) {
            Table weakEntity = tableDTOMap.get(tableId);
            Table strongEntity = tableDTOMap.get(weakEntity.getBelongStrongTableID());
            List<Column> strongPk = strongEntity.getPrimaryKey();

            List<Column> weakPk = weakEntity.getPrimaryKey();
            List<Column> weakColumns = weakEntity.getColumnList();
            Map<Long, List<Column>> weakFk = weakEntity.getForeignKey();
            List<Column> newFk = new ArrayList<>();
            for (Column column : strongPk) {
                Column cloneC = column.getForeignClone(tableId, true, strongEntity.getName());
                weakPk.add(cloneC);
                weakColumns.add(cloneC);
                newFk.add(cloneC);
            }
            weakFk.put(strongEntity.getId(), newFk);
            weakEntity.setColumnList(weakColumns);
            weakEntity.setPrimaryKey(weakPk);
            weakEntity.setForeignKey(weakFk);
        }

        // parse relation to new table or new attribute in existing table
        for (Relationship relationship : relationshipList) {
            List<RelationshipEdge> relationshipEdges = relationship.getEdgeList();
            List<Attribute> relationshipAttributeList = relationship.getAttributeList();

            if (relationshipEdges.size() <= 1) {
                continue;
//                throw new ParseException("The relationship only reference to one table. Relationship ID: " + relationship.getID());
            }

            // weak entity
            if (relationshipEdges.size() == 2) {
                if (!tableDTOMap.containsKey(relationshipEdges.get(0).getSchemaID()) ||
                        !tableDTOMap.containsKey(relationshipEdges.get(1).getSchemaID())) {
                    throw new ParseException("The table which relationship reference to does not exist. Relationship ID: " + relationship.getID());
                }
                Table firstTable = tableDTOMap.get(relationshipEdges.get(0).getConnObj().getID());
                Table secondTable = tableDTOMap.get(relationshipEdges.get(1).getConnObj().getID());
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
                    parseTwoOneToOne(cardinalityListMap, tableDTOMap);
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


    private static void parseTwoOneToOne(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, Table> tableDTOMap) {
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
    }


    private static void parseWeakEntity(Table weakEntity, Table strongEntity) {
        List<Column> weakColumns = weakEntity.getColumnList();
        List<Column> weakPk = weakEntity.getPrimaryKey();
        Map<Long, List<Column>> weakFk = weakEntity.getForeignKey();
        List<Column> newFk = new ArrayList<>();

        for (Column strongPkColumn : strongEntity.getPrimaryKey()) {
            Column weakFkColumn = strongPkColumn.getForeignClone(weakEntity.getId(), true, strongEntity.getName());
            weakPk.add(weakFkColumn);
            newFk.add(weakFkColumn);
            weakColumns.add(weakFkColumn);
        }
        weakFk.put(strongEntity.getId(), newFk);
    }


    private static Map<Cardinality, List<Long>> generateCardinalityMap(Map<Long, Table> tableDTOMap, Relationship relationship) throws ParseException {
        Map<Cardinality, List<Long>> cardinalityListMap = new HashMap<>();
        cardinalityListMap.put(Cardinality.OneToMany, new ArrayList<>());
        cardinalityListMap.put(Cardinality.OneToOne, new ArrayList<>());
        cardinalityListMap.put(Cardinality.ZeroToMany, new ArrayList<>());
        cardinalityListMap.put(Cardinality.ZeroToOne, new ArrayList<>());

        for (RelationshipEdge edge : relationship.getEdgeList()) {
            Long tableID = edge.getConnObj().getID();
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


    private static void parseOneToMany(Map<Cardinality, List<Long>> cardinalityListMap, Map<Long, Table> tableDTOMap, List<Attribute> attributeList) throws ParseException {
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
            if (foreignTable.getPrimaryKey().size() == 0) {
                throw new ParseException("Relationship fail to connect to the primary key of table : " + foreignTable.getName());
            }

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

        for (Attribute attribute : attributeList) {
            Column column = new Column();
            column.transformAttribute(attribute);
            columnList.add(column);
        }
    }


    private static void parseCardinalityWithNewTable(List<Long> tableIdList, Map<Long, Table> tableDTOMap, String reName, List<Attribute> attributeList) {

        StringBuilder tableName = new StringBuilder(reName.replace(' ', '_'));
        List<Column> columnList = new ArrayList<>();
        Map<Long, List<Column>> foreignKey = new HashMap<>();

        Table newTable = new Table(RandomUtils.generateID(), tableName.toString(), EntityType.STRONG, null,
                columnList, new ArrayList<>(), foreignKey);

        for (Long tableId : tableIdList) {
            Table foreignTable = tableDTOMap.get(tableId);
            tableName.append("_").append(foreignTable.getName());

            List<Column> fkColumns = new ArrayList<>();
            for (Column pk : foreignTable.getPrimaryKey()) {
                Column cloneFk = pk.getForeignClone(newTable.getId(), false, foreignTable.getName());
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
            Column column = new Column();
            column.transformAttribute(attribute);
            columnList.add(column);
        }

        newTable.setName(tableName.toString());
        tableDTOMap.put(newTable.getId(), newTable);
    }
}
