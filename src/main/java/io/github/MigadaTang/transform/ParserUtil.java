package io.github.MigadaTang.transform;

import io.github.MigadaTang.*;
import io.github.MigadaTang.common.*;
import io.github.MigadaTang.exception.ERException;
import io.github.MigadaTang.exception.ParseException;

import java.util.*;

public class ParserUtil {

    public static Schema parseAttributeToRelationship(List<Table> tableList) throws ParseException {
        Schema schema = ER.createSchema("reverseEng");

        Map<Long, Entity> tableDTOEntityMap = new HashMap<>();

        // table and keys need to be processed specially
        List<Column> foreignKeyList = new ArrayList<>();
        List<Table> tableGenerateByRelationship = new ArrayList<>();
        List<Table> possibleMultiValuedSet = new ArrayList<>();
        parseTableToEntity(tableList, tableDTOEntityMap, tableGenerateByRelationship, schema, foreignKeyList, possibleMultiValuedSet);

//        for (Table table: tableList) {
//            System.out.println(table.getName() + ", " + table.getForeignKey());
//        }

        // parser foreign key, (1-N)
        for (Column foreignKey : foreignKeyList) {
            Entity curEntity = tableDTOEntityMap.get(foreignKey.getBelongTo());
            Entity pointToEntity = tableDTOEntityMap.get(foreignKey.getForeignKeyTable());

            System.out.println("Foreign key: " + foreignKey.getName());
            System.out.println("curEntity: " + curEntity.getName());
            System.out.println("pointToEntity: " + pointToEntity.getName());

            // The relationshipName can't be "unknow", the name of the relationship name can get by extract
            // the name of the table. No idea when will this case happen...

            String relationshipName = curEntity.getName() + "_" + pointToEntity.getName();
            if (foreignKey.getName().contains(pointToEntity.getName())) {
                foreignKey.setName(foreignKey.getName().replaceAll("_", " "));
                relationshipName = foreignKey.getName().replace(pointToEntity.getName(), "");
            }
            if (relationshipName.equals("")) {
                relationshipName = curEntity.getName() + "_" + pointToEntity.getName();
            }

            if (foreignKey.isNullable()) {
                if (curEntity != null && pointToEntity != null) {
                    Relationship newRelationship = schema.createRelationship(relationshipName, curEntity, pointToEntity,
                        Cardinality.ZeroToOne,
                        Cardinality.OneToMany);
//                    System.out.println("newRelationship: " + newRelationship.getName());
                }
            } else {
                if (curEntity != null && pointToEntity != null) {
                    Relationship newRelationship = schema.createRelationship(relationshipName, curEntity, pointToEntity,
                        Cardinality.OneToOne,
                        Cardinality.OneToMany);
//                    System.out.println("newRelationship: " + newRelationship.getName());
                }
            }
        }

        // parser table generate by relationship
        Map<Long, Relationship> tableDTORelationshipMap = new HashMap<>();
        for (Table table : tableGenerateByRelationship) {
//            if (table.getName().equals("city_local_name") || table.getName().equals("city_other_name")) continue; ///
            List<ConnObjWithCardinality> connObjWithCardinalityList = new ArrayList<>();
            Set<Long> foreignTableList = new HashSet<>();

            // The name of the relationship should be extracted from the name of the table
//            System.out.println("TableName by relationship: " + table.getName());
            String relationshipName = table.getName().split("_")[0]; // extract the name of the relationship
//            Relationship relationship = schema.createEmptyRelationship("unknow");
            Relationship relationship = schema.createEmptyRelationship(table.getName());

            table.setId(relationship.getID());

            relationship.setReflexive(table.getReflexive());

            for (Column column : table.getColumnList()) {
                if (column.getForeignKeyTable() != null) {
//                if (column.getForeignKeyTable() != null && !foreignTableList.contains(column.getForeignKeyTable())) {
                    foreignTableList.add(column.getForeignKeyTable());
                    if (tableDTOEntityMap.get(column.getForeignKeyTable()) != null) { ///
                        relationship.linkObj(tableDTOEntityMap.get(column.getForeignKeyTable()), Cardinality.ZeroToMany);
                    }
                    // todo: check whether optional?
                } else {
                    DataType dataType = null;
                    if (column.getDataType().equals("varchar")) {
                        dataType = DataType.VARCHAR;
                    } else if (column.getDataType().equals("int4")) {
                        dataType = DataType.INT4;
                    } else if (column.getDataType().equals("numeric")) {
                        dataType = DataType.NUMERIC;
                    } else if (column.getDataType().equals("date")) {
                        dataType = DataType.DATE;
                    } else {
                        dataType = DataType.TEXT;
                    }
                    if (column.isNullable()) {
                        Attribute attribute = relationship.addAttribute(column.getName(), dataType, AttributeType.Optional);
                        column.setID(attribute.getID());
                    }
                    else {
                        Attribute attribute = relationship.addAttribute(column.getName(), dataType, AttributeType.Mandatory);
                        column.setID(attribute.getID());
                    }
                }
            }
            tableDTORelationshipMap.put(table.getId(), relationship);
        }

        for (Table multiValued : possibleMultiValuedSet) {
            ERConnectableObj mainObj;
            Column multiValuedColumn = null;
            for (Column column : multiValued.getColumnList()) {
                if (!column.isForeign()) {
                    multiValuedColumn = column;
                }
            }
            if (multiValuedColumn == null) {
                continue;
            }
            if (tableDTOEntityMap.containsKey(multiValued.getBelongStrongTableID())) {
                Entity entity = tableDTOEntityMap.get(multiValued.getBelongStrongTableID());
                DataType dataType = null;
                if (multiValuedColumn.getDataType().equals("varchar")) {
                    dataType = DataType.VARCHAR;
                } else if (multiValuedColumn.getDataType().equals("int4")) {
                    dataType = DataType.INT4;
                } else if (multiValuedColumn.getDataType().equals("numeric")) {
                    dataType = DataType.NUMERIC;
                } else if (multiValuedColumn.getDataType().equals("date")) {
                    dataType = DataType.DATE;
                } else {
                    dataType = DataType.TEXT;
                }
                Attribute attribute = entity.addAttribute(multiValuedColumn.getName(), dataType, AttributeType.Both);
                multiValuedColumn.setID(attribute.getID());
            } else if (tableDTORelationshipMap.containsKey(multiValued.getBelongStrongTableID())) {
                Relationship relationship = tableDTORelationshipMap.get(multiValued.getBelongStrongTableID());
                DataType dataType = null;
                if (multiValuedColumn.getDataType().equals("varchar")) {
                    dataType = DataType.VARCHAR;
                } else if (multiValuedColumn.getDataType().equals("int4")) {
                    dataType = DataType.INT4;
                } else if (multiValuedColumn.getDataType().equals("numeric")) {
                    dataType = DataType.NUMERIC;
                } else if (multiValuedColumn.getDataType().equals("date")) {
                    dataType = DataType.DATE;
                } else {
                    dataType = DataType.TEXT;
                }
                Attribute attribute = relationship.addAttribute(multiValuedColumn.getName(), dataType, AttributeType.Both);
                multiValuedColumn.setID(attribute.getID());
            }
        }

//        System.out.println("11111: " + schema.getEntityList());
//        for (Entity entity: schema.getEntityList()) {
//            System.out.println("entity: " + entity);
//        }
        for (Relationship relationship: schema.getRelationshipList()) {
//            System.out.println("relationship: " + relationship);
            System.out.println("RELATIONSHIP: " + relationship.getName() + ", edges size: " + relationship.getEdgeList().size());
            for (RelationshipEdge relationshipEdge: relationship.getEdgeList()) {
                System.out.println("edgeee: " + relationshipEdge.getConnObj().getName());
            }
        }

        return schema;
    }

    private static void parseTableToEntity(List<Table> tableList, Map<Long, Entity> tableDTOEntityMap,
                                           List<Table> tableGenerateByRelationship, Schema schema,
                                           List<Column> foreignKeyList, List<Table> possibleMultiValuedSet) throws ParseException {
        List<Table> possibleWeakEntitySet = new ArrayList<>();
        List<Table> possibleSubsetSet = new ArrayList<>();

        List<Table> tablesRelyOnNonStrongEntity = new ArrayList<>();
        Map<Long, Table> tableDTOTableMap = new HashMap<>();
        Map<Long, Long> idMap = new HashMap<>();

        // parse strong entity
        for (Table strongEntity : tableList) {
            List<Column> columnList = strongEntity.getColumnList();

            int pkIsFk = 0;
            int fkNum = 0;
            int pkColNum = 0;
            Set<Long> fkTables = new HashSet<>();
            Set<Long> pkFkTable = new HashSet<>();
//            List<Long> pkFkTable = new ArrayList<>(); ///
            Long fkTableId = null;

            boolean isReflexive = false;

            for (Column column : strongEntity.getColumnList()) {
                if (column.isForeign()) {
                    fkNum++;
                    fkTables.add(column.getForeignKeyTable());
                    if (column.isPrimary()) {
                        pkIsFk++;
                        if (fkTableId != null && fkTableId.equals(column.getForeignKeyTable())) {
                            isReflexive = true;
                        }
                        fkTableId = column.getForeignKeyTable();
                        pkFkTable.add(column.getForeignKeyTable());
                    }
                }
                if (column.isPrimary()) {
                    pkColNum++;
                }
            }

//            System.out.println("pkColNum: " + pkColNum);
//            System.out.println("pkIsFk: " + pkIsFk);
//            System.out.println("pkFkTable.size(): " + pkFkTable.size());
//            System.out.println("fkNum: " + fkNum);
//            System.out.println("columnList.size(): " + columnList.size());

            // 特殊情况下 pkFkTable.size() 可能会大于1，例如"province_other_name". 暂时删除该条件
            if (pkColNum == columnList.size() && pkFkTable.size() >= 1 && pkIsFk == columnList.size()-1) {
//            if (pkColNum == columnList.size() && pkIsFk == columnList.size()-1) {
                possibleMultiValuedSet.add(strongEntity);
                strongEntity.setBelongStrongTableID(fkTableId);
                continue;
            }

            // 暂时没问题
            if (pkIsFk == strongEntity.getPrimaryKey().size() && pkFkTable.size() == 1 && pkIsFk > 0 && !isReflexive) {
//            if (pkColNum > pkIsFk && pkFkTable.size() == 1 && pkIsFk > 0) {
                // may still have problem here, here we assume that subset must have its own primary key.
                strongEntity.setBelongStrongTableID(fkTableId);
                possibleSubsetSet.add(strongEntity);
                continue;
            }

            if (pkIsFk > 0 && pkFkTable.size() == 1 && pkColNum == pkIsFk + 1) {
//                System.out.println("find weak entity: " + strongEntity.getName());
                possibleWeakEntitySet.add(strongEntity);
                strongEntity.setBelongStrongTableID(fkTableId);
                continue;
            }

            if (isReflexive && pkColNum == pkIsFk && pkFkTable.size() == 1) {
                strongEntity.setReflexive(true);
                tableGenerateByRelationship.add(strongEntity);
                continue;
            }

            if (fkNum == pkIsFk && pkFkTable.size() > 1) {
                tableGenerateByRelationship.add(strongEntity);
                continue;
            }

            Entity entity = schema.addEntity(strongEntity.getName());
//            entity.setID(strongEntity.getId()); // todo:

            Long previousStrongEntityId = strongEntity.getId();
            strongEntity.setId(entity.getID());
            idMap.put(previousStrongEntityId, entity.getID());

            strongEntity.setEntityID(entity.getID()); ///

            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : columnList) {
//                if (strongEntity.getName().equals("organization")) {
//                    System.out.println("Column: " + column.getName() + ", " + column.getForeignKeyColumnName() + ", " + column.getForeignKeyTable());
//                }
                if (column.isForeign()) {
                    if (!foreignTableList.contains(column.getForeignKeyTable())) {
//                        System.out.println("column: " + column.getName());
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    DataType dataType = null;
                    if (column.getDataType().equals("varchar")) {
                        dataType = DataType.VARCHAR;
                    } else if (column.getDataType().equals("int4")) {
                        dataType = DataType.INT4;
                    } else if (column.getDataType().equals("numeric")) {
                        dataType = DataType.NUMERIC;
                    } else if (column.getDataType().equals("date")) {
                        dataType = DataType.DATE;
                    } else {
                        dataType = DataType.TEXT;
                    }
                    if (column.isNullable()) {
                        Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Optional);
                        Long previousColumnId = column.getID();
                        column.setID(attribute.getID());
                        strongEntity.getColumnIdsMap().put(previousColumnId, attribute.getID());
//                        System.out.println(attribute.getID() + ", " + column.getID());
                    } else {
                        Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Mandatory);
                        Long previousColumnId = column.getID();
                        column.setID(attribute.getID());
                        strongEntity.getColumnIdsMap().put(previousColumnId, attribute.getID());
//                        System.out.println(attribute.getID() + ", " + column.getID());
                    }

                }
            }

            tableDTOEntityMap.put(strongEntity.getId(), entity);
            tableDTOTableMap.put(strongEntity.getId(), strongEntity);
        }

        System.out.println("possibleWeakEntitySet: " + possibleWeakEntitySet.size());
        System.out.println("possibleSubsetSet: " + possibleSubsetSet.size());
        System.out.println("possibleMultiValuedSet: " + possibleMultiValuedSet.size());

        for (Table weakEntity: possibleWeakEntitySet) {
            for (Long previousId: idMap.keySet()) {
                if (weakEntity.getBelongStrongTableID().equals(previousId)) {
                    weakEntity.setBelongStrongTableID(idMap.get(previousId));
                }
            }
        }
        for (Table subset: possibleSubsetSet) {
            for (Long previousId: idMap.keySet()) {
                if (subset.getBelongStrongTableID().equals(previousId)) {
                    subset.setBelongStrongTableID(idMap.get(previousId));
                }
            }
        }
        for (Table multiValued: possibleMultiValuedSet) {
            for (Long previousId: idMap.keySet()) {
                if (multiValued.getBelongStrongTableID().equals(previousId)) {
                    multiValued.setBelongStrongTableID(idMap.get(previousId));
                }
            }
        }

        for (Long id: tableDTOEntityMap.keySet()) {
            System.out.println(id + ", " + tableDTOEntityMap.get(id));
        }

        for (Table weakEntity : possibleWeakEntitySet) {
            if (!tableDTOEntityMap.containsKey(weakEntity.getBelongStrongTableID())) {
//                throw new ParseException("Api only support weak entity relies on strong entity for current version");
                tablesRelyOnNonStrongEntity.add(weakEntity);
                continue;
            }
//            System.out.println("id: " + weakEntity.getBelongStrongTableID());
//            System.out.println("strong entity: " + tableDTOEntityMap.get(weakEntity.getBelongStrongTableID()));

            Entity entity = schema.addWeakEntity(weakEntity.getName(), tableDTOEntityMap.get(weakEntity.getBelongStrongTableID()),
                    weakEntity.getName() + "_" + tableDTOEntityMap.get(weakEntity.getBelongStrongTableID()).getName(), Cardinality.OneToOne, Cardinality.ZeroToMany).getLeft(); ///

//            System.out.println("weak entity relying on strong entity1: " + tableDTOEntityMap.get(weakEntity.getBelongStrongTableID()).getName());

            Long previousId = weakEntity.getId();
            weakEntity.setId(entity.getID());
            idMap.put(previousId, entity.getID());

            weakEntity.setEntityID(entity.getID()); ///??

            tableDTOEntityMap.put(weakEntity.getId(), entity);
            tableDTOTableMap.put(weakEntity.getId(), weakEntity);

            List<Column> columnList = weakEntity.getColumnList();
            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : columnList) {
                if (column.isForeign()) {
                    // When will this situation happen?
                    if (!foreignTableList.contains(column.getForeignKeyTable()) &&
                            !column.getForeignKeyTable().equals(weakEntity.getBelongStrongTableID()) && !column.isPrimary()) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    DataType dataType = null;
                    if (column.getDataType().equals("varchar")) {
                        dataType = DataType.VARCHAR;
                    } else if (column.getDataType().equals("int4")) {
                        dataType = DataType.INT4;
                    } else if (column.getDataType().equals("numeric")) {
                        dataType = DataType.NUMERIC;
                    } else if (column.getDataType().equals("date")) {
                        dataType = DataType.DATE;
                    } else {
                        dataType = DataType.TEXT;
                    }
                    if (column.isNullable()) {
                        Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Optional);
                        Long previousColumnId = column.getID();
                        column.setID(attribute.getID());
                        weakEntity.getColumnIdsMap().put(previousColumnId, attribute.getID());
                    }
                    else {
                        Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Mandatory);
                        Long previousColumnId = column.getID();
                        column.setID(attribute.getID());
                        weakEntity.getColumnIdsMap().put(previousColumnId, attribute.getID());
                    }
                }
            }
        }
        System.out.println("here ----------------- ");

        for (Table subset : possibleSubsetSet) {
//            System.out.println("Subset: " + subset);
            if (!tableDTOEntityMap.containsKey(subset.getBelongStrongTableID())) {
//                throw new ParseException("Api only support subset relies on strong entity for current version");
                tablesRelyOnNonStrongEntity.add(subset);
                continue;
            }
            System.out.println("subset getBelongStrongTableID: " + subset.getBelongStrongTableID());
            System.out.println("subset strong entity: " + tableDTOEntityMap.get(subset.getBelongStrongTableID()));

            Entity entity = schema.addSubset(subset.getName(), tableDTOEntityMap.get(subset.getBelongStrongTableID()));

//            System.out.println("subset relying on strong entity1: " + tableDTOEntityMap.get(subset.getBelongStrongTableID()).getName());

            Long previousId = subset.getId();
            subset.setId(entity.getID());
            idMap.put(previousId, entity.getID());

            subset.setEntityID(entity.getID()); ///

            tableDTOEntityMap.put(subset.getId(), entity);
            tableDTOTableMap.put(subset.getId(), subset);

            List<Column> columnList = subset.getColumnList();
            Set<Long> foreignTableList = new HashSet<>();
            for (Column column : columnList) {
                if (column.isForeign()) {
                    // This will never happen? Because the foreign key of the subset must belong to
                    // its relying strong entity? The foreign key can point to other entities!
//                    if (!foreignTableList.contains(column.getForeignKeyTable()) &&
//                            !column.getForeignKeyTable().equals(subset.getBelongStrongTableID())) {
                    // todo: check the result
                    if (!foreignTableList.contains(column.getForeignKeyTable()) && !column.isPrimary()) {
                        foreignKeyList.add(column);
                        foreignTableList.add(column.getForeignKeyTable());
                    }
                } else {
                    DataType dataType = null;
                    if (column.getDataType().equals("varchar")) {
                        dataType = DataType.VARCHAR;
                    } else if (column.getDataType().equals("int4")) {
                        dataType = DataType.INT4;
                    } else if (column.getDataType().equals("numeric")) {
                        dataType = DataType.NUMERIC;
                    } else if (column.getDataType().equals("date")) {
                        dataType = DataType.DATE;
                    } else {
                        dataType = DataType.TEXT;
                    }
                    if (column.isNullable()) {
                        Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Optional);
                        Long previousColumnId = column.getID();
                        column.setID(attribute.getID());
                        subset.getColumnIdsMap().put(previousColumnId, attribute.getID());
                    }
                    else {
                        Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Mandatory);
                        Long previousColumnId = column.getID();
                        column.setID(attribute.getID());
                        subset.getColumnIdsMap().put(previousColumnId, attribute.getID());
                    }
                }
            }
        }

        for (Table weakEntity: possibleWeakEntitySet) {
            for (Long previousId: idMap.keySet()) {
                if (weakEntity.getBelongStrongTableID().equals(previousId)) {
                    weakEntity.setBelongStrongTableID(idMap.get(previousId));
                }
            }
        }
        for (Table subset: possibleSubsetSet) {
            for (Long previousId: idMap.keySet()) {
                if (subset.getBelongStrongTableID().equals(previousId)) {
                    subset.setBelongStrongTableID(idMap.get(previousId));
                }
            }
        }
        for (Table multiValued: possibleMultiValuedSet) {
            for (Long previousId: idMap.keySet()) {
                if (multiValued.getBelongStrongTableID().equals(previousId)) {
                    multiValued.setBelongStrongTableID(idMap.get(previousId));
                }
            }
        }

        for (Long id: tableDTOEntityMap.keySet()) {
            System.out.println(id + ", " + tableDTOEntityMap.get(id));
        }

        System.out.println("--------------");

        while (tablesRelyOnNonStrongEntity.size() > 0) {
            List<Table> recursiveList = new ArrayList<>();
            for (Table table: tablesRelyOnNonStrongEntity) {

//                System.out.println("im here: " + table);
                System.out.println(possibleWeakEntitySet.contains(table));
                if (possibleWeakEntitySet.contains(table)) {
                    if (!tableDTOEntityMap.containsKey(table.getBelongStrongTableID())) {
                        recursiveList.add(table);
                        continue;
                    }

                    Entity entity = schema.addWeakEntity(table.getName(), tableDTOEntityMap.get(table.getBelongStrongTableID()),
                        table.getName() + "_" + tableDTOEntityMap.get(table.getBelongStrongTableID()).getName(), Cardinality.OneToOne, Cardinality.ZeroToMany).getLeft(); ///

//                    System.out.println("weak entity: " + table.getName());
//                    System.out.println("weak entity relying on strong entity2: " + tableDTOEntityMap.get(table.getBelongStrongTableID()).getName());
                    Long previousId = table.getId();
                    table.setId(entity.getID());
                    idMap.put(previousId, entity.getID());

                    table.setEntityID(entity.getID()); ///??

                    tableDTOEntityMap.put(table.getId(), entity);
                    tableDTOTableMap.put(table.getId(), table);

                    List<Column> columnList = table.getColumnList();
                    Set<Long> foreignTableList = new HashSet<>();
                    for (Column column : columnList) {
                        if (column.isForeign()) {
                            // When will this situation happen?
                            if (!foreignTableList.contains(column.getForeignKeyTable()) &&
                                !column.getForeignKeyTable().equals(table.getBelongStrongTableID()) && !column.isPrimary()) {
                                foreignKeyList.add(column);
                                foreignTableList.add(column.getForeignKeyTable());
                            }
                        } else {
                            DataType dataType = null;
                            if (column.getDataType().equals("varchar")) {
                                dataType = DataType.VARCHAR;
                            } else if (column.getDataType().equals("int4")) {
                                dataType = DataType.INT4;
                            } else if (column.getDataType().equals("numeric")) {
                                dataType = DataType.NUMERIC;
                            } else if (column.getDataType().equals("date")) {
                                dataType = DataType.DATE;
                            } else {
                                dataType = DataType.TEXT;
                            }
                            if (column.isNullable()) {
                                Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Optional);
                                Long previousColumnId = column.getID();
                                column.setID(attribute.getID());
                                table.getColumnIdsMap().put(previousColumnId, attribute.getID());
                            }
                            else {
                                Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Mandatory);
                                Long previousColumnId = column.getID();
                                column.setID(attribute.getID());
                                table.getColumnIdsMap().put(previousColumnId, attribute.getID());
                            }
                        }
                    }

//                    tablesRelyOnNonStrongEntity.remove(table);
                } else if (possibleSubsetSet.contains(table)) {

                    if (!tableDTOEntityMap.containsKey(table.getBelongStrongTableID())) {
                        recursiveList.add(table);
                        continue;
                    }

                    Entity entity = schema.addSubset(table.getName(), tableDTOEntityMap.get(table.getBelongStrongTableID()));

//                    System.out.println("subset: " + table.getName());
//                    System.out.println("subset relying on strong entity2: " + tableDTOEntityMap.get(table.getBelongStrongTableID()).getName());

                    Long previousId = table.getId();
                    table.setId(entity.getID());
                    idMap.put(previousId, entity.getID());

                    table.setEntityID(entity.getID()); ///

                    tableDTOEntityMap.put(table.getId(), entity);
                    tableDTOTableMap.put(table.getId(), table);

                    List<Column> columnList = table.getColumnList();
                    Set<Long> foreignTableList = new HashSet<>();
                    for (Column column : columnList) {
                        if (column.isForeign()) {
                            // This will never happen? Because the foreign key of the subset must belong to
                            // its relying strong entity?
//                            if (!foreignTableList.contains(column.getForeignKeyTable()) &&
//                                !column.getForeignKeyTable().equals(table.getBelongStrongTableID())) {
                            if (!foreignTableList.contains(column.getForeignKeyTable()) && !column.isPrimary()) {
                                foreignKeyList.add(column);
                                foreignTableList.add(column.getForeignKeyTable());
                            }
                        } else {
                            DataType dataType = null;
                            if (column.getDataType().equals("varchar")) {
                                dataType = DataType.VARCHAR;
                            } else if (column.getDataType().equals("int4")) {
                                dataType = DataType.INT4;
                            } else if (column.getDataType().equals("numeric")) {
                                dataType = DataType.NUMERIC;
                            } else if (column.getDataType().equals("date")) {
                                dataType = DataType.DATE;
                            } else {
                                dataType = DataType.TEXT;
                            }
                            if (column.isNullable()) {
                                Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Optional);
                                Long previousColumnId = column.getID();
                                column.setID(attribute.getID());
                                table.getColumnIdsMap().put(previousColumnId, attribute.getID());
                            }
                            else {
                                Attribute attribute = entity.addAttribute(column.getName(), dataType, column.isPrimary(), AttributeType.Mandatory);
                                Long previousColumnId = column.getID();
                                column.setID(attribute.getID());
                                table.getColumnIdsMap().put(previousColumnId, attribute.getID());
                            }
                        }
                    }

//                    tablesRelyOnNonStrongEntity.remove(table);
                } else {
                    throw new ParseException("cannot deal with the entity: " + table.getName());
                }
            }
            tablesRelyOnNonStrongEntity = recursiveList;
            for (Table weakEntity: possibleWeakEntitySet) {
                for (Long previousId: idMap.keySet()) {
                    if (weakEntity.getBelongStrongTableID().equals(previousId)) {
                        weakEntity.setBelongStrongTableID(idMap.get(previousId));
                    }
                }
            }
            for (Table subset: possibleSubsetSet) {
                for (Long previousId: idMap.keySet()) {
                    if (subset.getBelongStrongTableID().equals(previousId)) {
                        subset.setBelongStrongTableID(idMap.get(previousId));
                    }
                }
            }
            for (Table multiValued: possibleMultiValuedSet) {
                for (Long previousId: idMap.keySet()) {
                    if (multiValued.getBelongStrongTableID().equals(previousId)) {
                        multiValued.setBelongStrongTableID(idMap.get(previousId));
                    }
                }
            }

//            for (Long id: tableDTOEntityMap.keySet()) {
//                System.out.println(id + ", " + tableDTOEntityMap.get(id));
//            }
        }

//        for (Column column: foreignKeyList) {
//            for (Long previousId: idMap.keySet()) {
//                if (column.getForeignKeyTable().equals(previousId)) {
//                    Long newTableId = idMap.get(previousId);
//                    Table foreignTable = tableDTOTableMap.get(newTableId);
//                    column.setForeignKeyTable(newTableId);
//                    for (Long previousColumnId: foreignTable.getColumnIdsMap().keySet()) {
//                        if (previousColumnId.equals(column.getForeignKeyColumn())) {
//                            column.setForeignKeyColumn(foreignTable.getColumnIdsMap().get(previousColumnId));
//                            column.setID(foreignTable.getColumnIdsMap().get(previousColumnId));
//                            break;
//                        }
//                    }
//                    break;
//                }
//            }
//            for (Long previousId: idMap.keySet()) {
//                if (column.getBelongTo().equals(previousId)) {
//                    Long newTableId = idMap.get(previousId);
//                    column.setBelongTo(newTableId);
//                    break;
//                }
//            }
//        }

        for (Table table: tableList) {
            for (Column column: table.getColumnList()) {
                if (column.isForeign()) {

                    for (Long previousId: idMap.keySet()) {
                        if (previousId.equals(column.getForeignKeyTable())) {
                            // set ForeignKeyTable id
                            Long newForeignTableId = idMap.get(previousId);
                            Table foreignTable = tableDTOTableMap.get(newForeignTableId);

                            // find the foreign key and set its id
//                            boolean findForeignKeyColumn = false;
                            for (Long previousColumnId: foreignTable.getColumnIdsMap().keySet()) {
                                if (previousColumnId.equals(column.getForeignKeyColumn())) {
                                    column.setForeignKeyTable(newForeignTableId);
                                    Long newColumnId = foreignTable.getColumnIdsMap().get(previousColumnId);
                                    column.setForeignKeyColumn(newColumnId);
                                    table.getColumnIdsMap().put(column.getID(), newColumnId);
                                    column.setID(newColumnId);
//                                    findForeignKeyColumn = true;
                                    break;
                                }
                            }

//                            if (!findForeignKeyColumn) {
//                                for (Column foreignTableColumn: foreignTable.getColumnList()) {
//                                    if (foreignTableColumn.getID().equals(column.getForeignKeyColumn())) {
//                                        Long foreignForeignTableId = foreignTableColumn.getForeignKeyTable();
//
//                                        for (Long tableId: idMap.keySet()) {
//                                            if (tableId.equals(foreignForeignTableId)) {
//                                                Long newForeignForeignTableId = idMap.get(foreignForeignTableId);
//                                                Table foreignForeignTable = tableDTOTableMap.get(newForeignForeignTableId);
//
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//

                            break;
                        }
                    }
                    for (Long previousId: idMap.keySet()) {
                        if (column.getBelongTo().equals(previousId)) {
                            Long newTableId = idMap.get(previousId);
                            column.setBelongTo(newTableId);
                            break;
                        }
                    }

                }
            }

            Map<Long, List<Column>> foreignKeyMap = table.getForeignKey();
            Map<Long, List<Column>> newMap = new HashMap<>();
            for (Long id: foreignKeyMap.keySet()) {
                for (Long previousId: idMap.keySet()) {
                    if (id.equals(previousId)) {
                        Long newId = idMap.get(previousId);
                        List<Column> columns = foreignKeyMap.get(previousId);
                        newMap.put(newId, columns);
                    }
                }
            }
            table.setForeignKey(newMap);
        }

        for (Table table: tableList) {
            for (Column column: table.getColumnList()) {
                if (column.isForeign()) {

                    for (Long previousId: idMap.keySet()) {
                        if (previousId.equals(column.getForeignKeyTable())) {
                            // set ForeignKeyTable id
                            Long newForeignTableId = idMap.get(previousId);
                            Table foreignTable = tableDTOTableMap.get(newForeignTableId);

                            // find the foreign key and set its id
//                            boolean findForeignKeyColumn = false;
                            for (Long previousColumnId: foreignTable.getColumnIdsMap().keySet()) {
                                if (previousColumnId.equals(column.getForeignKeyColumn())) {
                                    column.setForeignKeyTable(newForeignTableId);
                                    Long newColumnId = foreignTable.getColumnIdsMap().get(previousColumnId);
                                    column.setForeignKeyColumn(newColumnId);
                                    table.getColumnIdsMap().put(column.getID(), newColumnId);
                                    column.setID(newColumnId);
//                                    findForeignKeyColumn = true;
//                                    table.getColumnIdsMap().put(previousColumnId, newColumnId);
                                    break;
                                }
                            }

                            break;
                        }
                    }

                }
            }
        }

        for (Table table: tableList) {
            for (Column column: table.getColumnList()) {
                if (column.isForeign()) {

                    for (Long previousId: idMap.keySet()) {
                        if (previousId.equals(column.getForeignKeyTable())) {
                            // set ForeignKeyTable id
                            Long newForeignTableId = idMap.get(previousId);
                            Table foreignTable = tableDTOTableMap.get(newForeignTableId);

                            // find the foreign key and set its id
//                            boolean findForeignKeyColumn = false;
                            for (Long previousColumnId: foreignTable.getColumnIdsMap().keySet()) {
                                if (previousColumnId.equals(column.getForeignKeyColumn())) {
                                    column.setForeignKeyTable(newForeignTableId);
                                    Long newColumnId = foreignTable.getColumnIdsMap().get(previousColumnId);
                                    column.setForeignKeyColumn(newColumnId);
                                    table.getColumnIdsMap().put(column.getID(), newColumnId);
                                    column.setID(newColumnId);
//                                    findForeignKeyColumn = true;
//                                    table.getColumnIdsMap().put(previousColumnId, newColumnId);
                                    break;
                                }
                            }

                            break;
                        }
                    }

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

//        for (Long id: tableDTOMap.keySet()) {
//            System.out.println("5555555: " + id);
//            System.out.println("6666666: " + tableDTOMap.get(id));
//        }
        // parse relation to new table or new attribute in existing table
        parseRelationships(relationshipList, tableDTOMap);

//        for (Long id: tableDTOMap.keySet()) {
//            System.out.println("7777777: " + id);
//            System.out.println("8888888: " + tableDTOMap.get(id));
//        }
//        for (Table table: tableDTOMap.values()) {
//            System.out.println("00000000000: " + table.getName());
//        }

        return tableDTOMap;
    }

//    public static Map<Long, Table> parseRelationshipsToAttribute(Schema schema, List<Entity> entityList, List<Relationship> relationshipList) throws ParseException {
//
//        Map<Long, Table> tableDTOMap = new HashMap<>();
//        List<Long> subsetMap = new ArrayList<>();
//
//        // parse all entities to table
//        parseEntityToTable(schema.getOldTables(), entityList, tableDTOMap, subsetMap);
//
//        // parse multi valued column
//        parseMultiValuedColumn(tableDTOMap, schema.getOldTables());
//
//        // parse subset: add pk from strong entity to subset
//        parseSubSet(tableDTOMap, subsetMap);
//
//        // parse relation to new table or new attribute in existing table
//        parseRelationships(relationshipList, tableDTOMap);
//
//        return tableDTOMap;
//    }


    private static void parseEntityToTable(List<Table> oldTables, List<Entity> entityList, Map<Long, Table> tableDTOMap, List<Long> subsetMap) {
        for (Entity entity : entityList) {
            Table table = new Table();
            table.tranformEntity(entity);
            table.setEntityID(entity.getID()); ///
            tableDTOMap.put(table.getId(), table);
            if (table.getTableType() == EntityType.SUBSET) {
                subsetMap.add(table.getId());
            }
        }
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
                Column cloneC = column.getForeignClone(tableId, true, strongEntity.getName(), column.isNullable());
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
        List<Table> newts = new ArrayList<>();
        for (Table table : tableDTOMap.values()) {
            List<Column> multiValuedColumns = table.getMultiValuedColumn();
            if (multiValuedColumns.size() == 0)
                continue;

            for (Column column : multiValuedColumns) {
                Table newT = parseMultiValuedColumn(tableDTOMap, table, column);
                newT.setId(column.getID()); // todo:
                newts.add(newT);
            }
        }

        for (Table newt : newts) {
            tableDTOMap.put(newt.getId(), newt);
        }
    }


    private static Table parseMultiValuedColumn(Map<Long, Table> tableDTOMap, Table table, Column column) {
        List<Column> columnList = new ArrayList<>();
        List<Column> fkList = new ArrayList<>();
        List<Column> pkList = new ArrayList<>();
        columnList.add(column);
        pkList.add(column);
//        Long newTableId = RandomUtils.generateID(); ///
        Long newTableId = column.getID();
        for (Column pk : table.getPrimaryKey()) {
            Column pkClone = pk.getForeignClone(newTableId, true, "", pk.isNullable()); // todo: change isPk to true
            columnList.add(pkClone);
            fkList.add(pkClone);
            pkList.add(pkClone);
        }
        Map<Long, List<Column>> fk = new HashMap<>();
        fk.put(table.getId(), fkList);
        Table newT = new Table(newTableId, table.getName() + "_" + column.getName(), EntityType.STRONG,
                null, columnList, pkList, new ArrayList<>(), fk, null, false, null); ///
        return newT;
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
//            System.out.println("relationship: ------- " + relationship.getName());
            List<RelationshipEdge> edgeList = relationship.getEdgeList();
            List<Attribute> attributeList = relationship.getAttributeList();
            Map<Cardinality, List<Long>> cardinalityListMap = analyzeCardinality(edgeList, mapRelationshipToTable);
            Table representTable;

            if (edgeList.size() == 1) {
                representTable = parseRelationshipWithSingleEntity(cardinalityListMap, tableDTOMap);
            } else {
                if (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 1) {
                    representTable = parseOneToMany(cardinalityListMap, tableDTOMap);
//                    representTable.setId(relationship.getID());
                } else {
                    if (edgeList.size() == 2 &&
                            (cardinalityListMap.get(Cardinality.OneToOne).size() + cardinalityListMap.get(Cardinality.ZeroToOne).size() == 2)) {
                        representTable = parseTwoOneToOne(cardinalityListMap, tableDTOMap);
//                        representTable.setId(relationship.getID());
                    } else {
                        List<Long> tableIdList = cardinalityListMap.get(Cardinality.OneToOne);
                        tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToOne));
                        tableIdList.addAll(cardinalityListMap.get(Cardinality.ZeroToMany));
                        tableIdList.addAll(cardinalityListMap.get(Cardinality.OneToMany));
                        representTable = parseCardinalityWithNewTable(tableIdList, tableDTOMap, relationship.getName(), relationship);
                    }
                }
            }
//            representTable.setId(relationship.getID()); /// todo: yuan lai zui kui huo shou zai zhe li md!
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

        if (relationshipQueue.size() != relationshipList.size()) {
            throw new ERException("Please remove the relationship cycles in the schema");
        }

        return relationshipQueue;
    }


    private static void parseWeakEntity(RelationshipEdge keyEdge, List<RelationshipEdge> edges, Map<Long, Table> tableMap) {
        Table weakEntity = tableMap.get(keyEdge.getConnObj().getID());
        for (RelationshipEdge edge : edges) {
            Table strongEntity = tableMap.get(edge.getConnObj().getID());
            List<Column> fkList = new ArrayList<>();
            for (Column strongPk : strongEntity.getPrimaryKey()) {
                Column clonePk = strongPk.getForeignClone(weakEntity.getId(), true, strongEntity.getName(), strongPk.isNullable());
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
            importedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.OneToOne).get(0)); // todo:
            exportedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.ZeroToOne).get(1));
            canbeNull = true;
        } else {
            importedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.OneToOne).get(0));
            exportedTable = tableDTOMap.get(cardinalityListMap.get(Cardinality.ZeroToOne).get(0));
        }

        List<Column> fkList = new ArrayList<>();
        for (Column pk : exportedTable.getPrimaryKey()) {
            Column fk = pk.getForeignClone(importedTable.getId(), true, exportedTable.getName(), pk.isNullable()); // todo: change isPk to true
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
                Column cloneFk = pk.getForeignClone(mainTableId, true, foreignTable.getName(), pk.isNullable()); // todo: change isOk to true
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


    private static Table parseCardinalityWithNewTable(List<Long> tableIdList, Map<Long, Table> tableDTOMap, String reName, Relationship relationship) {

        StringBuilder tableName = new StringBuilder(reName.replace(' ', '_'));
        List<Column> columnList = new ArrayList<>();
        List<Column> pkList = new ArrayList<>();
        Map<Long, List<Column>> foreignKey = new HashMap<>();

//        Table newTable = new Table(RandomUtils.generateID(), tableName.toString(), EntityType.STRONG, null,
//                columnList, pkList, new ArrayList<>(), foreignKey, null); ///
        Table newTable = new Table(relationship.getID(), tableName.toString(), EntityType.STRONG, null,
            columnList, pkList, new ArrayList<>(), foreignKey, null, false, null);

        for (Long tableId : tableIdList) {
            Table foreignTable = tableDTOMap.get(tableId);
//            tableName.append("_").append(foreignTable.getName()); ///

            List<Column> fkColumns = new ArrayList<>();
            for (Column pk : foreignTable.getPrimaryKey()) {
                Column cloneFk = pk.getForeignClone(newTable.getId(), true, foreignTable.getName(), pk.isNullable());
                cloneFk.setNullable(false);
                cloneFk.setID(pk.getID()); ///
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
                Table newT = parseMultiValuedColumn(tableDTOMap, table, column);
                tableDTOMap.put(newT.getId(), newT);
            }
        }
    }
}
