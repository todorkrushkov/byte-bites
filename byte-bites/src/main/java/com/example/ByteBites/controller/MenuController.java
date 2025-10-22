package com.example.ByteBites.controller;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.MenuItems;
import com.example.ByteBites.service.MenuService;
import com.example.ByteBites.service.inteface.MenuServiceInterface;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menu")
@CrossOrigin(origins ="http://localhost:3000")
public class MenuController {

    private final MenuServiceInterface menuService;

    public MenuController(MenuServiceInterface menuService) {
        this.menuService = menuService;
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('OWNER') or hasRole('USER')")
    public ResponseEntity<List<MenuItems>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }


    @GetMapping("item/{id}")
    public ResponseEntity<MenuItems> getMenuItemById(@PathVariable Long id) {
        Optional<MenuItems> menuItem = menuService.getMenuItemById(id);
        return menuItem.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuItems>> getMenuItemsByRestaurant(@PathVariable Long restaurantId) {
        return ResponseEntity.ok(menuService.getMenuItemsByRestaurant(restaurantId));
    }


    @PostMapping("/add/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuItems> addMenuItem(@RequestBody MenuItems menuItem, @PathVariable Long restaurantId, @AuthenticationPrincipal Accounts currentUser) {
        return ResponseEntity.ok(menuService.addMenuItem(menuItem, restaurantId, currentUser));
    }


    @PutMapping("/item/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuItems> updateMenuItem(@PathVariable Long id, @RequestBody MenuItems menuItem, @AuthenticationPrincipal Accounts currentUser) {
        return ResponseEntity.ok(menuService.updateMenuItem(id, menuItem, currentUser));
    }


    @DeleteMapping("/item/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id, @AuthenticationPrincipal Accounts currentUser) {
        menuService.deleteMenuItem(id,currentUser);
        return ResponseEntity.ok("Ястието беше изтрито успешно!");
    }
}

