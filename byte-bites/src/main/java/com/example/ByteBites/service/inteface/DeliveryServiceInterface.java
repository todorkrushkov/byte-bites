package com.example.ByteBites.service.inteface;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.Deliveries;
import com.example.ByteBites.models.DeliveryStatus;
import com.example.ByteBites.models.Orders;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DeliveryServiceInterface {
    List<Orders> getAvailableDeliveries(Long ownerId);
    String acceptDelivery(Long orderId, Accounts deliver);
    String updateDeliveryStatus(Long deliveryId, DeliveryStatus status);
    List<Deliveries> getDeliveriesByDeliver(Long deliverId);
    String acceptOrderByOwner(Long orderId);
    List<Orders> getReadyForPickupOrders(Long deliverId);
    String markOrderAsReady(Long orderId);
}
