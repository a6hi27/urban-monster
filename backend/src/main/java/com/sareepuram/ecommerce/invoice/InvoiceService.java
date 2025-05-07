package com.sareepuram.ecommerce.invoice;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import com.sareepuram.ecommerce.util.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InvoiceService {
    @Autowired
    private APIContext apiContext;

    @Autowired
    private EmailService emailService;

    public Payment getPaymentDetails(String paymentId) throws PayPalRESTException {
        return Payment.get(apiContext, paymentId);
    }


    public byte[] generateInvoice(Payment payment, String invoiceNumber, int orderId) throws DocumentException, IOException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        String recipientName = payment.getTransactions().get(0).getItemList().getShippingAddress().getRecipientName();
        String line1 = payment.getTransactions().get(0).getItemList().getShippingAddress().getLine1();
        String line2 = payment.getTransactions().get(0).getItemList().getShippingAddress().getLine2() != null ?
                payment.getTransactions().get(0).getItemList().getShippingAddress().getLine2() + "\n" : "";
        String city = payment.getTransactions().get(0).getItemList().getShippingAddress().getCity();
        String countryCode = payment.getTransactions().get(0).getItemList().getShippingAddress().getCountryCode();
        String postalCode = payment.getTransactions().get(0).getItemList().getShippingAddress().getPostalCode();
        String state = payment.getTransactions().get(0).getItemList().getShippingAddress().getState();
        String email = payment.getTransactions().get(0).getPayee().getEmail();

        String billingAddress =
                recipientName + "\n" + line1 + "\n" + line2 + city + "\n" + countryCode + "\n" + postalCode + "\n" + state + "\n" + "Email: " + email + "\n";
        int itemsLength = payment.getTransactions().get(0).getItemList().getItems().size();

        Image logo = Image.getInstance("E:\\Abhi_CS\\Ecommerce\\Backend\\urbanmonster_logo.png");
        logo.scaleAbsolute(100, 50);
        document.add(logo);

        // Set fonts
        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

        // Add header
        Paragraph header = new Paragraph("Tax Invoice", headerFont);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);
        document.add(new Paragraph(" ")); // Add blank space

        // Seller Information Table
        PdfPTable sellerTable = new PdfPTable(2);
        sellerTable.setWidthPercentage(100);
        sellerTable.setSpacingBefore(10);
        sellerTable.setSpacingAfter(10);

        sellerTable.addCell(new PdfPCell(new Phrase("Sold By: Urban Monster", boldFont)));
        sellerTable.addCell(new Phrase("Your shop address"));
        sellerTable.addCell(new PdfPCell(new Phrase("Billing Address: ", boldFont)));
        sellerTable.addCell(new Phrase(billingAddress));
        sellerTable.addCell(new PdfPCell(new Phrase("GST Registration No: Your GST Registration No", normalFont)));
        sellerTable.addCell(new PdfPCell(new Phrase("Shipping Address:" + billingAddress)));

        document.add(sellerTable);

        // Invoice Metadata Table
        PdfPTable metadataTable = new PdfPTable(2);
        metadataTable.setWidthPercentage(100);
        metadataTable.setSpacingBefore(10);
        metadataTable.setSpacingAfter(10);

        metadataTable.addCell(new PdfPCell(new Phrase("Invoice Number: " + invoiceNumber,
                boldFont)));
        System.out.println("The reference id is " + payment.getTransactions().toString());
        metadataTable.addCell(new PdfPCell(new Phrase("Order Number: " + orderId)));
        metadataTable.addCell(new PdfPCell(new Phrase("Order Date: " + payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getCreateTime().substring(0, 11)
                , normalFont)));

        document.add(metadataTable);

        // Items Table
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setSpacingBefore(10);
        itemsTable.setSpacingAfter(10);

        itemsTable.addCell(new PdfPCell(new Phrase("Sl. No", boldFont)));
        itemsTable.addCell(new PdfPCell(new Phrase("Product Name", boldFont)));
        itemsTable.addCell(new PdfPCell(new Phrase("Unit Price", boldFont)));
        itemsTable.addCell(new PdfPCell(new Phrase("Qty", boldFont)));
        itemsTable.addCell(new PdfPCell(new Phrase("Total Amount", boldFont)));

        //Adding the item list in the invoice table
        for (int i = 0; i < itemsLength;
             i++) {
            itemsTable.addCell(new PdfPCell(new Phrase(Integer.toString(i + 1))));
            itemsTable.addCell(new PdfPCell(new Phrase(payment.getTransactions().get(0).getItemList().getItems().get(i).getName())));
            String itemPrice = payment.getTransactions().get(0).getItemList().getItems().get(i).getPrice();
            String quantity = payment.getTransactions().get(0).getItemList().getItems().get(i).getQuantity();
            itemsTable.addCell(new PdfPCell(new Phrase("₹ " + itemPrice)));
            itemsTable.addCell(new PdfPCell(new Phrase(quantity)));
            itemsTable.addCell(new PdfPCell(new Phrase(" " + Integer.parseInt(quantity) * Double.parseDouble(itemPrice))));
        }

        document.add(itemsTable);


        // Total Amount
        Paragraph subTotal =
                new Paragraph("Items Subtotal: ₹" + payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getAmount().getDetails().getSubtotal(),
                        boldFont);

        Paragraph shipping =
                new Paragraph("Shipping fee: ₹" + payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getAmount().getDetails().getShipping(),
                        boldFont);

        Paragraph totalAmount =
                new Paragraph("Total Amount: ₹" + payment.getTransactions().get(0).getRelatedResources().get(0).getSale().getAmount().getTotal(),
                        boldFont);

        subTotal.setAlignment(Element.ALIGN_RIGHT);
        shipping.setAlignment(Element.ALIGN_RIGHT);
        totalAmount.setAlignment(Element.ALIGN_RIGHT);

        document.add(subTotal);
        document.add(shipping);
        document.add(totalAmount);

        // Footer
        Paragraph footer = new Paragraph("Thank you for shopping with us!", boldFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }


    public String saveInvoice(String invoiceNumber, byte[] pdfBytes) throws IOException, MessagingException {

        String fileName = invoiceNumber + ".pdf";
        String uploadDir = "E:\\Abhi_CS\\Ecommerce";
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Save the PDF to the directory
        Path filePath = uploadPath.resolve(fileName);
        Files.write(filePath, pdfBytes);

        // Return the file path or URL for later retrieval
        return filePath.toString();
    }

    public void emailInvoice(String invoiceNumber, String pdfPath) throws MessagingException {
        emailService.sendInvoiceEmail("abhinavm2705@gmail.com", invoiceNumber, pdfPath);
    }

}


