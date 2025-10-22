package com.example.ByteBites.repository;

import com.example.ByteBites.models.OrderItems;
import com.example.ByteBites.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemsRepository extends JpaRepository<OrderItems, Long> {
    List<OrderItems> findByOrder(Orders order);



    void deleteByOrderId(Long orderId);
}
