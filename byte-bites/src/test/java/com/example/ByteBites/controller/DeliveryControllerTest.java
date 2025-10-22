package com.example.ByteBites.controller;

import com.example.ByteBites.models.*;
import com.example.ByteBites.service.inteface.DeliveryServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DeliveryControllerTest {

    @Mock
    private DeliveryServiceInterface deliveryService;

    @InjectMocks
    private DeliveryController deliveryController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetReadyForPickupOrders() {
        // Създаване на mock Accounts обект (доставчик)
        Accounts deliverer = new Accounts();
        deliverer.setId(1L);  // Присвояваме ID на доставчика

        // Създаване на поръчки
        Orders order1 = new Orders();
        Orders order2 = new Orders();
        List<Orders> mockOrders = Arrays.asList(order1, order2);

        // Мока на услугата за връщане на поръчки, които са READY_FOR_PICKUP за този доставчик
        when(deliveryService.getReadyForPickupOrders(deliverer.getId())).thenReturn(mockOrders);

        // Извикваме метода с доставчика (текущия доставчик - чрез Accounts)
        ResponseEntity<List<Orders>> response = deliveryController.getReadyForPickupOrders(deliverer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(deliveryService, times(1)).getReadyForPickupOrders(deliverer.getId());
    }

    @Test
    void testAcceptDelivery() {
        Long orderId = 1L;
        Accounts deliverer = new Accounts();
        deliverer.setId(100L);

        when(deliveryService.acceptDelivery(orderId, deliverer))
                .thenReturn("Order accepted successfully");

        ResponseEntity<String> response = deliveryController.acceptDelivery(orderId, deliverer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Order accepted successfully", response.getBody());
        verify(deliveryService, times(1)).acceptDelivery(orderId, deliverer);
    }

    @Test
    void testUpdateDeliveryStatus() {
        Long deliveryId = 10L;
        DeliveryStatus newStatus = DeliveryStatus.IN_PROGRESS;

        when(deliveryService.updateDeliveryStatus(deliveryId, newStatus))
                .thenReturn("Delivery status updated");

        ResponseEntity<String> response = deliveryController.updateDeliveryStatus(deliveryId, newStatus);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Delivery status updated", response.getBody());
        verify(deliveryService, times(1)).updateDeliveryStatus(deliveryId, newStatus);
    }

    @Test
    void testGetDeliveriesByDeliver() {
        Long deliverId = 200L;
        Deliveries d1 = new Deliveries();
        Deliveries d2 = new Deliveries();
        List<Deliveries> mockList = Arrays.asList(d1, d2);

        when(deliveryService.getDeliveriesByDeliver(deliverId)).thenReturn(mockList);

        ResponseEntity<List<Deliveries>> response = deliveryController.getDeliveriesByDeliver(deliverId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(deliveryService, times(1)).getDeliveriesByDeliver(deliverId);
    }

    // New test for OWNER accepting an order and changing status to CONFIRMED
    @Test
    void testAcceptOrderByOwner() {
        Long orderId = 1L;

        when(deliveryService.acceptOrderByOwner(orderId))
                .thenReturn("Поръчката е успешно приета и е в процес на приготвяне!");

        ResponseEntity<String> response = deliveryController.acceptOrderByOwner(orderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Поръчката е успешно приета и е в процес на приготвяне!", response.getBody());
        verify(deliveryService, times(1)).acceptOrderByOwner(orderId);
    }

    // New test for OWNER marking the order as ready for pickup
    @Test
    void testMarkOrderAsReady() {
        Long orderId = 1L;

        when(deliveryService.markOrderAsReady(orderId))
                .thenReturn("Поръчката е маркирана като готова за взимане!");

        ResponseEntity<String> response = deliveryController.markOrderAsReady(orderId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Поръчката е маркирана като готова за взимане!", response.getBody());
        verify(deliveryService, times(1)).markOrderAsReady(orderId);
    }
}
