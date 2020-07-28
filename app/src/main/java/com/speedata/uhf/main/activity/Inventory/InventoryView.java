package com.speedata.uhf.main.activity.Inventory;

import com.speedata.uhf.main.model.MachineryModel;

import java.util.List;

public interface InventoryView {
    void showLoading();

    void hideLoading();

    void onGetResult(List<MachineryModel> inventoryModels);

    void onErrorLoading(String message);

    void insertSuccess();
}
