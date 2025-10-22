package com.example.ByteBites.service;

import com.example.ByteBites.models.*;
import com.example.ByteBites.models.DTO.OrderItemDTO;
import com.example.ByteBites.models.DTO.OrderRequestDTO;
import com.example.ByteBites.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import jakarta.transaction.Transactional;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrdersRepository ordersRepository;
    @Mock
    AccountRepository accountsRepository;
    @Mock
    RestaurantsRepository restaurantsRepository;
    @Mock
    MenuItemsRepository menuItemsRepository;
    @Mock
    OrderItemsRepository orderItemsRepository;

    @InjectMocks OrderService orderService;

    Accounts customer;
    Restaurants restaurant;
    MenuItems menuItem;

    @BeforeEach
    void setUp() {
        customer = new Accounts(); customer.setId(1L);
        restaurant = new Restaurants(); restaurant.setId(10L);
        menuItem = new MenuItems(); menuItem.setId(100L); menuItem.setPrice(20.0);
    }

    @Test
    void createOrder_smallTotal_addsDeliveryFeeAndTax() {
        // total = 20*2 = 40 <100 so fee=4.99 + 0.15
        OrderItemDTO dto = new OrderItemDTO(100L, 2);
        OrderRequestDTO req = new OrderRequestDTO("Addr", List.of(dto));

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantsRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.findById(100L)).thenReturn(Optional.of(menuItem));
        // save Order -> assign id
        ArgumentCaptor<Orders> orderCap = ArgumentCaptor.forClass(Orders.class);
        when(ordersRepository.save(orderCap.capture()))
                .thenAnswer(inv -> {
                    Orders o = inv.getArgument(0);
                    o.setId(500L);
                    return o;
                });

        Orders result = orderService.createOrder(1L, 10L, req);

        verify(orderItemsRepository, times(1)).save(any());

        assertThat(result.getTotalPrice()).isCloseTo(40 + 4.99 + 0.15, within(1e-6));
        assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(result.getDeliveryAddress()).isEqualTo("Addr");
        assertThat(orderCap.getValue().getCustomer()).isEqualTo(customer);
    }

    @Test
    void createOrder_largeTotal_onlyTax() {

        menuItem.setPrice(60.0);
        OrderItemDTO dto = new OrderItemDTO(100L, 2);
        OrderRequestDTO req = new OrderRequestDTO("X", List.of(dto));

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantsRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.findById(100L)).thenReturn(Optional.of(menuItem));
        when(ordersRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Orders result = orderService.createOrder(1L, 10L, req);

        assertThat(result.getTotalPrice()).isCloseTo(120 + 0.15, within(1e-6));
    }

    @Test
    void createOrder_customerNotFound_throws() {
        when(accountsRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.createOrder(1L, 10L, new OrderRequestDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Клиентът не е намерен");
    }

    @Test
    void createOrder_restaurantNotFound_throws() {
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantsRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.createOrder(1L, 10L, new OrderRequestDTO()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ресторантът не е намерен");
    }

    @Test
    void createOrder_menuItemNotFound_throws() {

        when(accountsRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(restaurantsRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.findById(anyLong())).thenReturn(Optional.empty());

        OrderItemDTO dto = new OrderItemDTO(999L, 1);
        OrderRequestDTO req = new OrderRequestDTO("Some Address", List.of(dto));

        assertThatThrownBy(() -> orderService.createOrder(1L, 10L, req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Меню артикулът не е намерен");
    }

    @Test
    void getAllOrders_returnsAll() {
        Orders o1 = new Orders(), o2 = new Orders();
        when(ordersRepository.findAll()).thenReturn(List.of(o1, o2));
        assertThat(orderService.getAllOrders()).containsExactly(o1, o2);
    }

    @Test
    void getOrdersByCustomer_success() {
        when(accountsRepository.findById(1L)).thenReturn(Optional.of(customer));
        Orders o = new Orders();
        when(ordersRepository.findByCustomer(customer)).thenReturn(List.of(o));

        List<Orders> list = orderService.getOrdersByCustomer(1L);
        assertThat(list).containsExactly(o);
    }

    @Test
    void getOrdersByCustomer_notFound_throws() {
        when(accountsRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrdersByCustomer(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Клиентът не е намерен");
    }

    @Test
    void updateOrderStatus_success() {
        Orders o = new Orders(); o.setId(7L); o.setStatus(OrderStatus.PENDING);
        when(ordersRepository.findById(7L)).thenReturn(Optional.of(o));
        when(ordersRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Orders updated = orderService.updateOrderStatus(7L, OrderStatus.CONFIRMED);
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void updateOrderStatus_notFound_throws() {
        when(ordersRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.updateOrderStatus(5L, OrderStatus.DELIVERED))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Поръчката не е намерена");
    }

    @Test
    void deleteOrder_success() {
        when(ordersRepository.existsById(3L)).thenReturn(true);
        orderService.deleteOrder(3L);
        verify(ordersRepository).deleteById(3L);
    }

    @Test
    void deleteOrder_notFound_throws() {
        when(ordersRepository.existsById(any())).thenReturn(false);
        assertThatThrownBy(() -> orderService.deleteOrder(9L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Поръчката не е намерена");
    }

    @Test
    void getOrderItemsByOrder_success() {
        Orders o = new Orders(); o.setId(8L);
        OrderItems oi = new OrderItems();
        when(ordersRepository.findById(8L)).thenReturn(Optional.of(o));
        when(orderItemsRepository.findByOrder(o)).thenReturn(List.of(oi));

        assertThat(orderService.getOrderItemsByOrder(8L)).containsExactly(oi);
    }

    @Test
    void getOrderItemsByOrder_notFound_throws() {
        when(ordersRepository.findById(any())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> orderService.getOrderItemsByOrder(8L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Поръчката не е намерена");
    }

    @Test
    void getOrdersByRestaurant_delegates() {
        Orders o = new Orders();
        when(ordersRepository.findByRestaurantId(42L)).thenReturn(List.of(o));
        assertThat(orderService.getOrdersByRestaurant(42L)).containsExactly(o);
    }
}
