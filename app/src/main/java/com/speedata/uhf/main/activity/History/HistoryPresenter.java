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
    private HistoryView view;

    public HistoryPresenter(HistoryView historyView) {
        this.view = historyView;
    }

    void getData() {
        view.showLoading();
        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<List<ResultInventoryModel>> call = apiInterface.getInventoryResult();
        call.enqueue( new Callback<List<ResultInventoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<ResultInventoryModel>> call, @NonNull Response<List<ResultInventoryModel>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetResult( response.body() );
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ResultInventoryModel>> call, @NonNull Throwable t) {

            }
        } );
    }

}
