package io.github.MigadaTang;

import io.github.MigadaTang.bean.dto.transform.TableDTO;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.util.DatabaseUtil;
import io.github.MigadaTang.util.GenerationSqlUtil;
import io.github.MigadaTang.util.ParserUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Transform {

    public void relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password) throws ParseException, DBConnectionException {
        String dbUrl = "";
        try {
            dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
            relationSchemasToERModel(databaseType, dbUrl, userName, password);
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        } catch (DBConnectionException e) {
            throw new DBConnectionException(e.getMessage());
        }
    }

    public void relationSchemasToERModel(RDBMSType databaseType, String dbUrl, String userName, String password) throws DBConnectionException, ParseException {
        Connection conn = null;
        String driver = "";

        try {
            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            List<TableDTO> tableDTOList = DatabaseUtil.getDatabseInfo(conn);
            DatabaseUtil.closeDBConnection(conn);
            Schema schema = ParserUtil.parseAttributeToRelationship(tableDTOList);
            schema = ER.querySchemaByID(schema.getID());
            String renderJSONStatement = schema.toRenderJSON();
            Render.render(renderJSONStatement);
        } catch (ParseException parseException) {
            throw new ParseException(parseException.getMessage());
        } catch (DBConnectionException dbConnectionException) {
            throw new DBConnectionException(dbConnectionException.getMessage());
        } catch (SQLException throwables) {
            throw new DBConnectionException("Fail to create db statement");
        }
    }


    public String ERModelToSql(Long viewId) throws ParseException {
        Schema schema = Schema.queryByID(viewId);
        Map<Long, TableDTO> tableDTOList;
        try {
            tableDTOList = ParserUtil.parseRelationshipsToAttribute(schema.getEntityList(), schema.getRelationshipList());
        } catch (ParseException e) {
            throw new ParseException(e.getMessage());
        }
        String sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);
        return sqlStatement;
    }
}
