package com.example.ByteBites.service;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.DTO.DelivererRevenueDTO;
import com.example.ByteBites.models.DTO.RestaurantPeriodRevenueDTO;
import com.example.ByteBites.models.DTO.RestaurantRevenueDTO;
import com.example.ByteBites.models.DTO.OrderStatsDTO;
import com.example.ByteBites.models.Orders;
import com.example.ByteBites.models.Restaurants;
import com.example.ByteBites.repository.AccountRepository;
import com.example.ByteBites.repository.OrdersRepository;
import com.example.ByteBites.repository.RestaurantsRepository;
import com.example.ByteBites.service.inteface.ReportServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportService implements ReportServiceInterface {

    private final OrdersRepository orderRepository;
    private final AccountRepository accountRepository;
    private final RestaurantsRepository restaurantRepository;

    @Value("${bonus.threshold}")
    private BigDecimal bonusThreshold;

    @Value("${bonus.multiplier}")
    private BigDecimal bonusMultiplier;

    public ReportService(
            OrdersRepository orderRepository,
            AccountRepository accountRepository,
            RestaurantsRepository restaurantRepository
    ) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public OrderStatsDTO getOrderStatistics() {
        List<Orders> allOrders = orderRepository.findAll();
        int totalOrders = allOrders.size();
        double averageValue = totalOrders == 0
                ? 0
                : allOrders.stream()
                .mapToDouble(Orders::getTotalPrice)
                .average()
                .orElse(0);
        return new OrderStatsDTO(totalOrders, averageValue);
    }

    @Override
    public List<RestaurantRevenueDTO> getRevenuePerRestaurant() {
        return orderRepository.getRevenuePerRestaurant();
    }

    @Override
    public RestaurantPeriodRevenueDTO getRestaurantRevenueForPeriod(
            Long restaurantId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        // Взимаме текущия логнат потребител
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Accounts account = accountRepository.findByUsernameIgnoreCase(username)
                .or(() -> accountRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (!restaurant.getOwner().getId().equals(account.getId())) {
            throw new AccessDeniedException("You do not own this restaurant.");
        }

        List<Orders> orders = orderRepository
                .findOrdersByRestaurantIdAndCreatedAtBetween(restaurantId, start, end);

        BigDecimal total = orders.stream()
                .map(o -> BigDecimal.valueOf(o.getTotalPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new RestaurantPeriodRevenueDTO(
                restaurant.getId(),
                restaurant.getName(),
                total
        );
    }

    @Override
    public List<DelivererRevenueDTO> getDelivererIncomeForPeriod(
            Long restaurantId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        Accounts account = accountRepository.findByUsernameIgnoreCase(username)
                .or(() -> accountRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Restaurants restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (!restaurant.getOwner().getId().equals(account.getId())) {
            throw new AccessDeniedException("You do not own this restaurant.");
        }

        List<DelivererRevenueDTO> revenues = orderRepository
                .getDelivererRevenueForRestaurantAndPeriod(restaurantId, start, end);

        // Приложим бонусната логика
        for (DelivererRevenueDTO dto : revenues) {
            if (dto.getTotalIncome().compareTo(bonusThreshold) >= 0) {
                BigDecimal bonus = dto.getTotalIncome().multiply(bonusMultiplier);
                dto.setTotalIncome(bonus);
                dto.setBonusAwarded(true);
            }
        }

        return revenues;
    }
}
