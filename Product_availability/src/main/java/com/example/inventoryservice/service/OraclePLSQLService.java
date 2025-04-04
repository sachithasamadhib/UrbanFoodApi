package com.example.inventoryservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Service
public class OraclePLSQLService {

    private final JdbcTemplate jdbcTemplate;
    private SimpleJdbcCall checkAvailabilityCall;

    @Autowired
    public OraclePLSQLService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.checkAvailabilityCall = new SimpleJdbcCall(jdbcTemplate)
                .withProcedureName("check_product_availability")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter("p_product_id", Types.NUMERIC),
                        new SqlParameter("p_required_amount", Types.NUMERIC),
                        new SqlOutParameter("p_is_available", Types.NUMERIC),
                        new SqlOutParameter("p_available_quantity", Types.NUMERIC)
                );
    }

    public Map<String, Object> checkProductAvailability(Long productId, Integer requiredAmount) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("p_product_id", productId)
                .addValue("p_required_amount", requiredAmount);

        return checkAvailabilityCall.execute(parameterSource);
    }
}