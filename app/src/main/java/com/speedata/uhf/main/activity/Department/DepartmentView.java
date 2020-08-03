package com.speedata.uhf.main.activity.Department;

import com.speedata.uhf.main.model.DepartmentModel;

import java.util.List;

public interface DepartmentView {
    void showLoading();

    void hideLoading();

    void onGetResult(List<DepartmentModel> departmentModels);

    void onGetCheckDepartment(String result);

    void onErrorLoading(String message);
}
