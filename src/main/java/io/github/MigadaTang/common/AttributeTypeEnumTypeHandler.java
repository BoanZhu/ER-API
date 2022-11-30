package io.github.MigadaTang.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AttributeTypeEnumTypeHandler extends BaseTypeHandler<AttributeType> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AttributeType parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public AttributeType getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return AttributeType.getFromCode(rs.getInt(columnName));
    }

    @Override
    public AttributeType getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return AttributeType.getFromCode(rs.getInt(columnIndex));
    }

    @Override
    public AttributeType getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return AttributeType.getFromCode(cs.getInt(columnIndex));
    }
}