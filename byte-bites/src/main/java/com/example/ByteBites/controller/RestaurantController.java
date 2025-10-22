package com.example.ByteBites.controller;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.DTO.RestaurantRequestDTO;
import com.example.ByteBites.models.Restaurants;
import com.example.ByteBites.service.RestaurantService;
import com.example.ByteBites.service.inteface.RestaurantServiceInterface;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/restaurants")
@CrossOrigin(origins = "http://localhost:3000")

public class RestaurantController {

    private final RestaurantServiceInterface restaurantService;

    public RestaurantController(RestaurantServiceInterface restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Restaurants> createRestaurant(@RequestBody RestaurantRequestDTO dto, @AuthenticationPrincipal Accounts currentUser) {
        return ResponseEntity.ok(restaurantService.createRestaurant(dto, currentUser));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Restaurants>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurants> getRestaurantById(@PathVariable Long id) {
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/filter")
    public ResponseEntity<List<Restaurants>> filterRestaurants(@RequestBody List<String> categories) {
        return ResponseEntity.ok(restaurantService.filterRestaurantsByCategories(categories));
    }

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> updateRestaurant(
            @PathVariable Long id,
            @RequestBody RestaurantRequestDTO dto,
            @AuthenticationPrincipal Accounts currentUser) {
        try {
            Restaurants updated = restaurantService.updateRestaurant(id, dto, currentUser);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<?> deleteRestaurant(
            @PathVariable Long id,
            @AuthenticationPrincipal Accounts currentUser) {
        try {
            restaurantService.deleteRestaurant(id, currentUser);
            return ResponseEntity.ok("Ресторантът беше успешно изтрит!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Restaurants>> getRestaurantsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByOwnerId(ownerId));
    }
}

