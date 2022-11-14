package io.github.MigadaTang;

import io.github.MigadaTang.bean.dto.transform.TableDTO;
import io.github.MigadaTang.common.RDBMSType;
import io.github.MigadaTang.common.ResultState;
import io.github.MigadaTang.common.ResultStateCode;
import io.github.MigadaTang.exception.DBConnectionException;
import io.github.MigadaTang.exception.ParseException;
import io.github.MigadaTang.util.DatabaseUtil;
import io.github.MigadaTang.util.GenerationSqlUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Tranform {

    public ResultState relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password) {
        ResultState resultState;
        String dbUrl = "";
        try {
            dbUrl = DatabaseUtil.generateDatabaseURL(databaseType, hostname, portNum, databaseName);
        } catch (ParseException e) {
            resultState = ResultState.build(ResultStateCode.Failure, e.getMessage());
            return resultState;
        }

        resultState = relationSchemasToERModel(databaseType, dbUrl, userName, password);

        return resultState;
    }

    public ResultState relationSchemasToERModel(RDBMSType databaseType, String dbUrl, String userName, String password) {
        Connection conn = null;
        ResultState resultState;
        String driver = "";

        try {
            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            List<TableDTO> tableDTOList = DatabaseUtil.getDatabseInfo(conn);
            Schema schema = ParserUtil.parseAttributeToRelationship(tableDTOList);

            DatabaseUtil.closeDBConnection(conn);
            resultState = ResultState.ok(schema);
        } catch (DBConnectionException | SQLException | ParseException e) {
            resultState = ResultState.build(ResultStateCode.Failure, e.getMessage());
            return resultState;
        }

        return resultState;
    }


    public ResultState ERModelToSql(Long viewId) {
        Schema schema = Schema.queryByID(viewId);
        Map<Long, TableDTO> tableDTOList;
        try {
            tableDTOList = ParserUtil.parseRelationshipsToAttribute(schema.getEntityList(), schema.getRelationshipList());
        } catch (ParseException e) {
            return ResultState.build(ResultStateCode.Failure, e.getMessage());
        }
        String sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);
        return ResultState.ok(sqlStatement);
    }
}
