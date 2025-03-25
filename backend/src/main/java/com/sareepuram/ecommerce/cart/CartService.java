package com.sareepuram.ecommerce.cart;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Collections;

import com.sareepuram.ecommerce.product.Product;
import com.sareepuram.ecommerce.product.ProductService;
import com.sareepuram.ecommerce.user.User;

@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;


    public CartDTO addOrUpdateProductInCart(CartDTO cartDTO, User user) throws IllegalArgumentException {
        Integer userId = user.getUserId();
        Integer productId = cartDTO.getProduct().getProductId();
        Integer quantity = cartDTO.getQuantity();
        Optional<Cart> cart = cartRepository.findByUser_UserIdAndProduct_ProductId(userId, productId);
        if (cart.isPresent()) {
            // Update existing product in cart
            Cart existingCart = cart.get();
            existingCart.setQuantity(cartDTO.getQuantity());
            existingCart = cartRepository.save(existingCart);
            CartDTO existingCartDTO = new CartDTO(existingCart.getProduct(), existingCart.getQuantity());
            return existingCartDTO;
        }
        // Add the new product in the cart
        Optional<Product> product = productService.findById(productId);
        Cart newCart = new Cart(user, product.get(), quantity);
        newCart = cartRepository.save(newCart);
        CartDTO newCartDTO = new CartDTO(newCart.getProduct(), newCart.getQuantity());
        return newCartDTO;
    }


    public List<CartDTO> getProductsInCart(User user) {
        Integer userId = user.getUserId();
        Optional<List<CartDTO>> productsInCart = cartRepository.findProductsInCartByUserId(userId);
        return productsInCart.orElse(Collections.emptyList());
    }


    public ResponseEntity<?> deleteProductFromCart(CartDTO cartDTO, User user) throws IllegalArgumentException {
        Integer userId = user.getUserId();
        Integer productId = cartDTO.getProduct().getProductId();
        Integer deletedRows = cartRepository.deleteByUser_UserIdAndProduct_ProductId(userId, productId);
        if (deletedRows > 0)
            return new ResponseEntity<>(HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Boolean deleteAllFromCart(User user) {
        Integer deletedRows = cartRepository.deleteAllByUser_UserId(user.getUserId());
        return deletedRows > 0;
    }

    public Long calculateTotalCartValue(List<CartDTO> productsInCart) {
        long cartTotal = 0L;
        for (CartDTO productInCart : productsInCart)
            cartTotal += productInCart.getProduct().getPrice() * productInCart.getQuantity();
        return cartTotal;
    }

}
