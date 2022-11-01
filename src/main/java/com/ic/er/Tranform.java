package com.ic.er;

import com.ic.er.bean.dto.transform.TableDTO;
import com.ic.er.common.RDBMSType;
import com.ic.er.exception.DBConnectionException;
import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import com.ic.er.exception.ParseException;
import com.ic.er.util.DatabaseUtil;
import com.ic.er.util.DrawingUtil;
import com.ic.er.util.GenerationSqlUtil;

import java.awt.Image;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tranform {

    public ResultState relationSchemasToERModel(RDBMSType databaseType, String hostname, String portNum, String databaseName
            , String userName, String password){
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

    public ResultState relationSchemasToERModel(RDBMSType databaseType, String dbUrl, String userName, String password){
        Connection conn = null;
        ResultState resultState;
        String driver = "";

        try {
            driver = DatabaseUtil.recognDriver(databaseType);
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            List<TableDTO> tableDTOList = DatabaseUtil.getDatabseInfo(conn);
            View view = ParserUtil.parseAttributeToRelationship(tableDTOList);

            Image erDiagram = DrawingUtil.drawingERModel();

            DatabaseUtil.closeDBConnection(conn);
            resultState = ResultState.ok(view);
        } catch (DBConnectionException | SQLException | ParseException e) {
            resultState = ResultState.build(ResultStateCode.Failure, e.getMessage());
            return resultState;
        }

        return resultState;
    }


    public ResultState ERModelToSql(Long viewId) {
        View view = View.queryByID(viewId);
        Map<Long, TableDTO> tableDTOList;
        try {
            tableDTOList = ParserUtil.parseRelationshipsToAttribute(view.getEntityList(), view.getRelationshipList());
        } catch (ParseException e) {
            return ResultState.build(ResultStateCode.Failure, e.getMessage());
        }
        String sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);
        return ResultState.ok(sqlStatement);
    }
}
