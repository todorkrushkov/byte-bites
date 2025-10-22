package com.example.ByteBites.repository;

import com.example.ByteBites.models.Restaurants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantsRepository extends JpaRepository<Restaurants, Long> {
    @Query("SELECT DISTINCT r FROM Restaurants r JOIN r.menuItems m WHERE m.category IN :categories")
    List<Restaurants> findDistinctByMenuItemsCategoryIn(@Param("categories") List<String> categories);

    @Query("SELECT r FROM Restaurants r WHERE r.isDeleted = false")
    List<Restaurants> findAllActiveRestaurants();

    List<Restaurants> findByOwnerId(Long id);
}
