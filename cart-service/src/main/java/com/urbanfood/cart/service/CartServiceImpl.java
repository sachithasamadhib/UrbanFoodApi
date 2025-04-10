package com.urbanfood.cart.service;

import com.urbanfood.cart.dto.CartDTO;
import com.urbanfood.cart.dto.CartItemDTO;
import com.urbanfood.cart.dto.ResponseDTO;
import com.urbanfood.cart.model.Cart;
import com.urbanfood.cart.model.CartItem;
import com.urbanfood.cart.model.Product;
import com.urbanfood.cart.repository.CartItemRepository;
import com.urbanfood.cart.repository.CartRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public ResponseDTO<CartDTO> getCart(Long userId) {
        try {
            Optional<Cart> cartOptional = cartRepository.findActiveCartByUserId(userId);

            if (cartOptional.isEmpty()) {
                return ResponseDTO.success("No active cart found", createEmptyCartDTO(userId));
            }

            Cart cart = cartOptional.get();
            List<CartItem> cartItems = cartItemRepository.findByCartCartId(cart.getCartId());

            // Load product details for each cart item
            for (CartItem item : cartItems) {
                Product product = entityManager.find(Product.class, item.getProductId());
                if (product != null) {
                    item.setProduct(product);
                }
            }

            return ResponseDTO.success("Cart retrieved successfully", mapToCartDTO(cart, cartItems));
        } catch (Exception e) {
            return ResponseDTO.error("Failed to retrieve cart: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO<CartDTO> addToCart(Long userId, Long productId, Integer quantity) {
        try {
            // Verify product exists and is available
            Product product = entityManager.find(Product.class, productId);
            if (product == null) {
                return ResponseDTO.error("Product not found");
            }
            if (!"AVAILABLE".equals(product.getStatus())) {
                return ResponseDTO.error("Product is not available for purchase");
            }
            if (product.getAmount() < quantity) {
                return ResponseDTO.error("Only " + product.getAmount() + " items available");
            }

            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("add_to_cart")
                    .declareParameters(
                            new SqlParameter("p_user_id", Types.NUMERIC),
                            new SqlParameter("p_product_id", Types.NUMERIC),
                            new SqlParameter("p_quantity", Types.NUMERIC),
                            new SqlOutParameter("p_success", Types.NUMERIC),
                            new SqlOutParameter("p_message", Types.VARCHAR),
                            new SqlOutParameter("p_cart_id", Types.NUMERIC)
                    );

            SqlParameterSource paramMap = new MapSqlParameterSource()
                    .addValue("p_user_id", userId)
                    .addValue("p_product_id", productId)
                    .addValue("p_quantity", quantity);

            Map<String, Object> result = jdbcCall.execute(paramMap);
            Number successNumber = (Number) result.get("p_success");
            int success = successNumber != null ? successNumber.intValue() : 0;
            String message = (String) result.get("p_message");

            if (success == 1) {
                return getCart(userId);
            } else {
                return ResponseDTO.error(message != null ? message : "Failed to add item to cart");
            }
        } catch (Exception e) {
            return ResponseDTO.error("Failed to add item to cart: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO<CartDTO> updateCartItem(Long userId, Long productId, Integer quantity) {
        try {
            // Verify product exists and is available
            Product product = entityManager.find(Product.class, productId);
            if (product == null) {
                return ResponseDTO.error("Product not found");
            }
            if (!"AVAILABLE".equals(product.getStatus())) {
                return ResponseDTO.error("Product is not available for purchase");
            }
            if (product.getAmount() < quantity) {
                return ResponseDTO.error("Only " + product.getAmount() + " items available");
            }

            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("update_cart_quantity")
                    .declareParameters(
                            new SqlParameter("p_user_id", Types.NUMERIC),
                            new SqlParameter("p_product_id", Types.NUMERIC),
                            new SqlParameter("p_quantity", Types.NUMERIC),
                            new SqlOutParameter("p_success", Types.NUMERIC),
                            new SqlOutParameter("p_message", Types.VARCHAR)
                    );

            SqlParameterSource paramMap = new MapSqlParameterSource()
                    .addValue("p_user_id", userId)
                    .addValue("p_product_id", productId)
                    .addValue("p_quantity", quantity);

            Map<String, Object> result = jdbcCall.execute(paramMap);
            Number successNumber = (Number) result.get("p_success");
            int success = successNumber != null ? successNumber.intValue() : 0;
            String message = (String) result.get("p_message");

            if (success == 1) {
                return getCart(userId);
            } else {
                return ResponseDTO.error(message != null ? message : "Failed to update cart item quantity");
            }
        } catch (Exception e) {
            return ResponseDTO.error("Failed to update cart item: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO<CartDTO> removeFromCart(Long userId, Long productId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("remove_from_cart")
                    .declareParameters(
                            new SqlParameter("p_user_id", Types.NUMERIC),
                            new SqlParameter("p_product_id", Types.NUMERIC),
                            new SqlOutParameter("p_success", Types.NUMERIC),
                            new SqlOutParameter("p_message", Types.VARCHAR)
                    );

            SqlParameterSource paramMap = new MapSqlParameterSource()
                    .addValue("p_user_id", userId)
                    .addValue("p_product_id", productId);

            Map<String, Object> result = jdbcCall.execute(paramMap);
            Number successNumber = (Number) result.get("p_success");
            int success = successNumber != null ? successNumber.intValue() : 0;
            String message = (String) result.get("p_message");

            if (success == 1) {
                return getCart(userId);
            } else {
                return ResponseDTO.error(message != null ? message : "Failed to remove item from cart");
            }
        } catch (Exception e) {
            return ResponseDTO.error("Failed to remove item from cart: " + e.getMessage());
        }
    }

    @Override
    public ResponseDTO<CartDTO> clearCart(Long userId) {
        try {
            SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate)
                    .withProcedureName("clear_cart")
                    .declareParameters(
                            new SqlParameter("p_user_id", Types.NUMERIC),
                            new SqlOutParameter("p_success", Types.NUMERIC),
                            new SqlOutParameter("p_message", Types.VARCHAR)
                    );

            SqlParameterSource paramMap = new MapSqlParameterSource()
                    .addValue("p_user_id", userId);

            Map<String, Object> result = jdbcCall.execute(paramMap);
            Number successNumber = (Number) result.get("p_success");
            int success = successNumber != null ? successNumber.intValue() : 0;
            String message = (String) result.get("p_message");

            if (success == 1) {
                return ResponseDTO.success(message, createEmptyCartDTO(userId));
            } else {
                return ResponseDTO.error(message != null ? message : "Failed to clear cart");
            }
        } catch (Exception e) {
            return ResponseDTO.error("Failed to clear cart: " + e.getMessage());
        }
    }

    private CartDTO createEmptyCartDTO(Long userId) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setUserId(userId);
        cartDTO.setItems(new ArrayList<>());
        cartDTO.setTotalPrice(0.0);
        cartDTO.setTotalShippingCost(0.0);
        cartDTO.setGrandTotal(0.0);
        return cartDTO;
    }

    private CartDTO mapToCartDTO(Cart cart, List<CartItem> cartItems) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setUserId(cart.getUserId());
        cartDTO.setCreatedAt(cart.getCreatedAt());
        cartDTO.setUpdatedAt(cart.getUpdatedAt());
        cartDTO.setStatus(cart.getStatus());

        double totalPrice = 0.0;
        double totalShippingCost = 0.0;

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        for (CartItem item : cartItems) {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setCartItemId(item.getCartItemId());
            itemDTO.setProductId(item.getProductId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setAddedAt(item.getAddedAt());
            itemDTO.setExpiresAt(item.getAddedAt().plusMinutes(20));

            Product product = item.getProduct();
            if (product != null) {
                itemDTO.setProductName(product.getProductName());
                itemDTO.setProductImage(product.getMainImageName());
                itemDTO.setPrice(product.getPrice());
                itemDTO.setShippingCost(product.getShippingCost() != null ? product.getShippingCost() : 0.0);
                itemDTO.setItemTotal(product.getPrice() * item.getQuantity());

                totalPrice += itemDTO.getItemTotal();
                totalShippingCost += itemDTO.getShippingCost();
            }

            cartItemDTOs.add(itemDTO);
        }

        cartDTO.setItems(cartItemDTOs);
        cartDTO.setTotalPrice(totalPrice);
        cartDTO.setTotalShippingCost(totalShippingCost);
        cartDTO.setGrandTotal(totalPrice + totalShippingCost);

        return cartDTO;
    }
}