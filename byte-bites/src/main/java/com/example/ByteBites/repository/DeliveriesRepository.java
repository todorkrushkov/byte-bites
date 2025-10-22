package com.example.ByteBites.repository;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.Deliveries;
import com.example.ByteBites.models.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveriesRepository extends JpaRepository<Deliveries, Long> {
    List<Deliveries> findByDeliver(Accounts deliver);
    Optional<Deliveries> findByOrder(Orders order);
}
