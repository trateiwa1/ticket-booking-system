package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.PaymentRequest;
import com.example.ticketbookingsystem.dto.PaymentResponse;
import com.example.ticketbookingsystem.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService){
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponse>> viewPayments(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponse> payments = paymentService.viewAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request){
        PaymentResponse payment = paymentService.processPayment(request);
        return ResponseEntity
                .status(201)
                .body(payment);
    }
}
