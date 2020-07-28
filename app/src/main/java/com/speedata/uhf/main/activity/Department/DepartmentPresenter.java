package com.speedata.uhf.main.activity.Department;

import androidx.annotation.NonNull;

import com.speedata.uhf.main.api.ApiClient;
import com.speedata.uhf.main.api.ApiInterface;
import com.speedata.uhf.main.model.DepartmentModel;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepartmentPresenter {
    private DepartmentView view;

    public DepartmentPresenter(DepartmentView view) {
        this.view = view;
    }

    public void getData() {
        view.showLoading();
        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<List<DepartmentModel>> call = apiInterface.getDepartment();

        call.enqueue( new Callback<List<DepartmentModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<DepartmentModel>> call, @NonNull Response<List<DepartmentModel>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetResult( response.body() );
//                    Log.d("AA_EE", response.body().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<DepartmentModel>> call, @NonNull Throwable t) {

            }
        } );
    }

    public Boolean Check_Inventory_Exists(String inventory_date, String department_name1) {
        view.showLoading();

        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<ResponseBody> call = apiInterface.check_Exists_Inventory( inventory_date, department_name1 );
        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
//                    view.onGetResult( response.body() );
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading( t.getLocalizedMessage() );
            }
        } );
        return false;
    }
}
