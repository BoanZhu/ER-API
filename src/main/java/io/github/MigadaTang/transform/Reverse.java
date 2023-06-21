package io.github.MigadaTang.transform;

import io.github.MigadaTang.Attribute;
import io.github.MigadaTang.Entity;
import io.github.MigadaTang.Relationship;
import io.github.MigadaTang.Schema;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Class for transforming between ER model and DDL
 */
public class Reverse {

    /**
     * The function parse table in specify database to er model
     *
     * @param databaseType the type of the database
     * @param hostname     the hostname of the database
     * @param portNum      the port number of database
     * @param databaseName the name of database
     * @param userName     the username to log in database
     * @param password     the password to log in database
     * @return the created schema
     * @throws ParseException        Exception that fail to mapping table and column to entity, relationship and attribute
     * @throws DBConnectionException Exception that fail to read database information
     */
    public Schema relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password, Boolean subsetRequirement)
        throws ParseException, DBConnectionException, IOException {
        Schema schema;
        try {
            schema = relationSchemasToERModel(databaseType, hostname, portNum, databaseName, userName, password, "", subsetRequirement);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (DBConnectionException e) {
            throw new DBConnectionException(e.getMessage());
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return schema;
    }

    public Schema relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password, String imageName, Boolean subsetRequirement)
        throws ParseException, DBConnectionException, IOException {
        String dbUrl = "";
        Schema schema;
        try {
            dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
            schema = relationSchemasToERModel(databaseType, dbUrl, userName, password, imageName);

            System.out.println("getEntityList().size(): " + schema.getEntityList().size());
            System.out.println("getRelationshipList().size(): " + schema.getRelationshipList().size());

            if (subsetRequirement) {
                GraphvizImplementation.useGraphviz(schema);
            } else {
                GraphvizImplementation.useGraphvizWithoutSubsetRequirement(schema);
            }
//            GraphvizImplementation.useGraphviz(schema);
//            GraphvizImplementation.useGraphvizWithoutSubsetRequirement(schema);
//            String json = schema.toRenderJSON();
//            System.out.println(json);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (DBConnectionException e) {
            throw new DBConnectionException(e.getMessage());
        }
        catch (IOException e) {
            throw new IOException(e.getMessage());
        }

        return schema;
    }

    /**
     * The function parse table in specify database to er model
     *
     * @param databaseType the type of the database
     * @param dbUrl        the url of the database
     * @param userName     the username to log in database
     * @param password     the password to log in database
     * @return the created schema
     * @throws DBConnectionException Exception that fail to read database information
     * @throws ParseException        Exception that fail to mapping table and column to entity, relationship and attribute
     */
    public Schema relationSchemasToERModel(RDBMSType databaseType, String dbUrl, String userName, String password, Boolean subsetRequirement) throws DBConnectionException, ParseException {
        Schema schema = relationSchemasToERModel(databaseType, dbUrl, userName, password, subsetRequirement);

        return schema;
    }

    /**
     * The function parse table in specify database to er model
     *
     * @param databaseType the type of the database
     * @param dbUrl        the url of the database
     * @param userName     the username to log in database
     * @param password     the password to log in database
     * @param imageName    the name of the image saved
     * @return the created schema
     * @throws DBConnectionException Exception that fail to read database information
     * @throws ParseException        Exception that fail to mapping table and column to entity, relationship and attribute
     */
    public Schema relationSchemasToERModel(RDBMSType databaseType, String dbUrl, String userName, String password, String imageName) throws DBConnectionException, ParseException {
        Connection conn = null;
        String driver = "";
        Schema schema;

        try {
            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            List<Table> tableList = DatabaseUtil.getDatabseInfo(conn);
            DatabaseUtil.closeDBConnection(conn);

            schema = ParserUtil.parseAttributeToRelationship(tableList);

            for (Entity entity: schema.getEntityList()) {
                System.out.println(entity.getName() + ", " + entity.getID());
                for (Attribute attribute: entity.getAttributeList()) {
                    System.out.println("    " + attribute.getName() + ", " + attribute.getID());
                }
            }
            for (Relationship relationship: schema.getRelationshipList()) {
                System.out.println(relationship.getName() + ", " + relationship.getID());
                for (Attribute attribute: relationship.getAttributeList()) {
                    System.out.println("    " + attribute.getName() + ", " + attribute.getID());
                }
            }

            System.out.println("------------------------------------");

            for (Table table: tableList) {
                System.out.println(table.getName() + ", " + table.getId());
                for (Column column: table.getColumnList()) {
                    System.out.println("        " + column.getName() + ", " + column);
                }
            }

            // Here we need to store the old tables generated.
            schema.setOldTables(tableList);

//            if (imageName != null) {
//                schema.renderAsImage(imageName);
//            }
        } catch (ParseException parseException) {
            throw new ParseException(parseException.getMessage());
        } catch (DBConnectionException dbConnectionException) {
            throw new DBConnectionException(dbConnectionException.getMessage());
        } catch (SQLException throwables) {
            throw new DBConnectionException("Fail to create db statement");
        }

        return schema;
    }

}
