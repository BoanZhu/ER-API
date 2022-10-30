package com.ic.er.util;

import com.ic.er.Entity;
import com.ic.er.Relationship;
import com.ic.er.bean.dto.transform.ColumnDTO;
import com.ic.er.bean.dto.transform.TableDTO;
import com.ic.er.common.Cardinality;
import com.ic.er.common.Utils;
import com.ic.er.exception.ParseException;

import java.util.*;

public class GenerationSqlUtil {

    public static String toSqlStatement(List<TableDTO> tableDTOList) {
        StringBuilder sqlStatement = new StringBuilder("");

        for (TableDTO tableDTO : tableDTOList) {
            sqlStatement.append("CREATE TABLE `").append(tableDTO.getName()).append("` (\n");
            List<ColumnDTO> columnDTOList = tableDTO.getColumnDTOList();
            StringBuilder constraintStatement = new StringBuilder("");
            for (ColumnDTO columnDTO : columnDTOList) {
                sqlStatement.append("    `").append(columnDTO.getName()).append("` ")
                        .append(columnDTO.getDataType().toUpperCase()).append(",\n");

                if (columnDTO.isPrimary()) {
                    constraintStatement.append("    CONSTRAINT ").append(tableDTO.getName())
                            .append("_pk").append(" PRIMARY KEY (").append(columnDTO.getName()).append("),\n");
                }

                int fkIndex = 1;
                if (columnDTO.getIsForeign() == 1) {
                    constraintStatement.append("    CONSTRAINT ").append(tableDTO.getName()).append("_fk").append(fkIndex)
                            .append(" FOREIGN KEY (").append(columnDTO.getName()).append(")")
                            .append(" REFERENCES ").append(columnDTO.getForeignKeyTable().getName())
                            .append("(").append(columnDTO.getForeignKeyTable().getPrimaryKey().get(0).getName()).append(")").append(",\n");

                }
            }

            sqlStatement.append(constraintStatement).append(")\n\n");
            sqlStatement.deleteCharAt(sqlStatement.lastIndexOf(","));

        }

        return sqlStatement.toString();
    }

    public static List<TableDTO> parseRelationshipsToAttribute(List<Entity> entityList, List<Relationship> relationshipList) throws ParseException {
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
                String firstTablePkName = firstTable.getName() + "_" + firstTable.getPrimaryKey().get(0).getName();
                ColumnDTO firstTableId = new ColumnDTO(firstTablePkName, firstTable.getPrimaryKey().get(0).getDataType(), false, 1, firstTable);
                String secondTablePkName = secondTable.getName() + "_" + secondTable.getPrimaryKey().get(0).getName();
                ColumnDTO secondTableId = new ColumnDTO(secondTablePkName, secondTable.getPrimaryKey().get(0).getDataType(), false, 1, secondTable);
                columnDTOList.add(firstTableId);
                columnDTOList.add(secondTableId);
                String tableName = relationship.getName().replace(' ', '_') + "_" + firstTable.getName() + "_" + secondTable.getName();
                List<ColumnDTO> primaryKey = new ArrayList<>();
                TableDTO tableDTO = new TableDTO(Utils.generateID(), tableName, columnDTOList, primaryKey);
                tableDTOMap.put(tableDTO.getId(), tableDTO);
            } else if ((relationship.getFirstCardinality() == Cardinality.OneToOne || relationship.getFirstCardinality() == Cardinality.ZeroToOne)
                    && (relationship.getSecondCardinality() == Cardinality.OneToMany || relationship.getSecondCardinality() == Cardinality.ZeroToMany)) {

                // one-one, many-many, create a new table
                ColumnDTO primaryKey = firstTable.getPrimaryKey().get(0);
                ColumnDTO foreignColumn = new ColumnDTO(firstTable.getName()+"_"+primaryKey.getName(), primaryKey.getDataType(),false,1,firstTable);
                secondTable.getColumnDTOList().add(foreignColumn);
            } else if ((relationship.getFirstCardinality() == Cardinality.OneToMany || relationship.getFirstCardinality() == Cardinality.ZeroToMany)
                    && (relationship.getSecondCardinality() == Cardinality.OneToOne || relationship.getSecondCardinality() == Cardinality.ZeroToOne)) {
                ColumnDTO primaryKey = secondTable.getPrimaryKey().get(0);
                ColumnDTO foreignColumn = new ColumnDTO(secondTable.getName()+"_"+primaryKey.getName(), primaryKey.getDataType(),false,1,secondTable);
                firstTable.getColumnDTOList().add(foreignColumn);
            }
        }

        ArrayList<TableDTO> tableDTOS = new ArrayList<>(tableDTOMap.values());
        return tableDTOS;
    }
}
