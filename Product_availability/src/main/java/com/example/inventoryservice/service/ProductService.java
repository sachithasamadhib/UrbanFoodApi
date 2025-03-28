package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.AvailabilityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Service
public class ProductService {
    
    private final SimpleJdbcCall checkAvailabilityProc;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ProductService(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.checkAvailabilityProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("check_product_availability")
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new org.springframework.jdbc.core.SqlParameter("p_product_id", Types.NUMERIC),
                        new org.springframework.jdbc.core.SqlParameter("p_required_amount", Types.NUMERIC)
                );
        this.jdbcTemplate = jdbcTemplate;
    }

    public AvailabilityResponse checkAvailability(Long productId, Integer requiredAmount) {
        return jdbcTemplate.execute(
                "{call check_product_availability(?, ?, ?, ?)}",
                (CallableStatementCallback<AvailabilityResponse>) cs -> {
                    cs.setLong(1, productId);
                    cs.setInt(2, requiredAmount);
                    cs.registerOutParameter(3, Types.VARCHAR);
                    cs.registerOutParameter(4, Types.NUMERIC);
                    cs.execute();

                    String status = cs.getString(3);
                    Integer availableQty = cs.getInt(4);

                    return new AvailabilityResponse(status, availableQty);
                }
        );
    }
}

