package com.example.ByteBites.models.DTO;

import lombok.Data;

import java.util.List;


public class OrderRequestDTO {
    private String deliveryAddress;
    private List<OrderItemDTO> items;

    public OrderRequestDTO() { }

    public OrderRequestDTO(String deliveryAddress, List<OrderItemDTO> items) {
        this.deliveryAddress = deliveryAddress;
        this.items = items;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
