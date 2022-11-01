package com.ic.er.util;

import com.ic.er.bean.dto.transform.ColumnDTO;
import com.ic.er.bean.dto.transform.TableDTO;
import com.ic.er.common.RDBMSType;
import com.ic.er.common.Utils;
import com.ic.er.exception.DBConnectionException;
import com.ic.er.exception.ParseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseUtil {
    public static List<TableDTO> getDatabseInfo(Connection conn) throws DBConnectionException {
        List<TableDTO> tableDTOList = new ArrayList<>();
        Map<String, Map<String, ColumnDTO>> columnTracker = new HashMap<>();
        Map<String, TableDTO> tableTracker = new HashMap<>();
        try {
            DatabaseMetaData meta = null;
            meta = conn.getMetaData();

            String catalog = null;
            String schemaPattern = null;// meta.getUserName();
            String tableNamePattern = null;
            String[] types = { "TABLE" };
            ResultSet tableRs = meta.getTables(catalog, schemaPattern, tableNamePattern, types);


            while (tableRs.next()) {
                String tableName = tableRs.getString("TABLE_NAME");
                TableDTO table = new TableDTO();
                table.setName(tableName);
                table.setId(Utils.generateID());

//                ResultSet columnRs = meta.getColumns(catalog, schemaPattern, tableName, null);
                PreparedStatement statement = conn.prepareStatement("select * from " + tableName);
                ResultSet columnRs = statement.executeQuery();
                ResultSetMetaData rsmd = columnRs.getMetaData();
                List<ColumnDTO> columnDTOList = new ArrayList<>();
                Map<String, ColumnDTO> columnTrackInTable = new HashMap<>();

                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    ColumnDTO columnDTO = new ColumnDTO();
                    String columnName = rsmd.getColumnName(i);
                    String columnDataType = rsmd.getColumnTypeName(i);
                    int nullable = rsmd.isNullable(i);
                    if (nullable >= 1) {
                        columnDTO.setNullable(true);
                    } else {
                        columnDTO.setNullable(false);
                    }
                    columnDTO.setDataType(columnDataType);
                    columnDTO.setName(columnName);
                    columnDTO.setBelongTo(table.getId());
                    columnDTOList.add(columnDTO);
                    columnTrackInTable.put(columnName, columnDTO);
                }

                table.setColumnDTOList(columnDTOList);
                tableDTOList.add(table);

                columnTracker.put(tableName, columnTrackInTable);
                tableTracker.put(tableName, table);
            }

            for (TableDTO table : tableDTOList) {
                ResultSet foreignKeyRs = meta.getImportedKeys(catalog, schemaPattern, table.getName());
                ResultSet primaryKeyRs = meta.getPrimaryKeys(catalog, schemaPattern, table.getName());
                Map<String, ColumnDTO> columnTrackInTable = columnTracker.get(table.getName());
                List<ColumnDTO> primaryKeyList = new ArrayList<>();
                while(primaryKeyRs.next()) {
                    String name = primaryKeyRs.getString("COLUMN_NAME");
                    ColumnDTO pk = columnTrackInTable.get(name);
                    pk.setPrimary(true);
                    primaryKeyList.add(pk);
                }
                table.setPrimaryKey(primaryKeyList);

                while(foreignKeyRs.next()) {
                    String name = foreignKeyRs.getString("FKCOLUMN_NAME");
                    ColumnDTO fk = columnTrackInTable.get(name);
                    fk.setIsForeign(1);
                    String foreignTableName = foreignKeyRs.getString("PKTABLE_NAME");
                    fk.setForeignKeyTable(tableTracker.get(foreignTableName).getId());
                }
            }

        } catch (SQLException e) {
            throw new DBConnectionException("Fail to read the tables or columns info from database.");
        }

        return tableDTOList;
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
            return "jdbc:oracle:thin:@" + hostname + ":" + portNum + ":" +databaseName;
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
