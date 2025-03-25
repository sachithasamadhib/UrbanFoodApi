package com.example.urbanfood.repository;

import com.example.urbanfood.model.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchRepository extends JpaRepository<Search, Long> {

    //optionally
    //    List<Product> findByNameContainingIgnoreCase(String name);
    //
    //    @Query(value = "BEGIN get_product_by_name(:name, :result); END;", nativeQuery = true)
    //    List<Product> getProductByNameUsingProcedure(@Param("name") String name);

}

