package com.speedata.uhf.main.activity.History;

import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

public interface HistoryView {
    void showLoading();

    void hideLoading();

    void onGetResult(List<ResultInventoryModel> resultModels);

    void onErrorLoading(String message);
}
