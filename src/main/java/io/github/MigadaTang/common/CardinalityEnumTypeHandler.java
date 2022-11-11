package io.github.MigadaTang.common;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CardinalityEnumTypeHandler extends BaseTypeHandler<Cardinality> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Cardinality parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public Cardinality getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return Cardinality.getFromCode(rs.getInt(columnName));
    }

    @Override
    public Cardinality getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return Cardinality.getFromCode(rs.getInt(columnIndex));
    }

    @Override
    public Cardinality getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return Cardinality.getFromCode(cs.getInt(columnIndex));
    }
}