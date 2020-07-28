package com.speedata.uhf.main.activity.History;

import androidx.annotation.NonNull;

import com.speedata.uhf.main.api.ApiClient;
import com.speedata.uhf.main.api.ApiInterface;
import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryPresenter {
    private HistoryView historyView;

    public HistoryPresenter(HistoryView historyView) {
        this.historyView = historyView;
    }

    void getData() {
        historyView.showLoading();
        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<List<ResultInventoryModel>> call = apiInterface.getInventoryResult();
        call.enqueue( new Callback<List<ResultInventoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResultInventoryModel>> call, @NonNull Response<List<ResultInventoryModel>> response) {
                historyView.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    historyView.onGetResult( response.body() );
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResultInventoryModel>> call, @NonNull Throwable t) {

            }
        } );
    }
}
