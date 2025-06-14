package com.sareepuram.ecommerce.checkout;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import com.sareepuram.ecommerce.address.Address;
import com.sareepuram.ecommerce.cart.CartDTO;
import com.sareepuram.ecommerce.cart.CartService;
import com.sareepuram.ecommerce.invoice.InvoiceService;
import com.sareepuram.ecommerce.order.OrderService;
import com.sareepuram.ecommerce.user.User;
import com.sareepuram.ecommerce.user.UserService;


import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Data
@Service
public class CheckoutService {


    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private APIContext apiContext;


    public Payment createCheckoutSession(User user) throws PayPalRESTException {

        //Get the list of products and their quantity from cart
        List<CartDTO> productsInCart = cartService.getProductsInCart(user);

        //Calculate the total cart amount
        Double subTotal = cartService.calculateCartSubTotal(productsInCart) + 0D;
        Double tax = 0.18 * subTotal;
        Double shippingFee = 40D;
        Double totalAmount = subTotal + tax + shippingFee;
        Amount amount = new Amount();
        amount.setCurrency(CheckoutConstants.CURRENCY);
        amount.setTotal(String.format("%.2f", totalAmount));
        Details details = new Details();
        details.setShipping(shippingFee.toString());
        details.setTax(tax.toString());
        details.setSubtotal(subTotal.toString());
        amount.setDetails(details);

        ItemList itemList = new ItemList();
        List<Item> items = new ArrayList<>();
        for (CartDTO productInCart : productsInCart) {
            items.add(new Item(productInCart.getProduct().getName(), String.valueOf(productInCart.getQuantity()),
                    String.valueOf(productInCart.getProduct().getPrice()), "USD"));
        }
        itemList.setItems(items);

        Transaction transaction = new Transaction();
        transaction.setDescription(CheckoutConstants.DESCRIPTION);
        transaction.setAmount(amount);
        transaction.setItemList(itemList);
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod("PayPal");

        Payment payment = new Payment();
        payment.setIntent(CheckoutConstants.INTENT);
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(CheckoutConstants.CANCEL_URL);
        redirectUrls.setReturnUrl(CheckoutConstants.SUCCESS_URL);
        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        Payment response = payment.execute(apiContext, paymentExecute);
        return response;
    }

    public static Address getAddress(Payment payment) {
        String addressLine1 = payment.getPayer().getPayerInfo().getShippingAddress().getLine1();
        String addressLine2 = payment.getPayer().getPayerInfo().getShippingAddress().getLine2();
        String city = payment.getPayer().getPayerInfo().getShippingAddress().getCity();
        String countryCode = payment.getPayer().getPayerInfo().getShippingAddress().getCountryCode();
        String state = payment.getPayer().getPayerInfo().getShippingAddress().getState();
        String zipCode = payment.getPayer().getPayerInfo().getShippingAddress().getPostalCode();
        return new Address(addressLine1, addressLine2, city, countryCode, state, zipCode);
    }

}


