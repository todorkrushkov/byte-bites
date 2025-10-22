package com.example.ByteBites.models.DTO;



public class OrderStatsDTO {
    private int totalOrders;
    private double averageOrderValue;

    public OrderStatsDTO(int totalOrders, double averageOrderValue) {
        this.totalOrders = totalOrders;
        this.averageOrderValue = averageOrderValue;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public double getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
}

