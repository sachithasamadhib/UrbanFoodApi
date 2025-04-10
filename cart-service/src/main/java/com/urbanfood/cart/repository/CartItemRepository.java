package com.urbanfood.cart.repository;

import com.urbanfood.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartCartId(Long cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.cartId = :cartId AND ci.productId = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Long cartId,
                                                @Param("productId") Long productId);
}