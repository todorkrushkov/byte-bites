package com.example.ByteBites.repository;

import com.example.ByteBites.models.MenuItems;
import com.example.ByteBites.models.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuItemsRepository extends JpaRepository<MenuItems, Long> {
    List<MenuItems> findByRestaurants(Restaurants restaurants);

    void deleteByRestaurantsId(Long restaurantId);

    @Query("SELECT m FROM MenuItems m WHERE m.restaurants.id = :restaurantId AND m.isDeleted = false")
    List<MenuItems> findByRestaurantIdAndNotDeleted(@Param("restaurantId") Long restaurantId);

}
