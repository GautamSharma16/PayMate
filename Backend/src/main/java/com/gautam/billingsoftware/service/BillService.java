
package com.gautam.billingsoftware.service;

import com.gautam.billingsoftware.entity.OrderEntity;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BillService {

    public byte[] generateBillPdf(OrderEntity order) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            // Add company header
            addHeader(document, order);

            // Add customer details
            addCustomerDetails(document, order);

            // Add order items table
            addOrderItems(document, order);

            // Add totals
            addTotals(document, order);

            // Add footer
            addFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF bill", e);
        }
    }

    public String generateBillHtml(OrderEntity order) {
        StringBuilder html = new StringBuilder();

        html.append("""
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 10px; }
                    .company-name { font-size: 24px; font-weight: bold; }
                    .bill-title { font-size: 20px; margin: 10px 0; }
                    .section { margin: 15px 0; }
                    .table { width: 100%; border-collapse: collapse; margin: 10px 0; }
                    .table th, .table td { border: 1px solid #ddd; padding: 8px; text-align: left; }
                    .table th { background-color: #f2f2f2; }
                    .totals { float: right; margin-top: 20px; }
                    .footer { margin-top: 50px; text-align: center; font-size: 12px; color: #666; }
                </style>
            </head>
            <body>
            """);

        // Add header
        html.append("""
            <div class="header">
                <div class="company-name">Your Company Name</div>
                <div class="bill-title">TAX INVOICE</div>
            </div>
            """);


        html.append(String.format("""
            <div class="section">
                <div><strong>Bill To:</strong> %s</div>
                <div><strong>Order ID:</strong> %s</div>
                <div><strong>Date:</strong> %s</div>
            </div>
            """,
                order.getCustomerName() != null ? order.getCustomerName() : "Walk-in Customer",
                order.getOrderId(),
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ));

        // Add items table
        html.append("""
            <table class="table">
                <thead>
                    <tr>
                        <th>Item</th>
                        <th>Quantity</th>
                        <th>Price</th>
                        <th>Total</th>
                    </tr>
                </thead>
                <tbody>
            """);

        order.getItems().forEach(item -> {
            double itemTotal = item.getPrice() * item.getQuantity();
            html.append(String.format("""
                <tr>
                    <td>%s</td>
                    <td>%d</td>
                    <td>₹%.2f</td>
                    <td>₹%.2f</td>
                </tr>
                """,
                    item.getName(),
                    item.getQuantity(),
                    item.getPrice(),
                    itemTotal
            ));
        });

        html.append("""
                </tbody>
            </table>
            """);

        // Add totals
        html.append(String.format("""
            <div class="totals">
                <div><strong>Subtotal:</strong> ₹%.2f</div>
                <div><strong>Tax (3%%):</strong> ₹%.2f</div>
                <div><strong>Grand Total:</strong> ₹%.2f</div>
            </div>
            """,
                order.getSubtotal(),
                order.getTax(),
                order.getGrandTotal()
        ));

        // Add footer
        html.append("""
            <div class="footer">
                <div>Thank you for your business!</div>
                <div>For queries, contact: +91-XXXXXXXXXX</div>
            </div>
            </body>
            </html>
            """);

        return html.toString();
    }

    private void addHeader(Document document, OrderEntity order) throws DocumentException {
        Font companyFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

        Paragraph company = new Paragraph("CROMA", companyFont);
        company.setAlignment(Element.ALIGN_CENTER);
        document.add(company);

        Paragraph title = new Paragraph("TAX INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
    }

    private void addCustomerDetails(Document document, OrderEntity order) throws DocumentException {
        Paragraph customer = new Paragraph();
        customer.add(new Chunk("Bill To: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        customer.add(new Chunk(order.getCustomerName() != null ? order.getCustomerName() : "Walk-in Customer"));
        customer.add(Chunk.NEWLINE);

        customer.add(new Chunk("Phn no: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        customer.add(new Chunk(order.getPhoneNumber() != null ? order.getPhoneNumber() : "Walk-in Customer"));
        customer.add(Chunk.NEWLINE);

        customer.add(new Chunk("Order ID: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        customer.add(new Chunk(order.getOrderId()));
        customer.add(Chunk.NEWLINE);

        customer.add(new Chunk("Date: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        customer.add(new Chunk(order.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        customer.setSpacingAfter(15);
        document.add(customer);
    }

    private void addOrderItems(Document document, OrderEntity order) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        // Table headers
        table.addCell(createCell("Item", true));
        table.addCell(createCell("Quantity", true));
        table.addCell(createCell("Price", true));
        table.addCell(createCell("Total", true));

        // Table rows
        order.getItems().forEach(item -> {
            double itemTotal = item.getPrice() * item.getQuantity();
            table.addCell(createCell(item.getName(), false));
            table.addCell(createCell(String.valueOf(item.getQuantity()), false));
            table.addCell(createCell("₹" + String.format("%.2f", item.getPrice()), false));
            table.addCell(createCell("₹" + String.format("%.2f", itemTotal), false));
        });

        document.add(table);
    }

    private void addTotals(Document document, OrderEntity order) throws DocumentException {
        Paragraph totals = new Paragraph();
        totals.setAlignment(Element.ALIGN_RIGHT);
        totals.add(new Chunk("Subtotal: ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        totals.add(new Chunk("₹" + String.format("%.2f", order.getSubtotal())));
        totals.add(Chunk.NEWLINE);

        totals.add(new Chunk("Tax (3%): ", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
        totals.add(new Chunk("₹" + String.format("%.2f", order.getTax())));
        totals.add(Chunk.NEWLINE);

        totals.add(new Chunk("Grand Total: ", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
        totals.add(new Chunk("₹" + String.format("%.2f", order.getGrandTotal())));
        totals.setSpacingBefore(10);
        document.add(totals);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph();
        footer.add("Thank you for your Shopping with us!\n");
        footer.add("For queries, contact: +91-6205499691");
        footer.setAlignment(Element.ALIGN_CENTER);
        footer.setSpacingBefore(50);
        document.add(footer);
    }

    private PdfPCell createCell(String content, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(content));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        if (isHeader) {
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        }
        return cell;
    }
}