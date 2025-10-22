package com.example.ByteBites.service.inteface;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.DTO.RestaurantRequestDTO;
import com.example.ByteBites.models.Restaurants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface RestaurantServiceInterface {
    Restaurants createRestaurant(RestaurantRequestDTO dto, Accounts currentUser);

    List<Restaurants> getAllRestaurants();

    Optional<Restaurants> getRestaurantById(Long id);

    Restaurants updateRestaurant(Long id, RestaurantRequestDTO dto, Accounts currentUser);

    void deleteRestaurant(Long id,Accounts currentUser);

    List<Restaurants> filterRestaurantsByCategories(List<String> categories);

    List<Restaurants> getRestaurantsByOwnerId(Long ownerId);
}
