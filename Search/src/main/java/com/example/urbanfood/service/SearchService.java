package com.example.urbanfood.service;

import com.example.urbanfood.model.Search;
import com.example.urbanfood.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {

    @Autowired
    private SearchRepository searchRepository;

    private SimpleJdbcCall searchProductsProc;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        // Updated to use the new enhanced search procedure
        this.searchProductsProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("search_products")
                .returningResultSet("PRODUCTCURSOR", (rs, rowNum) -> {
                    Search product = new Search();
                    product.setProductId(rs.getLong("PRODUCTID"));
                    product.setName(rs.getString("NAME"));
                    product.setWeight(rs.getBigDecimal("WEIGHT"));
                    product.setStatus(rs.getString("STATUS"));
                    product.setAmount(rs.getInt("AMOUNT"));
                    product.setDescription(rs.getString("DESCRIPTION"));
                    product.setExpiryDate(rs.getDate("EXPIRYDATE"));
                    product.setBrand(rs.getString("BRAND"));
                    product.setMadeDate(rs.getDate("MADEDATE"));
                    product.setPrice(rs.getBigDecimal("PRICE"));
                    product.setTags(rs.getString("TAGS"));
                    product.setMainImageName(rs.getString("MAINIMAGENAME"));
                    product.setShippingCost(rs.getBigDecimal("SHIPPINGCOST"));
                    return product;
                });
    }

    public List<Search> searchProducts(String searchText) {
        SqlParameterSource parameterSource = new MapSqlParameterSource()
                .addValue("SEARCHTEXT", searchText);

        Map<String, Object> result = searchProductsProc.execute(parameterSource);

        @SuppressWarnings("unchecked")
        List<Search> products = (List<Search>) result.get("PRODUCTCURSOR");

        return products;
    }
}

