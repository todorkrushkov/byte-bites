package com.example.ByteBites.service;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.DTO.RestaurantRequestDTO;
import com.example.ByteBites.models.Restaurants;
import com.example.ByteBites.repository.MenuItemsRepository;
import com.example.ByteBites.repository.OrderItemsRepository;
import com.example.ByteBites.repository.OrdersRepository;
import com.example.ByteBites.repository.RestaurantsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantsRepository restaurantsRepository;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private OrderItemsRepository orderItemsRepository;

    @Mock
    private MenuItemsRepository menuItemsRepository;

    @InjectMocks
    private RestaurantService restaurantService;

    private Accounts owner;
    private RestaurantRequestDTO dto;

    @BeforeEach
    void setUp() {
        owner = new Accounts();
        owner.setId(42L);

        dto = new RestaurantRequestDTO();
        dto.setName("Name");
        dto.setDescription("Desc");
        dto.setAddress("Addr");
        dto.setImageUrl("http://img");
    }

    @Test
    void createRestaurant_savesAndReturns() {
        Restaurants toSave = new Restaurants();

        Restaurants saved = new Restaurants();
        saved.setId(100L);
        when(restaurantsRepository.save(any())).thenReturn(saved);

        Restaurants result = restaurantService.createRestaurant(dto, owner);

        ArgumentCaptor<Restaurants> captor = ArgumentCaptor.forClass(Restaurants.class);
        verify(restaurantsRepository).save(captor.capture());
        Restaurants passed = captor.getValue();
        assertThat(passed.getName()).isEqualTo("Name");
        assertThat(passed.getDescription()).isEqualTo("Desc");
        assertThat(passed.getAddress()).isEqualTo("Addr");
        assertThat(passed.getImageUrl()).isEqualTo("http://img");
        assertThat(passed.getOwner()).isEqualTo(owner);

        assertThat(result.getId()).isEqualTo(100L);
    }

    @Test
    void getAllRestaurants_returnsListFromRepo() {
        Restaurants r1 = new Restaurants(), r2 = new Restaurants();
        when(restaurantsRepository.findAllActiveRestaurants()).thenReturn(List.of(r1, r2));

        List<Restaurants> list = restaurantService.getAllRestaurants();

        assertThat(list).containsExactly(r1, r2);
    }

    @Test
    void getRestaurantById_found() {
        Restaurants r = new Restaurants();
        when(restaurantsRepository.findById(7L)).thenReturn(Optional.of(r));

        Optional<Restaurants> opt = restaurantService.getRestaurantById(7L);

        assertThat(opt).contains(r);
    }

    @Test
    void updateRestaurant_success() {
        Restaurants existing = new Restaurants();
        existing.setOwner(owner);
        existing.setName("Old");
        when(restaurantsRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(restaurantsRepository.save(existing)).thenReturn(existing);

        Restaurants updated = restaurantService.updateRestaurant(5L, dto, owner);

        assertThat(updated.getName()).isEqualTo("Name");
        verify(restaurantsRepository).save(existing);
    }

    @Test
    void updateRestaurant_notFound_throws() {
        when(restaurantsRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                restaurantService.updateRestaurant(5L, dto, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Ресторантът не е намерен");
    }

    @Test
    void updateRestaurant_wrongOwner_throws() {
        Restaurants existing = new Restaurants();
        Accounts other = new Accounts(); other.setId(99L);
        existing.setOwner(other);
        when(restaurantsRepository.findById(5L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                restaurantService.updateRestaurant(5L, dto, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Нямате права");
    }

    @Test
    void deleteRestaurant_successMarksDeleted() {
        Restaurants existing = new Restaurants();
        existing.setOwner(owner);
        when(restaurantsRepository.findById(3L)).thenReturn(Optional.of(existing));

        restaurantService.deleteRestaurant(3L, owner);

        assertThat(existing.isDeleted()).isTrue();
        verify(restaurantsRepository).save(existing);
    }

    @Test
    void deleteRestaurant_noSuch_throws() {
        when(restaurantsRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                restaurantService.deleteRestaurant(3L, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("не е намерен");
    }

    @Test
    void deleteRestaurant_wrongOwner_throws() {
        Restaurants existing = new Restaurants();
        Accounts other = new Accounts(); other.setId(7L);
        existing.setOwner(other);
        when(restaurantsRepository.findById(3L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                restaurantService.deleteRestaurant(3L, owner))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Нямате право");
    }

    @Test
    void filterRestaurantsByCategories_returnsFromRepo() {
        Restaurants a = new Restaurants(), b = new Restaurants();
        List<String> cats = List.of("PIZZA","PASTA");
        when(restaurantsRepository.findDistinctByMenuItemsCategoryIn(cats))
                .thenReturn(List.of(a,b));

        List<Restaurants> out = restaurantService.filterRestaurantsByCategories(cats);

        assertThat(out).containsExactly(a,b);
    }

    @Test
    void getRestaurantsByOwnerId_returnsFromRepo() {
        Restaurants a = new Restaurants(), b = new Restaurants();
        when(restaurantsRepository.findByOwnerId(42L))
                .thenReturn(List.of(a,b));

        List<Restaurants> out = restaurantService.getRestaurantsByOwnerId(42L);

        assertThat(out).containsExactly(a,b);
    }
}
