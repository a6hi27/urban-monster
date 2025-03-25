package com.sareepuram.ecommerce.invoice;

import com.itextpdf.text.DocumentException;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.sareepuram.ecommerce.user.User;
import com.sareepuram.ecommerce.user.UserService;
import com.sareepuram.ecommerce.util.EmailService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;



@Controller
public class InvoiceController {
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;

    @GetMapping("invoice/generate")
    public String generateAndSaveInvoice(@RequestParam("paymentId") String paymentId,
                                         @RequestParam("orderId") String orderId, HttpSession httpSession) throws PayPalRESTException, DocumentException, IOException, MessagingException {

        //Get details required for generating invoice pdf
        Payment payment = invoiceService.getPaymentDetails(paymentId);
        User user = userService.getCurrentUser(httpSession);
        String invoiceNumber = "URB-MON-" + user.getUserId() + "-" + orderId;

        // Generate the PDF byte array
        byte[] pdfBytes = invoiceService.generateInvoice(payment);

        // Save the pdf in filesystem
        String pdfPath = invoiceService.saveInvoice(invoiceNumber, pdfBytes);

         //Send email to customer
        invoiceService.emailInvoice(invoiceNumber, pdfPath);
        return pdfFilePath;
    }
}
