package com.sareepuram.ecommerce.checkout;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.sareepuram.ecommerce.order.Order;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sareepuram.ecommerce.address.Address;
import com.sareepuram.ecommerce.order.OrderService;
import com.sareepuram.ecommerce.user.User;
import com.sareepuram.ecommerce.user.UserService;

import static com.sareepuram.ecommerce.checkout.CheckoutService.getAddress;


@Controller
public class CheckoutController {
    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/checkout")
    public ResponseEntity<?> initiateCheckout(HttpSession httpSession) throws Exception {
        User user = userService.getCurrentUser(httpSession);
        Payment payment = checkoutService.createCheckoutSession(user);
        for (Links link : payment.getLinks()) {
            if (link.getRel().equals("approval_url"))
                return new ResponseEntity<>(link.getHref(), HttpStatus.OK);
        }
        return new ResponseEntity<>("Cannot create PayPal checkout session!", HttpStatus.OK);
    }

    @GetMapping("/user/checkout/success")
    public ResponseEntity<?> executePayment(@RequestParam("paymentId") String paymentId,
                                 @RequestParam("PayerID") String payerId
            , HttpSession httpSession) {
        try {
            User user = userService.getCurrentUser(httpSession);
            Payment response = checkoutService.executePayment(paymentId, payerId);
            if (response != null && "approved".equalsIgnoreCase(response.getState())) {
                Address shippingAddress = getAddress(response);
                Order placedOrder = orderService.addOrder(user, response.getId(), shippingAddress);
                // Redirect to invoice generation, passing the payment response.
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (response == null)
                return "redirect:/checkout/failure?response=none";
            else
                return "redirect:/checkout/failure?response=paypal" + response.getFailureReason();
        } catch (PayPalRESTException e) {
            e.printStackTrace();
            return "redirect:/checkout/failure?response=" + e.getDetails();
        }
    }

    @GetMapping("/user/checkout/cancel")
    public ResponseEntity<?> handleCheckoutCancellation() {
        return new ResponseEntity<>("Checkout cancelled by user", HttpStatus.OK);
    }

    @GetMapping("/user/checkout/failure")
    public ResponseEntity<?> handlePaymentFailure(@RequestParam String response) {
        if (response.equals("none"))
            return new ResponseEntity<>("No response received from Paypal Payment Gateway or the request to Paypal " +
                    "Gateway has timed out.",
                    HttpStatus.BAD_GATEWAY);
        else if (response.startsWith("paypal"))
            return new ResponseEntity<>(response.substring(6), HttpStatus.BAD_REQUEST);
        else
            return new ResponseEntity<>(response, HttpStatus.BAD_GATEWAY);
    }

}
