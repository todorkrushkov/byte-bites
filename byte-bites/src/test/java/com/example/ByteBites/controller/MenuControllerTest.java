package com.example.ByteBites.controller;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.MenuItems;
import com.example.ByteBites.service.inteface.MenuServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MenuControllerTest {

    @Mock
    private MenuServiceInterface menuService;

    @InjectMocks
    private MenuController menuController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllMenuItems() {
        List<MenuItems> mockList = Arrays.asList(new MenuItems(), new MenuItems());
        when(menuService.getAllMenuItems()).thenReturn(mockList);

        ResponseEntity<List<MenuItems>> response = menuController.getAllMenuItems();

        assertEquals(2, response.getBody().size());
        verify(menuService, times(1)).getAllMenuItems();
    }

    @Test
    void testGetMenuItemById_Found() {
        MenuItems item = new MenuItems();
        when(menuService.getMenuItemById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<MenuItems> response = menuController.getMenuItemById(1L);

        assertEquals(item, response.getBody());
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void testGetMenuItemById_NotFound() {
        when(menuService.getMenuItemById(1L)).thenReturn(Optional.empty());

        ResponseEntity<MenuItems> response = menuController.getMenuItemById(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testGetMenuItemsByRestaurant() {
        List<MenuItems> mockList = Arrays.asList(new MenuItems(), new MenuItems());
        when(menuService.getMenuItemsByRestaurant(5L)).thenReturn(mockList);

        ResponseEntity<List<MenuItems>> response = menuController.getMenuItemsByRestaurant(5L);

        assertEquals(2, response.getBody().size());
        verify(menuService).getMenuItemsByRestaurant(5L);
    }

    @Test
    void testAddMenuItem() {
        MenuItems item = new MenuItems();
        Accounts user = new Accounts();
        when(menuService.addMenuItem(item, 10L, user)).thenReturn(item);

        ResponseEntity<MenuItems> response = menuController.addMenuItem(item, 10L, user);

        assertEquals(item, response.getBody());
        verify(menuService).addMenuItem(item, 10L, user);
    }

    @Test
    void testUpdateMenuItem() {
        MenuItems item = new MenuItems();
        Accounts user = new Accounts();
        when(menuService.updateMenuItem(3L, item, user)).thenReturn(item);

        ResponseEntity<MenuItems> response = menuController.updateMenuItem(3L, item, user);

        assertEquals(item, response.getBody());
        verify(menuService).updateMenuItem(3L, item, user);
    }

    @Test
    void testDeleteMenuItem() {
        Accounts user = new Accounts();

        ResponseEntity<String> response = menuController.deleteMenuItem(4L, user);

        assertEquals("Ястието беше изтрито успешно!", response.getBody());
        verify(menuService).deleteMenuItem(4L, user);
    }
}
