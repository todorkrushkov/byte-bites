package com.example.ByteBites.service;

import com.example.ByteBites.models.Category;
import com.example.ByteBites.models.MenuItems;
import com.example.ByteBites.models.Restaurants;
import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.repository.MenuItemsRepository;
import com.example.ByteBites.repository.RestaurantsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.server.ResponseStatusException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {

    @Mock
    private MenuItemsRepository menuItemsRepository;

    @Mock
    private RestaurantsRepository restaurantsRepository;

    @InjectMocks
    private MenuService menuService;

    private Accounts owner;
    private Accounts otherUser;
    private Restaurants restaurant;
    private MenuItems item;

    @BeforeEach
    void setUp() {
        owner = new Accounts();
        owner.setId(1L);
        otherUser = new Accounts();
        otherUser.setId(2L);
        restaurant = new Restaurants();
        restaurant.setId(10L);
        restaurant.setOwner(owner);
        item = new MenuItems();
        item.setId(100L);
        item.setName("Pizza");
    }

    @Test
    void getAllMenuItems_returnsAll() {
        List<MenuItems> list = List.of(item);
        when(menuItemsRepository.findAll()).thenReturn(list);

        List<MenuItems> result = menuService.getAllMenuItems();

        assertThat(result).hasSize(1).contains(item);
        verify(menuItemsRepository).findAll();
    }

    @Test
    void getMenuItemById_found() {
        when(menuItemsRepository.findById(100L)).thenReturn(Optional.of(item));

        Optional<MenuItems> fetched = menuService.getMenuItemById(100L);

        assertThat(fetched).contains(item);
    }

    @Test
    void getMenuItemById_notFound() {
        when(menuItemsRepository.findById(any())).thenReturn(Optional.empty());

        Optional<MenuItems> fetched = menuService.getMenuItemById(999L);

        assertThat(fetched).isEmpty();
    }

    @Test
    void getMenuItemsByRestaurant_success() {
        when(restaurantsRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.findByRestaurantIdAndNotDeleted(10L)).thenReturn(List.of(item));

        List<MenuItems> list = menuService.getMenuItemsByRestaurant(10L);

        assertThat(list).contains(item);
    }

    @Test
    void getMenuItemsByRestaurant_restaurantNotFound() {
        when(restaurantsRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.getMenuItemsByRestaurant(123L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ресторантът не е намерен");
    }

    @Test
    void addMenuItem_success() {
        when(restaurantsRepository.findById(10L)).thenReturn(Optional.of(restaurant));
        when(menuItemsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MenuItems toAdd = new MenuItems();
        toAdd.setName("Pasta");
        MenuItems saved = menuService.addMenuItem(toAdd, 10L, owner);

        assertThat(saved.getRestaurants()).isEqualTo(restaurant);
        verify(menuItemsRepository).save(toAdd);
    }

    @Test
    void addMenuItem_restaurantNotFound() {
        when(restaurantsRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.addMenuItem(item, 10L, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ресторантът не съществува");
    }

    @Test
    void addMenuItem_notOwner_forbidden() {
        when(restaurantsRepository.findById(10L)).thenReturn(Optional.of(restaurant));

        assertThatThrownBy(() -> menuService.addMenuItem(item, 10L, otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .matches(ex -> ((ResponseStatusException)ex).getStatusCode().value() == 403);
    }

    @Test
    void updateMenuItem_success() {
        MenuItems existing = new MenuItems();
        existing.setId(100L);
        existing.setRestaurants(restaurant);

        when(menuItemsRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(menuItemsRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MenuItems updated = new MenuItems();
        updated.setName("NewName");
        updated.setPrice(9.99);
        updated.setCategory(Category.PIZZA);
        updated.setFoodImage("url");

        MenuItems result = menuService.updateMenuItem(100L, updated, owner);

        assertThat(result.getName()).isEqualTo("NewName");
        assertThat(result.getPrice()).isEqualTo(9.99);
        verify(menuItemsRepository).save(existing);
    }

    @Test
    void updateMenuItem_notFound() {
        when(menuItemsRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.updateMenuItem(42L, item, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ястието не е намерено");
    }

    @Test
    void updateMenuItem_notOwner_forbidden() {
        MenuItems existing = new MenuItems();
        existing.setId(100L);
        existing.setRestaurants(restaurant);

        when(menuItemsRepository.findById(100L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> menuService.updateMenuItem(100L, item, otherUser))
                .isInstanceOf(ResponseStatusException.class)
                .matches(ex -> ((ResponseStatusException)ex).getStatusCode().value() == 403);
    }

    @Test
    void deleteMenuItem_success_marksDeleted() {
        MenuItems existing = new MenuItems();
        existing.setId(200L);
        existing.setRestaurants(restaurant);
        existing.setDeleted(false);

        when(menuItemsRepository.findById(200L)).thenReturn(Optional.of(existing));

        menuService.deleteMenuItem(200L, owner);

        assertThat(existing.isDeleted()).isTrue();
        verify(menuItemsRepository).save(existing);
    }

    @Test
    void deleteMenuItem_notFound() {
        when(menuItemsRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> menuService.deleteMenuItem(1L, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Продуктът не е намерен");
    }

    @Test
    void deleteMenuItem_notOwner_forbidden() {
        MenuItems existing = new MenuItems();
        existing.setId(200L);
        existing.setRestaurants(restaurant);

        when(menuItemsRepository.findById(200L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> menuService.deleteMenuItem(200L, otherUser))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Нямате право");
    }
}
