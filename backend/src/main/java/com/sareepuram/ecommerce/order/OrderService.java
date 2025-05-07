package com.sareepuram.ecommerce.order;

import com.paypal.api.payments.Payment;
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

import java.util.*;
import java.util.stream.Collectors;

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

    public Order getOrderByOrderId(Integer orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        return order.orElse(null);
    }

    @Transactional
    public Order addOrder(User user, Payment response, Address shippingAddress) {
        String paymentId = response.getId();
        String totalAmount = response.getTransactions().get(0).getAmount().getTotal();
        String subTotal = response.getTransactions().get(0).getAmount().getDetails().getSubtotal();
        String tax = response.getTransactions().get(0).getAmount().getDetails().getTax();
        String shippingFee = response.getTransactions().get(0).getAmount().getDetails().getShipping();
        List<Cart> cartsInOrder = cartRepository.findAllByUser_UserId(user.getUserId());
        List<Item> itemsInOrder = cartsInOrder.stream().map(cart -> {
            Product product = cart.getProduct();
            int availableQuantity = cart.getProduct().getAvailableQuantity();
            int orderedQuantity = cart.getQuantity();
            product.setAvailableQuantity(availableQuantity - orderedQuantity);
            productRepository.save(product);
            return new Item(cart.getProduct(), cart.getQuantity());
        }).collect(Collectors.toCollection(ArrayList::new));
        Order order = new Order(user, itemsInOrder, paymentId, shippingAddress, totalAmount, subTotal, tax, shippingFee);
        order = orderRepository.save(order);

        //Clear the user's cart after placing the order
        cartService.deleteAllFromCart(user);
        return order;
    }

    public Order addOrder(User user, String orderCreationStatus, String orderCreationStatusDetails) {
        Order failedOrder = new Order(user, orderCreationStatus, orderCreationStatusDetails);
        failedOrder = orderRepository.save(failedOrder);
        return failedOrder;
    }

    public Optional<String> getOrderCreationStatus(Integer orderId) {
        Optional<String> orderCreationStatus = orderRepository.findOrderCreationStatusByOrderId(orderId);
        return orderCreationStatus;
    }

    public Order updateOrder(Order order) {
        return orderRepository.save(order);
    }
}
