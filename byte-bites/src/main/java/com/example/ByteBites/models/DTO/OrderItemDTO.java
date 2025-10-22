package com.example.ByteBites.models.DTO;

import lombok.Data;


public class OrderItemDTO {

    private Long menuItemId;
    private Integer quantity;

    public Long getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(Long menuItemId) {
        this.menuItemId = menuItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderItemDTO() {}

    // Всички аргументи
    public OrderItemDTO(Long menuItemId, Integer quantity) {
        this.menuItemId = menuItemId;
        this.quantity   = quantity;
    }
}
