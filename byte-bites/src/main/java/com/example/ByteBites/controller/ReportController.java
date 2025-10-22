package com.example.ByteBites.controller;

import com.example.ByteBites.models.DTO.DelivererRevenueDTO;
import com.example.ByteBites.models.DTO.RestaurantPeriodRevenueDTO;
import com.example.ByteBites.models.DTO.RestaurantRevenueDTO;
import com.example.ByteBites.models.DTO.OrderStatsDTO;
import com.example.ByteBites.service.inteface.ReportServiceInterface;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportServiceInterface reportService;

    public ReportController(ReportServiceInterface reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/order-stats")
    public ResponseEntity<OrderStatsDTO> getOrderStatistics() {
        OrderStatsDTO stats = reportService.getOrderStatistics();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/restaurant-revenue")
    public ResponseEntity<?> getRestaurantRevenue(
            @RequestParam Long restaurantId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        if (start == null || end == null) {
            List<RestaurantRevenueDTO> allRevenue = reportService.getRevenuePerRestaurant();
            return ResponseEntity.ok(allRevenue);
        } else {
            RestaurantPeriodRevenueDTO periodRevenue =
                    reportService.getRestaurantRevenueForPeriod(restaurantId, start, end);
            return ResponseEntity.ok(periodRevenue);
        }
    }

    @GetMapping("/deliverer-revenue")
    public ResponseEntity<List<DelivererRevenueDTO>> getDelivererRevenues(
            @RequestParam Long restaurantId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<DelivererRevenueDTO> incomeList =
                reportService.getDelivererIncomeForPeriod(restaurantId, start, end);
        return ResponseEntity.ok(incomeList);
    }
}
