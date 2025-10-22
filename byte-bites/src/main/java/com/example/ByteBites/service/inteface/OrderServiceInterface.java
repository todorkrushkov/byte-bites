package com.example.ByteBites.service.inteface;

import com.example.ByteBites.models.DTO.OrderRequestDTO;
import com.example.ByteBites.models.OrderItems;
import com.example.ByteBites.models.OrderStatus;
import com.example.ByteBites.models.Orders;
import org.springframework.stereotype.Service;

import java.util.List;


public interface OrderServiceInterface {
    Orders createOrder(Long customerId, Long restaurantId, OrderRequestDTO request);

    List<Orders> getAllOrders();

    List<Orders> getOrdersByCustomer(Long customerId);

    Orders updateOrderStatus(Long orderId, OrderStatus status);

    void deleteOrder(Long orderId);

    List<OrderItems> getOrderItemsByOrder(Long orderId);

    List<Orders> getOrdersByRestaurant(Long restaurantId);
}
