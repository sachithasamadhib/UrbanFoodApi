package com.urbanfood.cart.repository;

import com.urbanfood.cart.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Query(value = "SELECT CART_ITEM_ID_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Long getNextCartItemId();
}