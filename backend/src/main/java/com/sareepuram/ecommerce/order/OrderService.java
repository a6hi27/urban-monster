package com.sareepuram.ecommerce.order;

import com.sareepuram.ecommerce.address.Address;
import com.sareepuram.ecommerce.cart.Cart;
import com.sareepuram.ecommerce.cart.CartRepository;
import com.sareepuram.ecommerce.cart.CartService;
import com.sareepuram.ecommerce.item.Item;
import com.sareepuram.ecommerce.product.Product;
import com.sareepuram.ecommerce.product.ProductRepository;
import com.sareepuram.ecommerce.user.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    public List<Order> getOrders(Integer userId) throws IllegalArgumentException {
        Optional<List<Order>> orders = orderRepository.findByUser_UserId(userId);
        return orders.orElse(Collections.emptyList());
    }

    @Transactional
    public Order addOrder(User user, String paymentId, Address shippingAddress) {
        List<Cart> cartsInOrder = cartRepository.findAllByUser_UserId(user.getUserId());
        List<Item> itemsInOrder = cartsInOrder.stream().map(cart -> {
            Product product = cart.getProduct();
            int availableQuantity = cart.getProduct().getAvailableQuantity();
            int orderedQuantity = cart.getQuantity();
            product.setAvailableQuantity(availableQuantity - orderedQuantity);
            productRepository.save(product);
            return new Item(cart.getProduct(), cart.getQuantity());
        }).toList();
        Order order = new Order(user, itemsInOrder, paymentId, shippingAddress);
        order = orderRepository.save(order);

        //Clear the user's cart after placing the order
        cartService.deleteAllFromCart(user);
        return order;
    }
}
