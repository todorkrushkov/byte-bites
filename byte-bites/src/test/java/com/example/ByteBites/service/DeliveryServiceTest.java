package com.example.ByteBites.service;

import com.example.ByteBites.models.*;
import com.example.ByteBites.repository.AccountRepository;
import com.example.ByteBites.repository.DeliveriesRepository;
import com.example.ByteBites.repository.OrdersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock OrdersRepository ordersRepo;
    @Mock DeliveriesRepository deliveriesRepo;
    @Mock AccountRepository accountsRepo;

    @InjectMocks DeliveryService service;

    Accounts owner;
    Accounts deliverer;
    Restaurants restaurant;
    Orders orderPending;
    Orders orderConfirmed;

    @BeforeEach
    void setUp() {
        owner = new Accounts();
        owner.setId(10L);
        deliverer = new Accounts();
        deliverer.setId(20L);

        restaurant = new Restaurants();
        restaurant.setOwner(owner);

        orderPending = new Orders();
        orderPending.setId(1L);
        orderPending.setStatus(OrderStatus.PENDING);
        orderPending.setRestaurant(restaurant);

        orderConfirmed = new Orders();
        orderConfirmed.setId(2L);
        orderConfirmed.setStatus(OrderStatus.CONFIRMED);
        orderConfirmed.setRestaurant(restaurant);
    }

    @Test
    void getAvailableDeliveries_filtersByOwnerId_andPending() {
        // поръчка към друг собственик
        Restaurants otherRest = new Restaurants();
        Accounts otherOwner = new Accounts(); otherOwner.setId(999L);
        otherRest.setOwner(otherOwner);
        Orders otherOrder = new Orders();
        otherOrder.setId(5L);
        otherOrder.setStatus(OrderStatus.PENDING);
        otherOrder.setRestaurant(otherRest);

        when(ordersRepo.findByStatus(OrderStatus.PENDING))
                .thenReturn(List.of(orderPending, otherOrder));

        List<Orders> result = service.getAvailableDeliveries(owner.getId());
        assertThat(result).containsExactly(orderPending);
    }

    @Test
    void getReadyForPickupOrders_excludesAlreadyAssigned() {
        Orders ready1 = new Orders();
        ready1.setId(101L);
        ready1.setStatus(OrderStatus.READY_FOR_PICKUP);
        Orders ready2 = new Orders();
        ready2.setId(102L);
        ready2.setStatus(OrderStatus.READY_FOR_PICKUP);

        when(ordersRepo.findByStatus(OrderStatus.READY_FOR_PICKUP))
                .thenReturn(List.of(ready1, ready2));
        when(deliveriesRepo.findByOrder(ready1))
                .thenReturn(Optional.of(new Deliveries()));
        when(deliveriesRepo.findByOrder(ready2))
                .thenReturn(Optional.empty());

        List<Orders> result = service.getReadyForPickupOrders(deliverer.getId());
        assertThat(result).containsExactly(ready2);
    }

    @Test
    void acceptOrderByOwner_success() {
        when(ordersRepo.findById(1L)).thenReturn(Optional.of(orderPending));

        String msg = service.acceptOrderByOwner(1L);
        assertEquals("Поръчката е успешно приета и готова за взимане!", msg);
        assertEquals(OrderStatus.CONFIRMED, orderPending.getStatus());
        verify(ordersRepo).save(orderPending);
    }

    @Test
    void acceptOrderByOwner_notPending() {
        orderPending.setStatus(OrderStatus.CONFIRMED);
        when(ordersRepo.findById(1L)).thenReturn(Optional.of(orderPending));

        String msg = service.acceptOrderByOwner(1L);
        assertTrue(msg.contains("не е в статус PENDING"));
        verify(ordersRepo, never()).save(any());
    }

    @Test
    void acceptOrderByOwner_notFound() {
        when(ordersRepo.findById(999L)).thenReturn(Optional.empty());
        assertEquals("Поръчката не съществува!", service.acceptOrderByOwner(999L));
    }

    @Test
    void markOrderAsReady_success() {
        orderConfirmed.setStatus(OrderStatus.CONFIRMED);
        when(ordersRepo.findById(2L)).thenReturn(Optional.of(orderConfirmed));

        String msg = service.markOrderAsReady(2L);
        assertEquals("Поръчката е маркирана като готова за взимане!", msg);
        assertEquals(OrderStatus.READY_FOR_PICKUP, orderConfirmed.getStatus());
        verify(ordersRepo).save(orderConfirmed);
    }

    @Test
    void markOrderAsReady_wrongStatus() {
        orderConfirmed.setStatus(OrderStatus.PENDING);
        when(ordersRepo.findById(2L)).thenReturn(Optional.of(orderConfirmed));

        String msg = service.markOrderAsReady(2L);
        assertTrue(msg.contains("не може да бъде маркирана като готова"));
        verify(ordersRepo, never()).save(any());
    }

    @Test
    void acceptDelivery_success() {
        Orders o = new Orders();
        o.setId(77L);
        o.setStatus(OrderStatus.READY_FOR_PICKUP);
        when(ordersRepo.findById(77L)).thenReturn(Optional.of(o));
        when(deliveriesRepo.findByOrder(o)).thenReturn(Optional.empty());

        String msg = service.acceptDelivery(77L, deliverer);
        assertEquals("Успешно приехте поръчката за доставка!", msg);
        assertEquals(OrderStatus.ON_THE_WAY, o.getStatus());
        verify(deliveriesRepo).save(Mockito.argThat(d ->
                d.getDeliver().equals(deliverer) && d.getOrder().equals(o)
        ));
        verify(ordersRepo).save(o);
    }

    @Test
    void acceptDelivery_notReadyOrAssigned() {
        Orders o = new Orders();
        o.setId(11L);
        o.setStatus(OrderStatus.PENDING);
        when(ordersRepo.findById(11L)).thenReturn(Optional.of(o));

        assertTrue(service.acceptDelivery(11L, deliverer)
                .contains("не е готова за доставка"));
    }

    @Test
    void acceptDelivery_alreadyAssigned() {
        Orders o = new Orders();
        o.setId(22L);
        o.setStatus(OrderStatus.READY_FOR_PICKUP);
        when(ordersRepo.findById(22L)).thenReturn(Optional.of(o));
        when(deliveriesRepo.findByOrder(o)).thenReturn(Optional.of(new Deliveries()));

        assertTrue(service.acceptDelivery(22L, deliverer)
                .contains("вече има назначен доставчик"));
    }

    @Test
    void updateDeliveryStatus_progressAndComplete() {
        Deliveries d = new Deliveries();
        d.setId(5L);
        Orders o = new Orders();
        o.setId(55L);
        d.setOrder(o);
        when(deliveriesRepo.findById(5L)).thenReturn(Optional.of(d));

        String msg1 = service.updateDeliveryStatus(5L, DeliveryStatus.IN_PROGRESS);
        assertEquals("Статусът на доставката е променен!", msg1);
        assertEquals(OrderStatus.ON_THE_WAY, o.getStatus());

        String msg2 = service.updateDeliveryStatus(5L, DeliveryStatus.COMPLETED);
        assertEquals("Статусът на доставката е променен!", msg2);
        assertEquals(OrderStatus.DELIVERED, o.getStatus());
    }

    @Test
    void updateDeliveryStatus_notFoundOrInvalid() {
        when(deliveriesRepo.findById(99L)).thenReturn(Optional.empty());
        assertEquals("Доставката не е намерена!", service.updateDeliveryStatus(99L, DeliveryStatus.ASSIGNED));

        Deliveries d = new Deliveries();
        when(deliveriesRepo.findById(10L)).thenReturn(Optional.of(d));
        assertTrue(service.updateDeliveryStatus(10L, DeliveryStatus.ASSIGNED)
                .contains("Невалиден статус"));
    }

    @Test
    void getDeliveriesByDeliver_existing() {
        Deliveries d1 = new Deliveries();
        Deliveries d2 = new Deliveries();
        when(accountsRepo.findById(deliverer.getId()))
                .thenReturn(Optional.of(deliverer));
        when(deliveriesRepo.findByDeliver(deliverer))
                .thenReturn(List.of(d1, d2));

        List<Deliveries> list = service.getDeliveriesByDeliver(deliverer.getId());
        assertThat(list).hasSize(2).containsExactly(d1, d2);
    }

    @Test
    void getDeliveriesByDeliver_notFound() {
        when(accountsRepo.findById(123L)).thenReturn(Optional.empty());
        assertNull(service.getDeliveriesByDeliver(123L));
    }
}
