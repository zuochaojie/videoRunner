package com.example.test.type;

import org.apache.ibatis.type.*;
import org.springframework.context.annotation.Configuration;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Configuration
@MappedTypes({UUID.class, String.class})
public class UUIDTypeHandler extends BaseTypeHandler<Object> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i,
                                    Object parameter, JdbcType jdbcType) throws SQLException {
        if (parameter instanceof UUID) {
            ps.setObject(i, parameter.toString(), JdbcType.OTHER.TYPE_CODE);
        } else if (parameter instanceof String) {
            ps.setObject(i, parameter, JdbcType.OTHER.TYPE_CODE);
        } else {
            ps.setObject(i, parameter);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return rs.getString(columnName);
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getString(columnIndex);
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return cs.getString(columnIndex);
    }
}
