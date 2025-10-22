package com.example.ByteBites.controller;

import com.example.ByteBites.models.*;
import com.example.ByteBites.models.DTO.OrderRequestDTO;
import com.example.ByteBites.service.OrderService;
import com.example.ByteBites.service.inteface.OrderServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins ="http://localhost:3000")

public class OrderController {

    private final OrderServiceInterface orderService;

    public OrderController(OrderServiceInterface orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/create/customer/{customerId}/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Orders> createOrder(
            @PathVariable Long customerId,
            @PathVariable Long restaurantId,
            @RequestBody OrderRequestDTO request) {
        return ResponseEntity.ok(orderService.createOrder(customerId, restaurantId, request));
    }

    @GetMapping
    public ResponseEntity<List<Orders>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Orders>> getOrdersByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }


    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('DELIVER')")
    public ResponseEntity<Orders> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }


    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('DELIVER')")
    public ResponseEntity<String> deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("Поръчката беше изтрита успешно!");
    }

    @GetMapping("/{orderId}/items")
    public ResponseEntity<List<OrderItems>> getOrderItems(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderItemsByOrder(orderId));
    }

    @GetMapping("/restaurant/{restaurantId}/orders")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<Orders>> getOrdersByRestaurant(@PathVariable Long restaurantId) {
        List<Orders> orders = orderService.getOrdersByRestaurant(restaurantId);
        return ResponseEntity.ok(orders);
    }
}

