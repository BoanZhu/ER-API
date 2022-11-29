package io.github.MigadaTang;

import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.transform.DatabaseUtil;
import io.github.MigadaTang.transform.GenerationSqlUtil;
import io.github.MigadaTang.transform.ParserUtil;
import io.github.MigadaTang.transform.Table;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Class for transforming between ER model and DDL
 */
public class Transform {

    /**
     * The function parse table in specify database to er model
     *
     * @param databaseType  the type of the database
     * @param hostname      the hostname of the database
     * @param portNum       the port number of database
     * @param databaseName  the name of database
     * @param userName      the username to log in database
     * @param password      the password to log in database
     * @throws ParseException           Exception that fail to mapping table and column to entity, relationship and attribute
     * @throws DBConnectionException    Exception that fail to read database information
     */
    public void relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password) throws ParseException, DBConnectionException {
        try {
            relationSchemasToERModel(databaseType, hostname, portNum, databaseName, userName, password, null);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (DBConnectionException e) {
            throw new DBConnectionException(e.getMessage());
        }
    }

    public void relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password, String imageName) throws ParseException, DBConnectionException {
        String dbUrl = "";
        try {
            dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
            relationSchemasToERModel(databaseType, dbUrl, userName, password, imageName);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (DBConnectionException e) {
            throw new DBConnectionException(e.getMessage());
        }
    }

    /**
     * The function parse table in specify database to er model
     *
     * @param databaseType  the type of the database
     * @param dbUrl         the url of the database
     * @param userName      the username to log in database
     * @param password      the password to log in database
     * @throws DBConnectionException    Exception that fail to read database information
     * @throws ParseException           Exception that fail to mapping table and column to entity, relationship and attribute
     */
    public void relationSchemasToERModel(RDBMSType databaseType, String dbUrl, String userName, String password, String imageName) throws DBConnectionException, ParseException {
        Connection conn = null;
        String driver = "";

        try {
            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            List<Table> tableList = DatabaseUtil.getDatabseInfo(conn);
            DatabaseUtil.closeDBConnection(conn);
            Schema schema = ParserUtil.parseAttributeToRelationship(tableList);
            schema = Schema.queryByID(schema.getID());
            schema.renderAsImage(imageName);
        } catch (ParseException parseException) {
            throw new ParseException(parseException.getMessage());
        } catch (DBConnectionException dbConnectionException) {
            throw new DBConnectionException(dbConnectionException.getMessage());
        } catch (SQLException throwables) {
            throw new DBConnectionException("Fail to create db statement");
        }
    }

    /**
     * Transform er model to data definition language
     *
     * @param schemaId The id of schema to generate DDL
     * @return  -  Sql Statement
     * @throws ParseException   Exception that fail to mapping entity, relationship and attribute to table and column
     */
    public String ERModelToSql(Long schemaId) throws ParseException {
        Schema schema = Schema.queryByID(schemaId);
        Map<Long, Table> tableDTOList;
        try {
            tableDTOList = ParserUtil.parseRelationshipsToAttribute(schema.getEntityList(), schema.getRelationshipList());
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        }
        String sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);
        return sqlStatement;
    }
}
