package com.sareepuram.ecommerce.cart;

import java.util.List;

import com.sareepuram.ecommerce.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sareepuram.ecommerce.user.UserService;

import jakarta.servlet.http.HttpSession;


@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @GetMapping("user/cart")
    public ResponseEntity<List<CartDTO>> getProductsInCart(HttpSession httpSession) {
        List<CartDTO> products = cartService.getProductsInCart(userService.getCurrentUser(httpSession));
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("user/cart")
    public ResponseEntity<CartDTO> addOrUpdateProductInCart(@RequestBody CartDTO cartDTO, HttpSession httpSession) {
        try {
            return new ResponseEntity<>(cartService.addOrUpdateProductInCart(cartDTO,
                    userService.getCurrentUser(httpSession)),
                    HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("user/cart")
    public ResponseEntity<?> deleteProductFromCart(@RequestBody CartDTO cartDTO, HttpSession httpSession) {
        try {
            return new ResponseEntity<>(cartService.deleteProductFromCart(cartDTO,
                    userService.getCurrentUser(httpSession)), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("user/cart/all")
    public ResponseEntity<?> deleteAllFromCart(HttpSession httpSession) {

        if (cartService.deleteAllFromCart(userService.getCurrentUser(httpSession)))
            return new ResponseEntity<>("Cart cleared successfully!", HttpStatus.OK);
        return new ResponseEntity<>("Cart is empty already!", HttpStatus.OK);

    }
}
