package com.ic.er;

import com.ic.er.bean.dto.transform.ColumnDTO;
import com.ic.er.bean.dto.transform.TableDTO;
import com.ic.er.common.Cardinality;
import com.ic.er.util.RandomUtils;
import com.ic.er.exception.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserUtil {

    public static Schema parseAttributeToRelationship(List<TableDTO> tableDTOList) throws ParseException {
        List<Entity> entityList = new ArrayList<>();
        List<Relationship> relationshipList = new ArrayList<>();
        List<ColumnDTO> foreignKeyList = new ArrayList<>();
        List<TableDTO> tableGenerateByManyToMany = new ArrayList<>();
        Map<Long, Entity> tableDTOEntityMap = new HashMap<>();
        for (TableDTO table : tableDTOList) {
            List<Attribute> attributeList = new ArrayList<>();
            List<ColumnDTO> columnDTOList = table.getColumnDTOList();

            for (ColumnDTO column : columnDTOList) {
                if (column.getIsForeign() == 1) {
                    foreignKeyList.add(column);
                } else {
                    Attribute attribute = new Attribute((long)1, (long)1, (long)1, column.getName(), null,
                            column.isPrimary(), column.isNullable(), null, null, null, null, null);
                    attributeList.add(attribute);
                }
            }

            if (attributeList.isEmpty()) {
                tableGenerateByManyToMany.add(table);
            }

            Entity entity = new Entity((long)1, table.getName(), (long)1, attributeList, null, null, null, null, null);
            tableDTOEntityMap.put(table.getId(), entity);

            entityList.add(entity);
        }

        // parser foreign key, (1-N)
        for (ColumnDTO foreignKey : foreignKeyList) {
            Entity firstEntity = tableDTOEntityMap.get(foreignKey.getForeignKeyTable());
            Entity secondEntity = tableDTOEntityMap.get(foreignKey.getBelongTo());
            Relationship relationship = new Relationship((long)1, "undefined", (long)1, firstEntity, secondEntity,
                    Cardinality.OneToOne, Cardinality.OneToMany, null, null,null);
            relationshipList.add(relationship);
        }

        // parser table generate by relationship
        for (TableDTO tableDTO : tableGenerateByManyToMany) {
            if (tableDTO.getColumnDTOList().size() != 2) {
                throw new ParseException("Cannot parse relationship over 2 entities.");
            }

            ColumnDTO firstRelation = tableDTO.getColumnDTOList().get(0);
            ColumnDTO secondRelation = tableDTO.getColumnDTOList().get(1);
            Entity firstEntity = tableDTOEntityMap.get(firstRelation.getForeignKeyTable());
            Entity secondEntity = tableDTOEntityMap.get(secondRelation.getForeignKeyTable());
            Relationship relationship = new Relationship((long)1, "undefined", (long)1, firstEntity, secondEntity,
                    Cardinality.OneToMany, Cardinality.OneToMany, null, null, null);
            relationshipList.add(relationship);
        }

        Schema schema = new Schema((long)1, "unknown", entityList, relationshipList, "unknown", null, null);
        return schema;
    }


    public static Map<Long, TableDTO> parseRelationshipsToAttribute(List<Entity> entityList, List<Relationship> relationshipList) throws ParseException {
        Map<Long, TableDTO> tableDTOMap = new HashMap<>();
        for (Entity entity : entityList) {
            TableDTO tableDTO = new TableDTO();
            tableDTO.tranformEntity(entity);
            tableDTOMap.put(tableDTO.getId(), tableDTO);
        }

        // parse relation to new table or new attribute in existing table
        for (Relationship relationship : relationshipList) {
            TableDTO firstTable = tableDTOMap.get(relationship.getFirstEntity().getID());
            TableDTO secondTable = tableDTOMap.get(relationship.getSecondEntity().getID());

            if (firstTable == null || secondTable == null) {
                throw new ParseException("The tables relationship connected does not exist");
            }

            if (firstTable.getPrimaryKey().size() == 0) {
                throw new ParseException("Relationship fail to connect to the primary key of table : " + firstTable.getName());
            }
            if (secondTable.getPrimaryKey().size() == 0) {
                throw new ParseException("Relationship fail to connect to the primary key of table : " + secondTable.getName());
            }

            if (((relationship.getFirstCardinality() == Cardinality.OneToOne || relationship.getFirstCardinality() == Cardinality.ZeroToOne)
                    && (relationship.getSecondCardinality() == Cardinality.OneToOne || relationship.getSecondCardinality() == Cardinality.ZeroToOne))
                    || ((relationship.getFirstCardinality() == Cardinality.OneToMany || relationship.getFirstCardinality() == Cardinality.ZeroToMany)
                    && (relationship.getSecondCardinality() == Cardinality.OneToMany || relationship.getSecondCardinality() == Cardinality.ZeroToMany))) {
                // one-one, many-many, create a new table
                List<ColumnDTO> columnDTOList = new ArrayList<>();
                String tableName = relationship.getName().replace(' ', '_') + "_" + firstTable.getName() + "_" + secondTable.getName();
                List<ColumnDTO> primaryKey = new ArrayList<>();
                TableDTO tableDTO = new TableDTO(RandomUtils.generateID(), tableName, columnDTOList, primaryKey);
                String firstTablePkName = firstTable.getName() + "_" + firstTable.getPrimaryKey().get(0).getName();
                ColumnDTO firstTableId = new ColumnDTO(firstTablePkName, firstTable.getPrimaryKey().get(0).getDataType()
                        , false, 1, firstTable.getId(), tableDTO.getId(), false);
                String secondTablePkName = secondTable.getName() + "_" + secondTable.getPrimaryKey().get(0).getName();
                ColumnDTO secondTableId = new ColumnDTO(secondTablePkName, secondTable.getPrimaryKey().get(0).getDataType()
                        , false, 1, secondTable.getId(), tableDTO.getId(), false);
                columnDTOList.add(firstTableId);
                columnDTOList.add(secondTableId);
                tableDTOMap.put(tableDTO.getId(), tableDTO);
            } else if ((relationship.getFirstCardinality() == Cardinality.OneToOne || relationship.getFirstCardinality() == Cardinality.ZeroToOne)
                    && (relationship.getSecondCardinality() == Cardinality.OneToMany || relationship.getSecondCardinality() == Cardinality.ZeroToMany)) {
                ColumnDTO primaryKey = secondTable.getPrimaryKey().get(0);
                ColumnDTO foreignColumn = new ColumnDTO(secondTable.getName()+"_"+primaryKey.getName(), primaryKey.getDataType()
                        ,false,1,secondTable.getId(), firstTable.getId(), false);
                // 0Many-01, 0Many-11 the foreign column can be null
                if (relationship.getSecondCardinality() == Cardinality.ZeroToOne) {
                    foreignColumn.setNullable(true);
                }

                firstTable.getColumnDTOList().add(foreignColumn);

            } else if ((relationship.getFirstCardinality() == Cardinality.OneToMany || relationship.getFirstCardinality() == Cardinality.ZeroToMany)
                    && (relationship.getSecondCardinality() == Cardinality.OneToOne || relationship.getSecondCardinality() == Cardinality.ZeroToOne)) {
                ColumnDTO primaryKey = firstTable.getPrimaryKey().get(0);
                ColumnDTO foreignColumn = new ColumnDTO(firstTable.getName()+"_"+primaryKey.getName(), primaryKey.getDataType()
                        ,false,1,firstTable.getId(), secondTable.getId(), false);
                // 01-0Many, 01-1Many the foreign column can be null
                if (relationship.getFirstCardinality() == Cardinality.ZeroToOne) {
                    foreignColumn.setNullable(true);
                }

                secondTable.getColumnDTOList().add(foreignColumn);
            }
        }

        return tableDTOMap;
    }
}
