package io.github.MigadaTang.transform;

import io.github.MigadaTang.common.EntityType;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.chrono.ThaiBuddhistChronology;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import org.apache.commons.lang3.tuple.Pair;
//import org.apache.commons.lang3.tuple.Pair;

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
                    if (!id.equals(table.getId())) {
                        List<Long> previousIds = tableMap.getOrDefault(table.getId(), new ArrayList<Long>());
                        previousIds.add(id);
                        tableMap.put(table.getId(), previousIds);
                    }
                }
            }
        }

        tableGenerationOrders(tablesSqlGenerationOrders, tableMap, tableDTOList);

//        boolean whetherContainsSubset = checkContainsSubset(tablesSqlGenerationOrders);
        boolean whetherContainsSubset = false;

        generateNewTablesSqlStatement(tableDTOList, tablesSqlGenerationOrders, sqlStatement);

        if (whetherContainsSubset) {

            sqlStatement.append("\n");
            sqlStatement.append("|");

            List<Table> subsetTablesFirstCase = generateSubsetTablesFirstCase(tablesSqlGenerationOrders);

            generateNewTablesSqlStatement(tableDTOList, subsetTablesFirstCase, sqlStatement);

            sqlStatement.append("\n");
            sqlStatement.append("|");

            List<Table> subsetTablesSecondCase = generateSubsetTablesSecondCase(tablesSqlGenerationOrders);

            generateNewTablesSqlStatement(tableDTOList, subsetTablesSecondCase, sqlStatement);

        }
        return sqlStatement.toString();
    }

    // We need to create the sql statements for foreign key tables first, otherwise the sql
    // statements may not be executed successfully.
    // E.g. In the case of one-many relationships, the relationship table will be the first table
    // This will be wrong since we need the two entities created before it.
    public static void tableGenerationOrders(List<Table> tablesSqlGenerationOrders, Map<Long, List<Long>> tableMap, Map<Long, Table> tableDTOList) {
        for (Long id: tableMap.keySet()) {
            List<Long> relyIds = tableMap.get(id);
            Table table = tableDTOList.get(id);
            if (!tablesSqlGenerationOrders.contains(table)) {
                if (relyIds.size() == 0) {
                    tablesSqlGenerationOrders.add(table);
                } else {
                    recursive(table, relyIds, tableDTOList, tablesSqlGenerationOrders, tableMap);
                }
            }
        }
    }

    // Helper function for "tableGenerationOrders".
    public static void recursive(Table table, List<Long> relyIds, Map<Long, Table> tableDTOList, List<Table> tablesSqlGenerationOrders, Map<Long, List<Long>> tableMap) {
        // If 'relyIds' == null, this means that the table does not needed to create and already exist.
        // So just pass the function.
        if (relyIds.size() == 0) {
            tablesSqlGenerationOrders.add(table);
            return;
        }
        for (Long relyId: relyIds) {
            Table relyTable = tableDTOList.get(relyId);
            if (!tablesSqlGenerationOrders.contains(relyTable)) {
                List<Long> ids = tableMap.get(relyTable.getId());
                recursive(relyTable, tableMap.get(relyTable.getId()), tableDTOList, tablesSqlGenerationOrders, tableMap);
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
                        specialColumn.setDataType("TEXT"); ///
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
            if (currTable.getBelongStrongTableID() != null) { // in this case this is the subset ///?
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

    public static void generateNewTablesSqlStatement(Map<Long, Table> tableDTOList, List<Table> tablesSqlGenerationOrders, StringBuilder sqlStatement)
        throws ParseException {
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

//                    constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_fk").append(fkIndex)
                    constraintStatement.append("    CONSTRAINT ").append(table.getName()).append("_fk_").append(tableDTOList.get(referTableId).getName())
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

    public static void generateDeletedTablesSqlStatement(List<Table> oldTables, List<Table> deletedTables
        , StringBuilder sqlStatement, List<Table> newTables, List<Table> tableInSchema, List<List<Table>> modifiedTablePair) {
        for (Table deletedTable: deletedTables) {
            for (Table oldTable: oldTables) {
                for (Long foreignTableId: oldTable.getForeignKey().keySet()) {
                    if (foreignTableId.equals(deletedTable.getId()) && !deletedTables.contains(oldTable)) {
                        sqlStatement.append("DROP TABLE ").append(oldTable.getName()).append(";\n\n");
                        for (Table schemaTable: tableInSchema) {
                            if (schemaTable.getId().equals(oldTable.getId())) {
                                newTables.add(schemaTable);
                            }
                        }
                        for (List<Table> tablePair: modifiedTablePair) {
                            if (tablePair.get(0).getId().equals(oldTable.getId()) || tablePair.get(1).equals(oldTable.getId())) {
                                modifiedTablePair.remove(tablePair);
                                break;
                            }
                        }
                    }
                }
            }
            sqlStatement.append("DROP TABLE ").append(deletedTable.getName()).append(";\n\n");
        }
    }


    // There is no need to connect the database and fetch the information, because it should already
    // have reverse or have the original schema.
//    public static String toSqlStatementEnhancement(Map<Long, Table> tableDTOList, RDBMSType databaseType, String dbUrl,
//        String userName, String password) throws ParseException, DBConnectionException {
//
//        Connection conn = null;
//        String driver = "";
//        String sqlStatements;
//
//        try {
//            driver = DatabaseUtil.recognDriver(databaseType);
//            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
//            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//
//            // Assume the table list is correct here.
//            List<Table> tablesInDatabase = DatabaseUtil.getDatabseInfo(conn);
//            DatabaseUtil.closeDBConnection(conn);
//
//            sqlStatements = generateSqlStatements(tableDTOList, tablesInDatabase);
//
//        } catch (ParseException parseException) {
//            throw new ParseException(parseException.getMessage());
//        } catch (DBConnectionException dbConnectionException) {
//            throw new DBConnectionException(dbConnectionException.getMessage());
//        } catch (SQLException throwables) {
//            throw new DBConnectionException("Fail to create db statement");
//        }
//        return sqlStatements;
//    }

    public static String generateSqlStatements(Map<Long, Table> tableDTOList, List<Table> oldTables)
        throws ParseException {
        List<Table> newTables = new ArrayList<>();
//        List<List<Table>> oldTables = new ArrayList<>();
        List<List<Table>> modifiedTablePair = new ArrayList<>();
        List<Table> deleteTables = new ArrayList<>();

        // For all new tables, we only need to create these tables in the database with the information
        // provided in the schema.
        Map<Long, List<Long>> tableMap = new HashMap<>();
        List<Table> tablesSqlGenerationOrders = new ArrayList<Table>();

//        System.out.println("size of tableDTOList: " + tableDTOList.values().size());
//
//        System.out.println("size of oldTables: " + oldTables.size());

//        for (Table table : tableDTOList.values()) {
        for (Long idInDTO : tableDTOList.keySet()) {
            Table table = tableDTOList.get(idInDTO);
            if (table.getForeignKey().size() == 0) {
                List<Long> list = new ArrayList<>();
                tableMap.put(idInDTO, list);
            } else {
                for (Long id: table.getForeignKey().keySet()) {
                    List<Long> previousIds = tableMap.getOrDefault(table.getId(), new ArrayList<Long>());
                    previousIds.add(id);
                    tableMap.put(idInDTO, previousIds);
                }
            }
        }

        // reorder all the tables
        tableGenerationOrders(tablesSqlGenerationOrders, tableMap, tableDTOList);

        for (Table tableInSchema: tablesSqlGenerationOrders) {
            if (tableInSchema.getName().contains("other_name")) continue;
            boolean ifAlreadyExist = false;
            for (Table tableInDatabase: oldTables) {
                if (tableInSchema.getId().equals(tableInDatabase.getId())) {
                    boolean modified = checkTableWhetherModified(tableInSchema, tableInDatabase);
                    ifAlreadyExist = true;
                    if (modified) {
                        List<Table> newPair = new ArrayList<Table>();
                        newPair.add(tableInSchema);
                        newPair.add(tableInDatabase);
                        modifiedTablePair.add(newPair);
                    }
                    break;
                }
            }
            if (!ifAlreadyExist) {
                newTables.add(tableInSchema);
            }
        }

        for (Table tableInDatabase: oldTables) {
            if (tableInDatabase.getName().contains("other_name")) continue;
            boolean inSchema = false;
            for (Table tableInSchema: tablesSqlGenerationOrders) {
                if (tableInSchema.getId().equals(tableInDatabase.getId())) {
                    inSchema = true;
                    break;
                }
            }
            if (!inSchema) {
                deleteTables.add(tableInDatabase);
            }
        }

        StringBuilder sqlStatement = new StringBuilder("");

        List<Table> tableInSchema = new ArrayList<>();
        tableInSchema.addAll(tablesSqlGenerationOrders);

        // For all tables that were deleted, we need to drop these tables in the database.
        generateDeletedTablesSqlStatement(oldTables, deleteTables, sqlStatement, newTables, tableInSchema, modifiedTablePair);

        generateNewTablesSqlStatement(tableDTOList, newTables, sqlStatement);

//        System.out.println("Size of newTables: " + newTables.size());
//        System.out.println("Size of deletedTables: " + deleteTables.size());

        Collections.reverse(modifiedTablePair);
        // For the tables already exist in the database/schema and modified, we need to check the difference between
        // the new table and old tables. For the attributes, we will add these new columns in the table.
        // For already exist columns, we need to find the modifications and change them. For some
        // already existing columns in the old table which are not appear in the new table, we can't
        // delete them.
        for (List<Table> tablePair: modifiedTablePair) {

            Table newTable = tablePair.get(0);
            Table oldTable = tablePair.get(1);

            if (!newTable.getName().equals(oldTable.getName())) {
                sqlStatement.append("ALTER TABLE ").append(oldTable.getName()).append("\n");
                sqlStatement.append("RENAME TO ").append(newTable.getName()).append(";\n");
//                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");

                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                sqlStatement.append("RENAME CONSTRAINT ").append(oldTable.getName() + "_pk")
                    .append(" TO " + newTable.getName() + "_pk").append(";\n");
            }

            List<Column> newColumns = newTable.getColumnList();
            List<Column> oldColumns = oldTable.getColumnList();
            List<Column> createdColumns = new ArrayList<>();
            List<Column> deletedColumns = new ArrayList<>();

//            List<Column> diffColumns = new ArrayList<>();
//            List<Column> newColumns = new ArrayList<>();

            for (Column oldColumn: oldColumns) {
                boolean inSchema = false;
                for (Column newColumn: newColumns) {
                    if (newColumn.getID().equals(oldColumn.getID())) {
//                    if (newColumn.getName().equals(oldColumn.getName())) {
                        inSchema = true;
                        break;
                    }
                }
                if (!inSchema) {
                    deletedColumns.add(oldColumn);
                }
            }

            boolean deletePrimaryKey = false;
            // Delete all columns in deletedColumns.
            for (Column column: deletedColumns) {
                if (column.isPrimary()) {
                    sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                    sqlStatement.append("DROP CONSTRAINT ").append(oldTable.getName() + "_pk")
                        .append(";\n");
                    deletePrimaryKey = true;
                }
                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                sqlStatement.append("DROP COLUMN ").append(column.getName()).append(";\n");
            }

            for (Column newColumn: newColumns) {
                boolean alreadyExist = false;
                for (Column oldColumn: oldColumns) {
                    if (newColumn.getID().equals(oldColumn.getID())) {
//                    if (newColumn.getName().equals(oldColumn.getName())) {
                        alreadyExist = true;
                        break;
                    }
                }

                if (!alreadyExist) {
                    createdColumns.add(newColumn);
                }
            }

            for (Column column: createdColumns) {
                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                sqlStatement.append("ADD COLUMN ").append(column.getName()).append(" " + column.getDataType())
                    .append(" " + (column.isNullable() ? "NULL" : "NOT NULL DEFAULT 'N/A'")).append(";\n");
            }

            for (Column newColumn: newColumns) {
//                boolean alreadyExist = false;
                for (Column oldColumn: oldColumns) {
                    if (newColumn.getID().equals(oldColumn.getID())) {
//                    if (newColumn.getName().equals(oldColumn.getName())) {
//                        alreadyExist = true;
                        if (!newColumn.getName().equals(oldColumn.getName()) && !newColumn.isForeign()) {
                            sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                            sqlStatement.append("RENAME COLUMN ").append(oldColumn.getName())
                                .append(" TO ").append(newColumn.getName()).append(";\n");

//                            if () {
//
//                            }
                        }
                        if (!newColumn.getDataType().equals(oldColumn.getDataType())) {
                            sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                            sqlStatement.append("ALTER COLUMN ").append(newColumn.getName())
                                .append(" TYPE " + newColumn.getDataType().toUpperCase() + " USING ")
                                .append(newColumn.getName() + "::" + newColumn.getDataType().toLowerCase())
                                .append(";\n");
                        }
                        if (newColumn.isNullable() != oldColumn.isNullable()) {
                            if (oldColumn.isNullable() && !newColumn.isNullable()) {
                                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                                sqlStatement.append("ALTER COLUMN ").append(newColumn.getName())
                                    .append(" SET NOT NULL").append(";\n");
                            } else {
//                                System.out.println("why: " + newColumn);
//                                System.out.println("whyyyy: " + oldColumn);
                                // todo: if the column is a primary key, it's impossible to become nullable
                                if (!newColumn.isPrimary()) {
                                    sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                                    sqlStatement.append("ALTER COLUMN ").append(newColumn.getName())
                                        .append(" DROP NOT NULL").append(";\n");
                                }

                            }
                        }
                        if (newColumn.isPrimary() != oldColumn.isPrimary()) {
                            if (!newColumn.isPrimary() && oldColumn.isPrimary()) {

                                // There are two steps in this case, the first step is to drop all the
                                // foreign key constraints which reference this primary key;
//                                for (Table table: oldTables) {
//                                    List<Column> columns = table.getColumnList();
//                                    boolean hasForeignKeys = false;
//                                    for (Column column: columns) {
//                                        if (column.getForeignKeyColumn() != null && column.getForeignKeyColumn().equals(newColumn.getID())) {
//                                            hasForeignKeys = true;
//                                            break;
//                                        }
//                                    }

                                    // There is no need to drop the foreign key constraint, since the column
                                    // was already be deleted.

//                                    if (hasForeignKeys) {
//                                        sqlStatement.append("ALTER TABLE ").append(table.getName()).append("\n");
//                                        sqlStatement.append("DROP CONSTRAINT ")
//                                            .append(table.getName() + "_fk_" + oldTable.getName())
//                                            .append(";\n");
//                                    }
//                                }

                                // then the next step is to drop the primary key constraint in this table.
                                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                                sqlStatement.append("DROP CONSTRAINT ").append(newTable.getName() + "_pk")
                                    .append(";\n");

                                // todo:
//                                if (newTable.getPrimaryKey().size() != 1) {
//                                    throw new ParseException("Error! There are more than one primary key!");
//                                }
                                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                                sqlStatement.append("ADD CONSTRAINT ").append(newTable.getName() + "_pk")
                                    .append(" PRIMARY KEY ").append("(")
                                    .append(newTable.getPrimaryKey().get(0).getName())
                                    .append(")").append(";\n");

                                for (Table table: oldTables) { ///
                                    List<Column> columns = table.getColumnList();
                                    boolean hasForeignKeys = false;
                                    for (Column column: columns) {
                                        if (column.getForeignKeyColumn() != null && column.getForeignKeyColumn().equals(newColumn.getID())) {
                                            hasForeignKeys = true;
                                            break;
                                        }
                                    }
                                    if (hasForeignKeys) {
                                        StringBuilder fkName = new StringBuilder();
                                        StringBuilder relatedName = new StringBuilder();

                                        Table tableInDTOList = null;
                                        for (Table tableInDTO: tableDTOList.values()) {
                                            if (tableInDTO.getId().equals(table.getId())) {
                                                tableInDTOList = tableInDTO;
                                                break;
                                            }
                                        }

//                                        System.out.println("tableInDTOList: " + tableInDTOList);
//                                        System.out.println("newTable: " + newTable);
                                        if (tableInDTOList != null && tableInDTOList.getForeignKey().get(newTable.getId()) != null) {
                                            for (Column column : tableInDTOList.getForeignKey().get(newTable.getId())) {
                                                fkName.append(column.getName()).append(",");
                                                relatedName.append(column.getForeignKeyColumnName()).append(",");
                                            }
                                            fkName.deleteCharAt(fkName.lastIndexOf(","));
                                            relatedName.deleteCharAt(relatedName.lastIndexOf(","));

                                            sqlStatement.append("ALTER TABLE ").append(table.getName()).append("\n");
                                            sqlStatement.append("ADD CONSTRAINT ")
                                                .append(table.getName() + "_fk_" + newTable.getName())
                                                .append(" FOREIGN KEY ").append("(").append(fkName).append(")")
                                                .append(" REFERENCES ").append(newTable.getName())
                                                .append("(").append(relatedName).append(")")
                                                .append(";\n");
                                        }

                                    }
                                }
                            } else {

                                // Similarly, there are two steps in this case, the first step is to
                                // add the new primary key constraint in this table;
//                                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
//                                sqlStatement.append("ADD CONSTRAINT ").append(newColumn.getName() + "_pk")
//                                   .append(" PRIMARY KEY ").append("(").append().append(")").append(";\n");

                                // Then the second step is to add all new foreign key constraints in
                                // other tables.
                            }
                            // Do something here...
                        }
                        break;
                    }
                }
//                if (!alreadyExist) {
//                    createdColumns.add(newColumn);
//                }
            }


            if (deletePrimaryKey) {
                StringBuilder constraintNames = new StringBuilder();
                for (Column column : newTable.getPrimaryKey()) {
                    constraintNames.append(column.getName()).append(",");
                }
                constraintNames.deleteCharAt(constraintNames.lastIndexOf(","));
                sqlStatement.append("ALTER TABLE ").append(newTable.getName()).append("\n");
                sqlStatement.append("ADD CONSTRAINT ").append(newTable.getName() + "_pk")
                    .append(" PRIMARY KEY ").append("(").append(constraintNames)
                    .append(")").append(";\n");
            }

            System.out.println("Size of createdColumns: " + createdColumns.size());
            System.out.println("Size of deletedColumns: " + deletedColumns.size());

            sqlStatement.append("\n");
        }

        // For all tables that didn't be modified, we do not to change anything about them.
        // Since this function was invoked by

        return sqlStatement.toString();
    }


    // Simply check whether the the table with same ID have been modified. Still need to improve.
    public static boolean checkTableWhetherModified(Table tableInSchema, Table tableInDatabase) {
//        Boolean result = false;
        if (!tableInSchema.getName().equals(tableInDatabase.getName())) {
            return true;
        }
        if (tableInSchema.getColumnList().size() != tableInDatabase.getColumnList().size()) {
            return true;
        }
        for (Column newColumn: tableInSchema.getColumnList()) {
            boolean canFind = false;
            for (Column oldColumn: tableInDatabase.getColumnList()) {
                if (newColumn.getID().equals(oldColumn.getID())) {
//                if (newColumn.getName().equals(oldColumn.getName())) {
                    canFind = true;
                    if (!newColumn.getName().equals(oldColumn.getName()) && !newColumn.isForeign()) {
                        return true;
                    }
                    if (newColumn.isNullable() != oldColumn.isNullable()) {
                        return true;
                    }
                    if (newColumn.isPrimary() != oldColumn.isPrimary()) {
                        return true;
                    }
                    if (!newColumn.getDataType().equals(oldColumn.getDataType())) {
                        return true;
                    }
                    if (newColumn.isForeign() != oldColumn.isForeign()) {
                        return true;
                    }

                }
            }
            if (!canFind) {
                return true;
            }
        }
        return false;
    }

}
