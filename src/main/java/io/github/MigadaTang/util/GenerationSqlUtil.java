package io.github.MigadaTang.util;

import io.github.MigadaTang.bean.dto.transform.ColumnDTO;
import io.github.MigadaTang.bean.dto.transform.TableDTO;

import java.util.List;
import java.util.Map;

public class GenerationSqlUtil {

    public static String toSqlStatement(Map<Long, TableDTO> tableDTOList) {
        StringBuilder sqlStatement = new StringBuilder("");

        for (TableDTO tableDTO : tableDTOList.values()) {
            sqlStatement.append("CREATE TABLE `").append(tableDTO.getName()).append("` (\n");
            List<ColumnDTO> columnDTOList = tableDTO.getColumnDTOList();
            StringBuilder constraintStatement = new StringBuilder("");
            int fkIndex = 1;
            for (ColumnDTO columnDTO : columnDTOList) {
                sqlStatement.append("    `").append(columnDTO.getName()).append("` ")
                        .append(columnDTO.getDataType().toUpperCase())
                        .append(" ").append(columnDTO.nullable()).append(",\n");

                if (columnDTO.isPrimary()) {
                    constraintStatement.append("    CONSTRAINT ").append(tableDTO.getName())
                            .append("_pk").append(" PRIMARY KEY (").append(columnDTO.getName()).append("),\n");
                }

                if (columnDTO.getIsForeign() == 1) {
                    constraintStatement.append("    CONSTRAINT ").append(tableDTO.getName()).append("_fk").append(fkIndex)
                            .append(" FOREIGN KEY (").append(columnDTO.getName()).append(")")
                            .append(" REFERENCES ").append(tableDTOList.get(columnDTO.getForeignKeyTable()).getName())
                            .append("(").append(tableDTOList.get(columnDTO.getForeignKeyTable()).getPrimaryKey().get(0).getName()).append(")").append(",\n");
                    fkIndex++;
                }
            }

            sqlStatement.append(constraintStatement).append(")\n\n");
            sqlStatement.deleteCharAt(sqlStatement.lastIndexOf(","));

        }

        return sqlStatement.toString();
    }

}
