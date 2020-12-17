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

    public void setInventory_date(String inventory_date) {
        this.inventory_date = inventory_date;
    }

    public void setDepartment_code(String department_code) {
        this.department_code = department_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public void setDepartment_asset_code(String department_asset_code) {
        this.department_asset_code = department_asset_code;
    }

    public void setDepartment_asset_name(String department_asset_name) {
        this.department_asset_name = department_asset_name;
    }

    public void setRFID_code(String RFID_code) {
        this.RFID_code = RFID_code;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setDate_acceptance(Date date_acceptance) {
        this.date_acceptance = date_acceptance;
    }

    public void setDate_depreciation(Date date_depreciation) {
        this.date_depreciation = date_depreciation;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public void setOriginal_price(double original_price) {
        this.original_price = original_price;
    }

    public void setDepreciated_price(double depreciated_price) {
        this.depreciated_price = depreciated_price;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public void setInventory_department(String inventory_department) {
        this.inventory_department = inventory_department;
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
