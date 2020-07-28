package com.speedata.uhf.main.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.Date;

public class MachineryModel implements Serializable {

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
    private String location;

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
    private String department_used;

    @Expose
    private String group_code;

    @Expose
    private String inventory_department;

    public String getDepartment_code() {
        return department_code;
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

    public void setStatus(Integer status) {
        this.status = status;
    }
}
