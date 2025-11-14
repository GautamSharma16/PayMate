// BillController.java
package com.gautam.billingsoftware.controller;

import com.gautam.billingsoftware.entity.OrderEntity;
import com.gautam.billingsoftware.service.OrderService;
import com.gautam.billingsoftware.service.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bills")
@RequiredArgsConstructor
public class BillController {

    private final OrderService orderService;
    private final BillService billService;

    @GetMapping("/order/{orderId}/pdf")
    public ResponseEntity<byte[]> generateBillPdf(@PathVariable String orderId) {
        try {
            OrderEntity order = orderService.getOrderById(orderId);
            byte[] pdfBytes = billService.generateBillPdf(order);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "bill-" + orderId + ".pdf");
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/order/{orderId}/html")
    public ResponseEntity<String> generateBillHtml(@PathVariable String orderId) {
        try {
            OrderEntity order = orderService.getOrderById(orderId);
            String htmlContent = billService.generateBillHtml(order);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(htmlContent);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}