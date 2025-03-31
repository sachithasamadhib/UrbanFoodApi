package com.urbanfood.cart.repository;

import com.urbanfood.cart.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    @Query(value = "SELECT CART_ID_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Long getNextCartId();

    @Query(value = "BEGIN add_to_cart(:userId, :productId, :quantity, :price); END;", nativeQuery = true)
    void callAddToCartProcedure(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity,
            @Param("price") BigDecimal price);

    @Query(value = "BEGIN remove_from_cart(:userId, :productId); END;", nativeQuery = true)
    void callRemoveFromCartProcedure(
            @Param("userId") Long userId,
            @Param("productId") Long productId);

    @Query(value = "BEGIN update_cart_quantity(:userId, :productId, :quantity); END;", nativeQuery = true)
    void callUpdateCartQuantityProcedure(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity);

    @Query(value = "BEGIN clear_cart(:userId); END;", nativeQuery = true)
    void callClearCartProcedure(@Param("userId") Long userId);
}