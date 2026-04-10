package org.erick.orderproducer.controller;

import org.erick.orderproducer.dto.CreateOrderRequest;
import org.erick.orderproducer.dto.OrderResponse;
import org.erick.orderproducer.service.OrderPublisherService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderPublisherService orderPublisherService;

    public OrderController(OrderPublisherService orderPublisherService) {
        this.orderPublisherService = orderPublisherService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderPublisherService.createOrder(request);
    }
}
