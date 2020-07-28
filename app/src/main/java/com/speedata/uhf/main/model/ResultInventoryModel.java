package com.speedata.uhf.main.model;

import com.google.gson.annotations.Expose;

public class ResultInventoryModel {
    @Expose
    private String inventory_date;

    @Expose
    private String inventory_department;

    @Expose
    private int find;

    @Expose
    private int total;

    public int getFind() {
        return find;
    }

    public int getTotal() {
        return total;
    }

    public String getInventory_date() {
        return inventory_date;
    }

    public String getInventory_department() {
        return inventory_department;
    }
}
