package com.speedata.uhf.main.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class MachineryModel implements Serializable {

    @Expose
    private Integer ordinal_numbers;

    @Expose
    private String inventory_date;

    @Expose
    private String department_code;

    @Expose
    private String asset_code;

    @Expose
    private String department_asset_code;

    @Expose
    private String department_asset_name;

    @Expose
    private String RFID_code;

    @Expose
    private Integer status;

    @Expose
    private Date date_acceptance;

    @Expose
    private Date date_depreciation;

    @Expose
    private int time_used;

    @Expose
    private double original_price;

    @Expose
    private double depreciated_price;

    @Expose
    private String location;

    @Expose
    private String group_code;

    @Expose
    private String inventory_department;

    public void setOrdinal_numbers(Integer ordinal_numbers) {
        this.ordinal_numbers = ordinal_numbers;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOrdinal_numbers() {
        return ordinal_numbers;
    }

    public String getInventory_date() {
        return inventory_date;
    }

    public String getDepartment_code() {
        return department_code;
    }

    public String getAsset_code() {
        return asset_code;
    }

    public String getDepartment_asset_code() {
        return department_asset_code;
    }

    public String getDepartment_asset_name() {
        return department_asset_name;
    }

    public String getRFID_code() {
        return RFID_code;
    }

    public Integer getStatus() {
        return status;
    }

    public Date getDate_acceptance() {
        return date_acceptance;
    }

    public Date getDate_depreciation() {
        return date_depreciation;
    }

    public int getTime_used() {
        return time_used;
    }

    public double getOriginal_price() {
        return original_price;
    }

    public double getDepreciated_price() {
        return depreciated_price;
    }

    public String getLocation() {
        return location;
    }

    public String getGroup_code() {
        return group_code;
    }

    public String getInventory_department() {
        return inventory_department;
    }
}
