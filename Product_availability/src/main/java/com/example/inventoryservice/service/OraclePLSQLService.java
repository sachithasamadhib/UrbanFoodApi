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
    private SimpleJdbcCall checkAvailabilityProcedure;

    @Autowired
    public OraclePLSQLService(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;
        
        // Initialize the SimpleJdbcCall for the stored procedure
        this.checkAvailabilityProcedure = new SimpleJdbcCall(dataSource)
                .withProcedureName("CHECK_PRODUCT_AVAILABILITY")
                .withCatalogName("INVENTORY_PKG")
                .declareParameters(
                        new SqlParameter("p_product_id", Types.NUMERIC),
                        new SqlParameter("p_required_amount", Types.NUMERIC),
                        new SqlOutParameter("p_is_available", Types.NUMERIC)
                );
    }

    public boolean checkProductAvailabilityPLSQL(Long productId, Integer requiredAmount) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("p_product_id", productId)
                .addValue("p_required_amount", requiredAmount);
        
        Map<String, Object> result = checkAvailabilityProcedure.execute(parameterSource);
        
        // The stored procedure returns 1 for available, 0 for unavailable
        Integer isAvailable = (Integer) result.get("p_is_available");
        return isAvailable != null && isAvailable == 1;
    }
}

