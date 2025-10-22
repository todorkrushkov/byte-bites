package com.example.ByteBites.service;

import com.example.ByteBites.models.*;
import com.example.ByteBites.models.DTO.OrderItemDTO;
import com.example.ByteBites.models.DTO.OrderRequestDTO;
import com.example.ByteBites.repository.*;
import com.example.ByteBites.service.inteface.OrderServiceInterface;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements OrderServiceInterface {

    private final OrdersRepository ordersRepository;
    private final AccountRepository accountsRepository;
    private final RestaurantsRepository restaurantsRepository;
    private final MenuItemsRepository menuItemsRepository;
    private final OrderItemsRepository orderItemsRepository;

    public OrderService(OrdersRepository ordersRepository, AccountRepository accountsRepository, RestaurantsRepository restaurantsRepository, MenuItemsRepository menuItemsRepository, OrderItemsRepository orderItemsRepository) {
        this.ordersRepository = ordersRepository;
        this.accountsRepository = accountsRepository;
        this.restaurantsRepository = restaurantsRepository;
        this.menuItemsRepository = menuItemsRepository;
        this.orderItemsRepository = orderItemsRepository;
    }

    @Transactional
    @Override
    public Orders createOrder(Long customerId, Long restaurantId, OrderRequestDTO request) {
        Accounts customer = accountsRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Клиентът не е намерен!"));

        Restaurants restaurant = restaurantsRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Ресторантът не е намерен!"));

        double totalPrice = 0.0;
        List<OrderItems> orderItemsList = new ArrayList<>();

        for (OrderItemDTO itemDTO : request.getItems()) {
            MenuItems menuItem = menuItemsRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Меню артикулът не е намерен!"));

            OrderItems orderItem = new OrderItems();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDTO.getQuantity());

            orderItemsList.add(orderItem);
            totalPrice += menuItem.getPrice() * itemDTO.getQuantity();
        }

        if (totalPrice >= 100) {
            totalPrice += 0.15;
        } else {
            totalPrice += 0.15 + 4.99;
        }

        Orders order = new Orders();
        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setStatus(OrderStatus.PENDING);
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setTotalPrice(totalPrice);

        Orders savedOrder = ordersRepository.save(order);

        for (OrderItems item : orderItemsList) {
            item.setOrder(savedOrder);
            orderItemsRepository.save(item);
        }

        return savedOrder;
    }
    @Override
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    @Override
    public List<Orders> getOrdersByCustomer(Long customerId) {
        Accounts customer = accountsRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Клиентът не е намерен!"));

        return ordersRepository.findByCustomer(customer);
    }

    @Override
    public Orders updateOrderStatus(Long orderId, OrderStatus status) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Поръчката не е намерена!"));

        order.setStatus(status);
        return ordersRepository.save(order);
    }

    @Override
    public void deleteOrder(Long orderId) {
        if (!ordersRepository.existsById(orderId)) {
            throw new RuntimeException("Поръчката не е намерена!");
        }
        ordersRepository.deleteById(orderId);
    }
    @Override
    public List<OrderItems> getOrderItemsByOrder(Long orderId) {
        Orders order = ordersRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Поръчката не е намерена!"));

        return orderItemsRepository.findByOrder(order);
    }

    @Override
    public List<Orders> getOrdersByRestaurant(Long restaurantId) {
        return ordersRepository.findByRestaurantId(restaurantId);
    }
}

