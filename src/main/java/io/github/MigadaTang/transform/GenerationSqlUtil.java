package io.github.MigadaTang.transform;

import io.github.MigadaTang.exception.ParseException;

import java.time.chrono.ThaiBuddhistChronology;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GenerationSqlUtil {

    public static String toSqlStatement(Map<Long, Table> tableDTOList) throws ParseException {
        StringBuilder sqlStatement = new StringBuilder("");

        Map<Long, List<Long>> tableMap = new HashMap<>();
        List<Table> tablesSqlGenerationOrders = new ArrayList<Table>();

        // One improvement here we need to create the sql statements for foreign key tables first,
        // otherwise the sql statements may not be executed successfully.
        // E.g. In the case of one-many relationships, the relationship table will be the first table
        for (Table table : tableDTOList.values()) {
            if (table.getForeignKey().size() == 0) {
                tableMap.put(table.getId(), new ArrayList<Long>());
            } else {
                for (Long id: table.getForeignKey().keySet()) {
                    List<Long> previousIds = tableMap.getOrDefault(table.getId(), new ArrayList<Long>());
                    previousIds.add(id);
                    tableMap.put(table.getId(), previousIds);
                }
            }
        }

        tableGenerationOrders(tablesSqlGenerationOrders, tableMap, tableDTOList);

        boolean whetherContainsSubset = checkContainsSubset(tablesSqlGenerationOrders);

//        for (Table table : tableDTOList.values()) {
        for (Table table : tablesSqlGenerationOrders) {
            sqlStatement.append("CREATE TABLE ").append(table.getName()).append(" (\n");
            List<Column> columnList = table.getColumnList();
            StringBuilder constraintStatement = new StringBuilder("");
            Set<String> columnNames = new HashSet<>();

            if (columnList.size() == 0) {
                throw new ParseException("Table `" + table.getName() + "` should contain at least one column");
            }

            for (Column column : columnList) {
                if (columnNames.contains(column.getName()))
                    column.setName(column.getName() + "1");
                else
                    columnNames.add(column.getName());

                // One improvement here, we need to transform all the spaces into underlines, or the sql statements may not
                // bt executed successfully.
                String nameTransformed = column.getName().replaceAll(" ", "_");

                sqlStatement.append("    ").append(column.getName()).append(" ")
                    .append(column.getDataType().toUpperCase())
                    .append(" ").append(column.nullable()).append(",\n");
            }

            if (table.getPrimaryKey().size() > 0) {
                constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_pk").append(" PRIMARY KEY (");
                for (Column column : table.getPrimaryKey()) {
                    constraintStatement.append(column.getName()).append(",");
                }
                constraintStatement.deleteCharAt(constraintStatement.lastIndexOf(","));
                constraintStatement.append("),\n");
            }

            if (table.getForeignKey().size() > 0) {
                int fkIndex = 1;
                for (Long referTableId : table.getForeignKey().keySet()) {
                    List<Column> foreignKey = table.getForeignKey().get(referTableId);
                    if (foreignKey.size() == 0) {
                        continue;
                    }
                    StringBuilder fkName = new StringBuilder();
                    StringBuilder relatedName = new StringBuilder();

                    for (Column column : foreignKey) {
                        fkName.append(column.getName()).append(",");
                        relatedName.append(column.getForeignKeyColumnName()).append(",");
                    }
                    fkName.deleteCharAt(fkName.lastIndexOf(","));
                    relatedName.deleteCharAt(relatedName.lastIndexOf(","));

                    constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_fk").append(fkIndex)
                        .append(" FOREIGN KEY (").append(fkName).append(")")
                        .append(" REFERENCES ").append(tableDTOList.get(referTableId).getName())
                        .append("(").append(relatedName).append(")").append(",\n");
                    fkIndex++;
                }
            }

            sqlStatement.append(constraintStatement).append(");\n\n");
            sqlStatement.deleteCharAt(sqlStatement.lastIndexOf(","));

        }

        if (whetherContainsSubset) {

            sqlStatement.append("\n");
            sqlStatement.append("|");

            List<Table> subsetTablesFirstCase = generateSubsetTablesFirstCase(tablesSqlGenerationOrders);

            for (Table table : subsetTablesFirstCase) {
                sqlStatement.append("CREATE TABLE ").append(table.getName()).append(" (\n");
                List<Column> columnList = table.getColumnList();
                StringBuilder constraintStatement = new StringBuilder("");
                Set<String> columnNames = new HashSet<>();

                if (columnList.size() == 0) {
                    throw new ParseException("Table `" + table.getName() + "` should contain at least one column");
                }

                for (Column column : columnList) {
                    if (columnNames.contains(column.getName()))
                        column.setName(column.getName() + "1");
                    else
                        columnNames.add(column.getName());

                    // One improvement here, we need to transform all the spaces into underlines, or the sql statements may not
                    // bt executed successfully.
                    String nameTransformed = column.getName().replaceAll(" ", "_");

                    sqlStatement.append("    ").append(column.getName()).append(" ")
                        .append(column.getDataType().toUpperCase())
                        .append(" ").append(column.nullable()).append(",\n");
                }

                if (table.getPrimaryKey().size() > 0) {
                    constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_pk").append(" PRIMARY KEY (");
                    for (Column column : table.getPrimaryKey()) {
                        constraintStatement.append(column.getName()).append(",");
                    }
                    constraintStatement.deleteCharAt(constraintStatement.lastIndexOf(","));
                    constraintStatement.append("),\n");
                }

                if (table.getForeignKey().size() > 0) {
                    int fkIndex = 1;
                    for (Long referTableId : table.getForeignKey().keySet()) {
                        List<Column> foreignKey = table.getForeignKey().get(referTableId);
                        if (foreignKey.size() == 0) {
                            continue;
                        }
                        StringBuilder fkName = new StringBuilder();
                        StringBuilder relatedName = new StringBuilder();

                        for (Column column : foreignKey) {
                            fkName.append(column.getName()).append(",");
                            relatedName.append(column.getForeignKeyColumnName()).append(",");
                        }
                        fkName.deleteCharAt(fkName.lastIndexOf(","));
                        relatedName.deleteCharAt(relatedName.lastIndexOf(","));

                        constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_fk").append(fkIndex)
                            .append(" FOREIGN KEY (").append(fkName).append(")")
                            .append(" REFERENCES ").append(tableDTOList.get(referTableId).getName())
                            .append("(").append(relatedName).append(")").append(",\n");
                        fkIndex++;
                    }
                }

                sqlStatement.append(constraintStatement).append(");\n\n");
                sqlStatement.deleteCharAt(sqlStatement.lastIndexOf(","));

            }

            sqlStatement.append("\n");
            sqlStatement.append("|");

            List<Table> subsetTablesSecondCase = generateSubsetTablesSecondCase(tablesSqlGenerationOrders);

            for (Table table : subsetTablesSecondCase) {
                sqlStatement.append("CREATE TABLE ").append(table.getName()).append(" (\n");
                List<Column> columnList = table.getColumnList();
                StringBuilder constraintStatement = new StringBuilder("");
                Set<String> columnNames = new HashSet<>();

                if (columnList.size() == 0) {
                    throw new ParseException("Table `" + table.getName() + "` should contain at least one column");
                }

                for (Column column : columnList) {
                    if (columnNames.contains(column.getName()))
                        column.setName(column.getName() + "1");
                    else
                        columnNames.add(column.getName());

                    // One improvement here, we need to transform all the spaces into underlines, or the sql statements may not
                    // bt executed successfully.
                    String nameTransformed = column.getName().replaceAll(" ", "_");

                    sqlStatement.append("    ").append(column.getName()).append(" ")
                        .append(column.getDataType().toUpperCase())
                        .append(" ").append(column.nullable()).append(",\n");
                }

                if (table.getPrimaryKey().size() > 0) {
                    constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_pk").append(" PRIMARY KEY (");
                    for (Column column : table.getPrimaryKey()) {
                        constraintStatement.append(column.getName()).append(",");
                    }
                    constraintStatement.deleteCharAt(constraintStatement.lastIndexOf(","));
                    constraintStatement.append("),\n");
                }

                if (table.getForeignKey().size() > 0) {
                    int fkIndex = 1;
                    for (Long referTableId : table.getForeignKey().keySet()) {
                        List<Column> foreignKey = table.getForeignKey().get(referTableId);
                        if (foreignKey.size() == 0) {
                            continue;
                        }
                        StringBuilder fkName = new StringBuilder();
                        StringBuilder relatedName = new StringBuilder();

                        for (Column column : foreignKey) {
                            fkName.append(column.getName()).append(",");
                            relatedName.append(column.getForeignKeyColumnName()).append(",");
                        }
                        fkName.deleteCharAt(fkName.lastIndexOf(","));
                        relatedName.deleteCharAt(relatedName.lastIndexOf(","));

                        constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_fk").append(fkIndex)
                            .append(" FOREIGN KEY (").append(fkName).append(")")
                            .append(" REFERENCES ").append(tableDTOList.get(referTableId).getName())
                            .append("(").append(relatedName).append(")").append(",\n");
                        fkIndex++;
                    }
                }

                sqlStatement.append(constraintStatement).append(");\n\n");
                sqlStatement.deleteCharAt(sqlStatement.lastIndexOf(","));

            }

        }
        return sqlStatement.toString();
    }

    // We need to create the sql statements for foreign key tables first, otherwise the sql
    // statements may not be executed successfully.
    // E.g. In the case of one-many relationships, the relationship table will be the first table
    // This is wrong since we need the two entities created before it.
    public static void tableGenerationOrders(List<Table> tablesSqlGenerationOrders, Map<Long, List<Long>> tableMap, Map<Long, Table> tableDTOList) {
        for (Long id: tableMap.keySet()) {
            List<Long> relyIds = tableMap.get(id);
            Table table = tableDTOList.get(id);
            if (!tablesSqlGenerationOrders.contains(table)) {
                if (relyIds.size() == 0) {
                    tablesSqlGenerationOrders.add(tableDTOList.get(id));
                } else {
                    recursive(table, relyIds, tableDTOList, tablesSqlGenerationOrders, tableMap);
                }
            }
        }
    }

    // Helper function for "tableGenerationOrders".
    public static void recursive(Table table, List<Long> relyIds, Map<Long, Table> tableDTOList, List<Table> tablesSqlGenerationOrders, Map<Long, List<Long>> tableMap) {
        for (Long relyId: relyIds) {
            Table relyTable = tableDTOList.get(relyId);
            if (!tablesSqlGenerationOrders.contains(relyTable)) {
                recursive(relyTable, tableMap.get(relyId), tableDTOList, tablesSqlGenerationOrders, tableMap);
            }
        }
        tablesSqlGenerationOrders.add(table);
    }

    // Helper function for generating two kinds of subset ddl.
    public static boolean checkContainsSubset(List<Table> tables) {
        for (Table table: tables) {
            if (table.getBelongStrongTableID() != null) {
                return true;
            }
        }
        return false;
    }

    // There are two ways of generating subset ddl statements, the first way is to push up the subset.
    public static List<Table> generateSubsetTablesFirstCase(List<Table> tables) {
        List<Table> result = new ArrayList<>();
        for (Table currTable: tables) {
            if (currTable.getBelongStrongTableID() != null) { // in this case this is a subset entity
                Table newTable = new Table();
                Table tableRemoved = new Table();
                for (Table addedTable: result) {
                    if (addedTable.getId().equals(currTable.getBelongStrongTableID())) { // this is the strong entity

                        tableRemoved = addedTable;
                        List<Column> columnList = new ArrayList<>();
                        for (Column column: addedTable.getColumnList()) {
                            columnList.add(column);
                        }

                        newTable.setName(addedTable.getName());
                        newTable.setColumnList(columnList);
                        newTable.setTableType(addedTable.getTableType());
                        newTable.setForeignKey(addedTable.getForeignKey());
                        newTable.setPrimaryKey(addedTable.getPrimaryKey());
                        newTable.setMultiValuedColumn(addedTable.getMultiValuedColumn());

                        List<Column> columns = newTable.getColumnList();

                        Column specialColumn = new Column();
                        specialColumn.setName("is_" + currTable.getName());
                        specialColumn.setNullable(false);
                        specialColumn.setDataType("TEXT");
                        columns.add(specialColumn);

                        for (Column column: currTable.getColumnList()) {
                            if (column.getForeignKeyColumn() == null) {
                                column.setNullable(true);
                                columns.add(column);
                            }
                        }
                        newTable.setColumnList(columns);
                    }
                }
                result.remove(tableRemoved);
                result.add(newTable);
            } else {
                result.add(currTable);
            }
        }
        return result;
    }

    // There are two ways of generating subset ddl statements, the second way is to push down the strong entity.
    public static List<Table> generateSubsetTablesSecondCase(List<Table> tables) {
        List<Table> result = new ArrayList<>();
        for (Table currTable: tables) {
            if (currTable.getBelongStrongTableID() != null) { // in this case this is the subset
                for (Table addedTable: result) {
                    if (addedTable.getId().equals(currTable.getBelongStrongTableID())) { // in this case this is the strong entity
                        List<Column> columns = currTable.getColumnList();
                        for (Column column: addedTable.getColumnList()) {
                            if (!column.isPrimary()) {
                                Column newColumn = new Column();
                                newColumn.setForeign(true);
//                                newColumn.setNullable();
                                newColumn.setName(column.getName());
                                newColumn.setForeignKeyColumnName(column.getName());
                                newColumn.setDataType(column.getDataType());
                                newColumn.setBelongTo(currTable.getId());
                                newColumn.setForeignKeyColumn(column.getID());
                                newColumn.setForeignKeyTable(addedTable.getId());
                                newColumn.setPrimary(false);
//                                newColumn.setID();
                                columns.add(newColumn);
                                List<Column> newForeignKeyColumns = currTable.getForeignKey().get(addedTable.getId());
                                newForeignKeyColumns.add(newColumn);
                                currTable.getForeignKey().put(addedTable.getId(), newForeignKeyColumns);
                            }
                        }
                        currTable.setColumnList(columns);
                    }
                }
            }
            result.add(currTable);
        }
        return result;
    }

}
