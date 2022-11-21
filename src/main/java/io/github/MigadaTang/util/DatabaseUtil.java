package io.github.MigadaTang.util;

import io.github.MigadaTang.Column;
import io.github.MigadaTang.Table;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtil {
    public static List<Table> getDatabseInfo(Connection conn) throws DBConnectionException {
        List<Table> tableList = new ArrayList<>();
        Map<String, Map<String, Column>> columnTracker = new HashMap<>();
        Map<String, Table> tableTracker = new HashMap<>();
        try {
            DatabaseMetaData meta = conn.getMetaData();

            String catalog = null;
            String schemaPattern = null;// meta.getUserName();
            String tableNamePattern = null;
            String[] types = {"TABLE"};
            ResultSet tableRs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);


            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                Table table = new Table();
                table.setName(tableName);
                table.setId(RandomUtils.generateID());

//                ResultSet columnRs = meta.getColumns(catalog, schemaPattern, tableName, null);
                PreparedStatement statement = conn.prepareStatement("select * from " + tableName);
                ResultSet columnRs = statement.executeQuery();
                ResultSetMetaData rsmd = columnRs.getMetaData();
                List<Column> columnList = new ArrayList<>();
                Map<String, Column> columnTrackInTable = new HashMap<>();

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    Column column = new Column();
                    String columnName = rsmd.getColumnName(i);
                    String columnDataType = rsmd.getColumnTypeName(i);
                    int nullable = rsmd.isNullable(i);
                    if (nullable >= 1) {
                        column.setNullable(true);
                    } else {
                        column.setNullable(false);
                    }
                    column.setID(RandomUtils.generateID());
                    column.setDataType(columnDataType);
                    column.setName(columnName);
                    column.setBelongTo(table.getId());
                    columnList.add(column);
                    columnTrackInTable.put(columnName, column);
                }

                table.setColumnList(columnList);
                tableList.add(table);

                columnTracker.put(tableName, columnTrackInTable);
                tableTracker.put(tableName, table);
            }

            for (Table table : tableList) {
                ResultSet foreignKeyRs = meta.getImportedKeys(catalog, schemaPattern, table.getName());
                ResultSet primaryKeyRs = meta.getPrimaryKeys(catalog, schemaPattern, table.getName());
                Map<String, Column> columnTrackInTable = columnTracker.get(table.getName());
                List<Column> primaryKeyList = new ArrayList<>();
                while (primaryKeyRs.next()) {
                    String name = primaryKeyRs.getString("COLUMN_NAME");
                    Column pk = columnTrackInTable.get(name);
                    pk.setPrimary(true);
                    primaryKeyList.add(pk);
                }
                table.setPrimaryKey(primaryKeyList);

                Map<Long, List<Column>> foreignKeyList = new HashMap<>();
                while (foreignKeyRs.next()) {
                    // TODO how to recognize multi column combine one fk
                    String name = foreignKeyRs.getString("FKCOLUMN_NAME");
                    List<Column> fks = new ArrayList<>();
                    Column fk = columnTrackInTable.get(name);
                    fk.setForeign(true);
                    String foreignTableName = foreignKeyRs.getString("PKTABLE_NAME");
                    String foreignColumnName = foreignKeyRs.getString("PKCOLUMN_NAME");
                    Long foreignTableId = tableTracker.get(foreignTableName).getId();
                    fk.setForeignKeyTable(foreignTableId);
                    fk.setForeignKeyColumnName(foreignColumnName);
                    fk.setForeignKeyColumn(columnTracker.get(foreignTableName).get(foreignColumnName).getID());
                    fks.add(fk);
                    foreignKeyList.put(foreignTableId, fks);
                }
                table.setForeignKey(foreignKeyList);
            }

        } catch (SQLException e) {
            throw new DBConnectionException("Fail to read the tables or columns info from database.");
        }

        return tableList;
    }


    public static void closeDBConnection(Connection connection) throws DBConnectionException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throw new DBConnectionException("Fail to close the database connection");
            }
        }
    }


    public static Connection acquireDBConnection(String driver, String dbUrl, String userName, String password) throws DBConnectionException {
        Connection conn = null;
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(dbUrl, userName, password);
        } catch (ClassNotFoundException e) {
            throw new DBConnectionException("Driver not found: " + driver);
        } catch (SQLException e) {
            throw new DBConnectionException("Fail to connect the database. Database Url: " + dbUrl + " User name"
                    + userName + " Password: " + password);
        }
        return conn;
    }


    public static String generateDatabaseURL(RDBMSType databaseType, String hostname, String portNum, String databaseName) throws ParseException {
        if (databaseType == RDBMSType.MYSQL) {
            return "jdbc:mysql://" + hostname + ":" + portNum + "/" + databaseName;
        } else if (databaseType == RDBMSType.ORACLE) {
            return "jdbc:oracle:thin:@" + hostname + ":" + portNum + ":" + databaseName;
        } else if (databaseType == RDBMSType.DB2) {
            return "jdbc:db2://" + hostname + ":" + portNum + "/" + databaseName;
        } else if (databaseType == RDBMSType.H2) {
            return "jdbc:h2:tcp://" + hostname + ":" + portNum + "/" + databaseName;
        } else if (databaseType == RDBMSType.POSTGRESQL) {
            return "jdbc:postgresql://" + hostname + ":" + portNum + "/" + databaseName;
        } else if (databaseType == RDBMSType.SQLSERVER) {
            return "jdbc:sqlserver://" + hostname + ":" + portNum + ";DatabaseName=" + databaseName;
        }
        throw new ParseException("Cannot recognise current RDBMS type: " + databaseType.toString());
    }

    public static String recognDriver(RDBMSType databaseType) throws ParseException {
        if (databaseType == RDBMSType.MYSQL) {
            return "com.mysql.jdbc.Driver";
        } else if (databaseType == RDBMSType.ORACLE) {
            return "oracle.jdbc.driver.OracleDriver";
        } else if (databaseType == RDBMSType.DB2) {
            return "com.ibm.db2.jdbc.net.DB2Driver";
        } else if (databaseType == RDBMSType.H2) {
            return "org.h2.Driver";
        } else if (databaseType == RDBMSType.POSTGRESQL) {
            return "org.postgresql.Driver";
        } else if (databaseType == RDBMSType.SQLSERVER) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        }
        throw new ParseException("Cannot recognise current RDBMS type: " + databaseType.toString());
    }
}
