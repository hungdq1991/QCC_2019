package com.speedata.uhf.main.activity.History;

import android.util.Log;

import androidx.annotation.NonNull;

import com.speedata.uhf.main.api.ApiClient;
import com.speedata.uhf.main.api.ApiInterface;
import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryPresenter {
    private static final String TAG = "HistoryPresenter";
    private final HistoryView view;

    public HistoryPresenter(HistoryView historyView) {
        this.view = historyView;
    }

    void getData() {
        view.showLoading();
        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<ResultInventoryModel>> call = apiInterface.getInventoryResult();
        call.enqueue( new Callback<List<ResultInventoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResultInventoryModel>> call, @NonNull Response<List<ResultInventoryModel>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    logLargeString(response.body().toString());
                    view.onGetResult(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResultInventoryModel>> call, @NonNull Throwable t) {

            }
        });
    }

    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str);    // Continuation
        }
    }

}
