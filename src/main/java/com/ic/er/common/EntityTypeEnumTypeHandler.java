package com.ic.er.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EntityTypeEnumTypeHandler extends BaseTypeHandler<EntityType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, EntityType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public EntityType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return EntityType.getFromCode(rs.getInt(columnName));
    }

    @Override
    public EntityType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return EntityType.getFromCode(rs.getInt(columnIndex));
    }

    @Override
    public EntityType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return EntityType.getFromCode(cs.getInt(columnIndex));
    }
}