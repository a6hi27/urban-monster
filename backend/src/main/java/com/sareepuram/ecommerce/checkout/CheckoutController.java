package com.sareepuram.ecommerce.checkout;

import com.itextpdf.text.DocumentException;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.sareepuram.ecommerce.invoice.InvoiceService;
import com.sareepuram.ecommerce.order.Order;
import jakarta.mail.MessagingException;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


@Controller
//@RequestMapping("/user/checkout/")
public class CheckoutController {
    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserService userService;

    @GetMapping("/user/checkout")
    public ResponseEntity<?> initiateCheckout(HttpSession httpSession) throws Exception {
        User user = userService.getCurrentUser(httpSession);
        Payment payment = checkoutService.createCheckoutSession(user);
        for (Links link : payment.getLinks()) {
            if (link.getRel().equals("approval_url")) return new ResponseEntity<>(link.getHref(), HttpStatus.OK);
        }
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }

    @GetMapping("user/checkout/success")
    public String handleCheckoutSuccess(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, HttpSession httpSession, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getCurrentUser(httpSession);
            Payment response = checkoutService.executePayment(paymentId, payerId);
            if (response != null && "approved".equalsIgnoreCase(response.getState())) {
                Address shippingAddress = CheckoutService.getAddress(response);
                System.out.println(response.toJSON());
                Order placedOrder = orderService.addOrder(user, response, shippingAddress);
                try {
                    int orderId = placedOrder.getOrderId();
                    String invoiceNumber = "URB-MON-" + user.getUserId() + "-" + orderId;
                    // Generate the PDF byte array
                    byte[] pdfBytes = invoiceService.generateInvoice(response, invoiceNumber, orderId);
                    String orderCreationStatusDetail = "Order created successfully!";
                    // Save the pdf in filesystem
                    String pdfPath = invoiceService.saveInvoice(invoiceNumber, pdfBytes);
                    //Send email to customer
                    invoiceService.emailInvoice(invoiceNumber, pdfPath);

                    placedOrder.setOrderCreationStatus("CREATED");
                    placedOrder.setInvoiceURL(pdfPath);  // or a public URL if stored externally
                    placedOrder.setOrderCreationStatusDetails(orderCreationStatusDetail);
                    placedOrder = orderService.updateOrder(placedOrder);

                    redirectAttributes.addAttribute("orderId", orderId);
                    redirectAttributes.addAttribute("orderCreationStatus", placedOrder.getOrderCreationStatus());
                    redirectAttributes.addAttribute("orderCreationStatusDetail",
                            placedOrder.getOrderCreationStatusDetails());
                    return "redirect:http://localhost:8081/order-processing.html";

                } catch (DocumentException | IOException | MessagingException e) {
                    String orderCreationStatusDetail = "Payment completed but an error occurred in either invoice " + "generation or sending email. \n" + e;
                    placedOrder.setOrderCreationStatus("FAILED");
                    placedOrder.setOrderCreationStatusDetails(orderCreationStatusDetail);
                    Order pdfOrEmailFailedOrder = orderService.updateOrder(placedOrder);
                    redirectAttributes.addAttribute("orderId", pdfOrEmailFailedOrder.getOrderId());
                    redirectAttributes.addAttribute("orderCreationStatus", pdfOrEmailFailedOrder.getOrderCreationStatus());
                    redirectAttributes.addAttribute("orderCreationStatusDetail", orderCreationStatusDetail);
                }
                // Redirect to static HTML page
                return "redirect:http://localhost:8081/order-processing.html";

            } else if (response == null) {
                String orderCreationStatusDetail = "No response received from Paypal Payment Gateway or the request " + "to Paypal Gateway has timed out.";
                Order failedOrder = orderService.addOrder(user, "FAILED", orderCreationStatusDetail);
                redirectAttributes.addAttribute("orderId", failedOrder.getOrderId());
                redirectAttributes.addAttribute("orderCreationStatus", failedOrder.getOrderCreationStatus());
                redirectAttributes.addAttribute("orderCreationStatusDetail", orderCreationStatusDetail);
                return "redirect:http://localhost:8081/order-processing.html";

            } else {
                String orderCreationStatusDetails = "Payment not approved";
                Order failedOrder = orderService.addOrder(user, "FAILED", orderCreationStatusDetails);

                redirectAttributes.addAttribute("orderId", failedOrder.getOrderId());
                redirectAttributes.addAttribute("orderCreationStatus", failedOrder.getOrderCreationStatus());
                redirectAttributes.addAttribute("orderCreationStatusDetail",
                        failedOrder.getOrderCreationStatusDetails());
                return "redirect:http://localhost:8081/order-processing.html";
            }

        } catch (PayPalRESTException e) {
            Logger.getLogger(CheckoutController.class.getName()).log(Level.SEVERE, null, e);
            User user = userService.getCurrentUser(httpSession);
            String orderCreationStatusDetails = "An exception occurred in PayPal Gateway \n" + e.getDetails();
            Order failedOrder = orderService.addOrder(user, "FAILED", orderCreationStatusDetails);

            redirectAttributes.addAttribute("orderId", failedOrder.getOrderId());
            redirectAttributes.addAttribute("orderCreationStatus", failedOrder.getOrderCreationStatus());
            redirectAttributes.addAttribute("orderCreationStatusDetail", failedOrder.getOrderCreationStatusDetails());
            return "redirect:http://localhost:8081/order-processing.html";
        }
    }


    @GetMapping("user/checkout/cancel")
    public ResponseEntity<?> handleCheckoutCancellation() {
        return new ResponseEntity<>("Checkout cancelled by user", HttpStatus.OK);
    }

}
