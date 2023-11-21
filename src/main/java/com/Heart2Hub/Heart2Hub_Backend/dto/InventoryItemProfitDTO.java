package com.Heart2Hub.Heart2Hub_Backend.dto;

import java.math.BigDecimal;

public class InventoryItemProfitDTO {
    private String itemName;
    private BigDecimal totalProfit;

    public InventoryItemProfitDTO(String itemName, BigDecimal totalProfit) {
        this.itemName = itemName;
        this.totalProfit = totalProfit;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }
}
