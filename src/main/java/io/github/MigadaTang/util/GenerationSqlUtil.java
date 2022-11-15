package io.github.MigadaTang.util;

import io.github.MigadaTang.bean.dto.transform.ColumnDTO;
import io.github.MigadaTang.bean.dto.transform.TableDTO;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenerationSqlUtil {

    public static String toSqlStatement(Map<Long, TableDTO> tableDTOList) {
        StringBuilder sqlStatement = new StringBuilder("");

        for (TableDTO tableDTO : tableDTOList.values()) {
            sqlStatement.append("CREATE TABLE `").append(tableDTO.getName()).append("` (\n");
            List<ColumnDTO> columnDTOList = tableDTO.getColumnDTOList();
            StringBuilder constraintStatement = new StringBuilder("");
            Set<String> columnNames = new HashSet<>();
            for (ColumnDTO columnDTO : columnDTOList) {
                if (columnNames.contains(columnDTO.getName()))
                    columnDTO.setName(columnDTO.getName() + "1");
                else
                    columnNames.add(columnDTO.getName());

                sqlStatement.append("    `").append(columnDTO.getName()).append("` ")
                        .append(columnDTO.getDataType().toUpperCase())
                        .append(" ").append(columnDTO.nullable()).append(",\n");
            }

            if (tableDTO.getPrimaryKey().size() > 0) {
                constraintStatement.append("    CONSTRAINT ").append(tableDTO.getName()).append("_pk").append(" PRIMARY KEY (");
                for (ColumnDTO columnDTO : tableDTO.getPrimaryKey()) {
                    constraintStatement.append(columnDTO.getName()).append(",");
                }
                constraintStatement.deleteCharAt(constraintStatement.lastIndexOf(","));
                constraintStatement.append("),\n");
            }

            if (tableDTO.getForeignKey().size() > 0) {
                int fkIndex = 1;
                for (Long referTableId : tableDTO.getForeignKey().keySet()) {
                    List<ColumnDTO> foreignKey = tableDTO.getForeignKey().get(referTableId);
                    if (foreignKey.size() == 0) {
                        continue;
                    }
                    StringBuilder fkName = new StringBuilder();
                    StringBuilder relatedName = new StringBuilder();

                    for (ColumnDTO column : foreignKey) {
                        fkName.append(column.getName()).append(",");
                        relatedName.append(column.getForeignKeyColumnName()).append(",");
                    }
                    fkName.deleteCharAt(fkName.lastIndexOf(","));
                    relatedName.deleteCharAt(relatedName.lastIndexOf(","));

                    constraintStatement.append("    CONSTRAINT ").append(tableDTO.getName()).append("_fk").append(fkIndex)
                            .append(" FOREIGN KEY (").append(fkName).append(")")
                            .append(" REFERENCES ").append(tableDTOList.get(referTableId).getName())
                            .append("(").append(relatedName).append(")").append(",\n");
                    fkIndex++;
                }
            }

            sqlStatement.append(constraintStatement).append(")\n\n");
            sqlStatement.deleteCharAt(sqlStatement.lastIndexOf(","));

        }

        return sqlStatement.toString();
    }

}
