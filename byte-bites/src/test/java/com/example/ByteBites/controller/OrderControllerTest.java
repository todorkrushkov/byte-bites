package com.example.ByteBites.controller;

import com.example.ByteBites.models.*;
import com.example.ByteBites.models.DTO.OrderRequestDTO;
import com.example.ByteBites.service.inteface.OrderServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderServiceInterface orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        OrderRequestDTO requestDTO = new OrderRequestDTO();
        Orders mockOrder = new Orders();
        when(orderService.createOrder(1L, 2L, requestDTO)).thenReturn(mockOrder);

        ResponseEntity<Orders> response = orderController.createOrder(1L, 2L, requestDTO);

        assertEquals(mockOrder, response.getBody());
        verify(orderService).createOrder(1L, 2L, requestDTO);
    }

    @Test
    void testGetAllOrders() {
        List<Orders> orders = Arrays.asList(new Orders(), new Orders());
        when(orderService.getAllOrders()).thenReturn(orders);

        ResponseEntity<List<Orders>> response = orderController.getAllOrders();

        assertEquals(2, response.getBody().size());
        verify(orderService).getAllOrders();
    }

    @Test
    void testGetOrdersByCustomer() {
        List<Orders> customerOrders = Arrays.asList(new Orders(), new Orders());
        when(orderService.getOrdersByCustomer(5L)).thenReturn(customerOrders);

        ResponseEntity<List<Orders>> response = orderController.getOrdersByCustomer(5L);

        assertEquals(2, response.getBody().size());
        verify(orderService).getOrdersByCustomer(5L);
    }

    @Test
    void testUpdateOrderStatus() {
        Orders updatedOrder = new Orders();
        when(orderService.updateOrderStatus(3L, OrderStatus.CONFIRMED)).thenReturn(updatedOrder);

        ResponseEntity<Orders> response = orderController.updateOrderStatus(3L, OrderStatus.CONFIRMED);

        assertEquals(updatedOrder, response.getBody());
        verify(orderService).updateOrderStatus(3L, OrderStatus.CONFIRMED);
    }

    @Test
    void testDeleteOrder() {
        ResponseEntity<String> response = orderController.deleteOrder(6L);

        assertEquals("Поръчката беше изтрита успешно!", response.getBody());
        verify(orderService).deleteOrder(6L);
    }

    @Test
    void testGetOrderItems() {
        List<OrderItems> items = Arrays.asList(new OrderItems(), new OrderItems());
        when(orderService.getOrderItemsByOrder(4L)).thenReturn(items);

        ResponseEntity<List<OrderItems>> response = orderController.getOrderItems(4L);

        assertEquals(2, response.getBody().size());
        verify(orderService).getOrderItemsByOrder(4L);
    }
}

