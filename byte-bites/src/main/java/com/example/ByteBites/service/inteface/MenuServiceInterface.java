package com.example.ByteBites.service.inteface;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.MenuItems;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



public interface MenuServiceInterface {
    List<MenuItems> getAllMenuItems();

    Optional<MenuItems> getMenuItemById(Long id);

    List<MenuItems> getMenuItemsByRestaurant(Long restaurantId);

    MenuItems addMenuItem(MenuItems menuItem, Long restaurantId, Accounts currentUser);

    MenuItems updateMenuItem(Long id, MenuItems updatedItem, Accounts currentUser);

    void deleteMenuItem(Long id, Accounts currentUser);
}
