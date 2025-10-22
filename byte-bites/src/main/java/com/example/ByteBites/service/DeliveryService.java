package com.example.ByteBites.service;

import com.example.ByteBites.models.*;
import com.example.ByteBites.repository.AccountRepository;
import com.example.ByteBites.repository.DeliveriesRepository;
import com.example.ByteBites.repository.OrdersRepository;
import com.example.ByteBites.service.inteface.DeliveryServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeliveryService implements DeliveryServiceInterface {
    private final DeliveriesRepository deliveriesRepository;
    private final OrdersRepository ordersRepository;
    private final AccountRepository accountsRepository;

    public DeliveryService(DeliveriesRepository deliveriesRepository, OrdersRepository ordersRepository, AccountRepository accountsRepository) {
        this.deliveriesRepository = deliveriesRepository;
        this.ordersRepository = ordersRepository;
        this.accountsRepository = accountsRepository;
    }

    @Override
    public List<Orders> getAvailableDeliveries(Long ownerId) {
        List<Orders> pendingOrders = ordersRepository.findByStatus(OrderStatus.PENDING);

        return pendingOrders.stream()
                .filter(order -> order.getRestaurant().getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Orders> getReadyForPickupOrders(Long deliverId) {
        List<Orders> readyForPickupOrders = ordersRepository.findByStatus(OrderStatus.READY_FOR_PICKUP);

        return readyForPickupOrders.stream()
                .filter(order -> deliveriesRepository.findByOrder(order).isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public String acceptDelivery(Long orderId, Accounts deliver) {
        Optional<Orders> orderOpt = ordersRepository.findById(orderId);

        if (orderOpt.isEmpty()) {
            return "Поръчката не съществува!";
        }

        Orders order = orderOpt.get();

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            return "Поръчката не е готова за доставка!";
        }

        if (deliveriesRepository.findByOrder(order).isPresent()) {
            return "Поръчката вече има назначен доставчик!";
        }

        Deliveries delivery = new Deliveries();
        delivery.setOrder(order);
        delivery.setDeliver(deliver);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        deliveriesRepository.save(delivery);

        order.setStatus(OrderStatus.ON_THE_WAY);
        ordersRepository.save(order);

        return "Успешно приехте поръчката за доставка!";
    }

    @Override
    public String acceptOrderByOwner(Long orderId) {
        Optional<Orders> orderOpt = ordersRepository.findById(orderId);

        if (orderOpt.isEmpty()) {
            return "Поръчката не съществува!";
        }

        Orders order = orderOpt.get();

        if (order.getStatus() != OrderStatus.PENDING) {
            return "Поръчката не може да бъде приета, тя не е в статус PENDING!";
        }

        order.setStatus(OrderStatus.CONFIRMED);
        ordersRepository.save(order);

        return "Поръчката е успешно приета и готова за взимане!";
    }

    @Override
    public String markOrderAsReady(Long orderId) {
        Optional<Orders> orderOpt = ordersRepository.findById(orderId);

        if (orderOpt.isEmpty()) {
            return "Поръчката не съществува!";
        }

        Orders order = orderOpt.get();

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            return "Поръчката не може да бъде маркирана като готова, защото тя не е в процес на приготвяне!";
        }

        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        ordersRepository.save(order);

        return "Поръчката е маркирана като готова за взимане!";
    }


    @Override
    public String updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Optional<Deliveries> deliveryOpt = deliveriesRepository.findById(deliveryId);

        if (deliveryOpt.isEmpty()) {
            return "Доставката не е намерена!";
        }

        Deliveries delivery = deliveryOpt.get();
        Orders order = delivery.getOrder();

        if (status == DeliveryStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.ON_THE_WAY);
        } else if (status == DeliveryStatus.COMPLETED) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            return "Невалиден статус за тази поръчка!";
        }

        ordersRepository.save(order);
        delivery.setStatus(status);
        deliveriesRepository.save(delivery);

        return "Статусът на доставката е променен!";
    }

    @Override
    public List<Deliveries> getDeliveriesByDeliver(Long deliverId) {
        Optional<Accounts> deliverOpt = accountsRepository.findById(deliverId);
        return deliverOpt.map(deliveriesRepository::findByDeliver).orElse(null);
    }
}
