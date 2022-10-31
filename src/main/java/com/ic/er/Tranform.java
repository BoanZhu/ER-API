package com.ic.er;

import com.ic.er.bean.dto.transform.TableDTO;
import com.ic.er.exception.DBConnectionException;
import com.ic.er.common.ResultState;
import com.ic.er.common.ResultStateCode;
import com.ic.er.exception.ParseException;
import com.ic.er.util.DatabaseUtil;
import com.ic.er.util.GenerationSqlUtil;

import java.awt.Image;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Tranform {

    public ResultState relationSchemasToERModel(String driver, String dbUrl, String userName, String password){
        Connection conn = null;
        ResultState resultState;
        try {
            conn = DatabaseUtil.acquireDBConnection(driver, dbUrl, userName, password);
            conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            List<TableDTO> tableDTOList = DatabaseUtil.getDatabseInfo(conn);

            Image erDiagram = null;

            DatabaseUtil.closeDBConnection(conn);
            resultState = ResultState.ok(tableDTOList);
        } catch (DBConnectionException | SQLException e) {
            resultState = ResultState.build(ResultStateCode.Failure, e.getMessage());
        }

        return resultState;
    }


    public ResultState ERModelToSql(Long viewId) {
        View view = View.queryByID(viewId);
        List<TableDTO> tableDTOList = new ArrayList<>();
        try {
            tableDTOList = GenerationSqlUtil.parseRelationshipsToAttribute(view.getEntityList(), view.getRelationshipList());
        } catch (ParseException e) {
            return ResultState.build(ResultStateCode.Failure, e.getMessage());
        }
        String sqlStatement = GenerationSqlUtil.toSqlStatement(tableDTOList);
        return ResultState.ok(sqlStatement);
    }
}
