package com.example.ByteBites.controller;

import com.example.ByteBites.models.*;
import com.example.ByteBites.models.DTO.RestaurantRequestDTO;
import com.example.ByteBites.service.inteface.RestaurantServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantControllerTest {

    @Mock
    private RestaurantServiceInterface restaurantService;

    @InjectMocks
    private RestaurantController restaurantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRestaurant() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO();
        Accounts user = new Accounts();
        Restaurants restaurant = new Restaurants();

        when(restaurantService.createRestaurant(dto, user)).thenReturn(restaurant);

        ResponseEntity<Restaurants> response = restaurantController.createRestaurant(dto, user);
        assertEquals(restaurant, response.getBody());
    }

    @Test
    void testGetAllRestaurants() {
        List<Restaurants> restaurants = Arrays.asList(new Restaurants(), new Restaurants());
        when(restaurantService.getAllRestaurants()).thenReturn(restaurants);

        ResponseEntity<List<Restaurants>> response = restaurantController.getAllRestaurants();
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetRestaurantById_Found() {
        Restaurants restaurant = new Restaurants();
        when(restaurantService.getRestaurantById(1L)).thenReturn(Optional.of(restaurant));

        ResponseEntity<Restaurants> response = restaurantController.getRestaurantById(1L);
        assertEquals(restaurant, response.getBody());
    }

    @Test
    void testGetRestaurantById_NotFound() {
        when(restaurantService.getRestaurantById(999L)).thenReturn(Optional.empty());

        ResponseEntity<Restaurants> response = restaurantController.getRestaurantById(999L);
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testFilterRestaurants() {
        List<String> categories = List.of("PIZZA", "SUSHI");
        List<Restaurants> filtered = List.of(new Restaurants());
        when(restaurantService.filterRestaurantsByCategories(categories)).thenReturn(filtered);

        ResponseEntity<List<Restaurants>> response = restaurantController.filterRestaurants(categories);
        assertEquals(filtered, response.getBody());
    }

    @Test
    void testUpdateRestaurant_Success() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO();
        Restaurants updated = new Restaurants();
        Accounts user = new Accounts();

        when(restaurantService.updateRestaurant(5L, dto, user)).thenReturn(updated);

        ResponseEntity<?> response = restaurantController.updateRestaurant(5L, dto, user);
        assertEquals(updated, response.getBody());
    }

    @Test
    void testUpdateRestaurant_Failure() {
        RestaurantRequestDTO dto = new RestaurantRequestDTO();
        Accounts user = new Accounts();

        when(restaurantService.updateRestaurant(5L, dto, user))
                .thenThrow(new RuntimeException("Not your restaurant"));

        ResponseEntity<?> response = restaurantController.updateRestaurant(5L, dto, user);
        assertEquals(403, response.getStatusCode().value());
        assertEquals("Not your restaurant", response.getBody());
    }

    @Test
    void testDeleteRestaurant_Success() {
        Accounts user = new Accounts();
        doNothing().when(restaurantService).deleteRestaurant(4L, user);

        ResponseEntity<?> response = restaurantController.deleteRestaurant(4L, user);
        assertEquals("Ресторантът беше успешно изтрит!", response.getBody());
    }

    @Test
    void testDeleteRestaurant_Failure() {
        Accounts user = new Accounts();
        doThrow(new RuntimeException("Not allowed")).when(restaurantService).deleteRestaurant(4L, user);

        ResponseEntity<?> response = restaurantController.deleteRestaurant(4L, user);
        assertEquals(403, response.getStatusCode().value());
        assertEquals("Not allowed", response.getBody());
    }

    @Test
    void testGetRestaurantsByOwnerId() {
        List<Restaurants> restaurants = List.of(new Restaurants(), new Restaurants());
        when(restaurantService.getRestaurantsByOwnerId(99L)).thenReturn(restaurants);

        ResponseEntity<List<Restaurants>> response = restaurantController.getRestaurantsByOwnerId(99L);
        assertEquals(2, response.getBody().size());
    }
}

