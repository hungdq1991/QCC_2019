package com.speedata.uhf.main.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class DepartmentModel implements Serializable {
    @Expose
    private String department_number_code;

    @Expose
    private String group_code;

    @Expose
    private String department_name1;

    @Expose
    private String department_name2;

    //GROUP CODE
    public String getGroup_code() {
        return group_code;
    }

    //DEPARTMENT NAME1
    public String getDepartment_name1() {
        return department_name1;
    }

    //DEPARTMENT NAME2
    public String getDepartment_name2() {
        return department_name2;
    }

}
