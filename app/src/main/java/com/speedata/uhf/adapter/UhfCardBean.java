package com.speedata.uhf.adapter;

/**
 * Card search list management
 *
 * @author 张智超
 * @date 2019/3/7
 */
public class UhfCardBean {
    private String tvepc;
    private String tvvRssi;
    private String tvTidUser;
    private String epc;
    private int valid;
    private String rssi;
    private String tidUser;

    public UhfCardBean(String epc, int valid, String rssi, String tidUser) {
        this.epc = epc;
        this.valid = valid;
        this.rssi = rssi;
        this.tidUser = tidUser;
    }

    /**
     * 匹配item的TextView
     *
     * @return tvepc
     */
    String getTvEpc() {
        tvepc = epc;
        return tvepc;
    }

    /**
     * 匹配item的TextView
     *
     * @return tvvRssi
     */
    String getTvvRssi() {
        tvvRssi = "COUNT:" + valid + "  RSSI:" + rssi;
        return tvvRssi;
    }

    /**
     * 匹配item的TextView
     *
     * @return tvTidUser
     */
    public String getTvTidUser() {
        tvTidUser = "T/U:" + tidUser;
        return tvTidUser;
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public String getTidUser() {
        return tidUser;
    }

    public void setTidUser(String tidUser) {
        this.tidUser = tidUser;
    }

}
