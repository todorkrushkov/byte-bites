package com.example.ByteBites.service.inteface;
import com.example.ByteBites.models.DTO.DelivererRevenueDTO;
import com.example.ByteBites.models.DTO.OrderStatsDTO;
import com.example.ByteBites.models.DTO.RestaurantPeriodRevenueDTO;
import com.example.ByteBites.models.DTO.RestaurantRevenueDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportServiceInterface {
    OrderStatsDTO getOrderStatistics();
    List<RestaurantRevenueDTO> getRevenuePerRestaurant();
     RestaurantPeriodRevenueDTO getRestaurantRevenueForPeriod(Long restaurantId, LocalDateTime start, LocalDateTime end) ;
    List<DelivererRevenueDTO> getDelivererIncomeForPeriod(Long restaurantId, LocalDateTime start, LocalDateTime end);




}

