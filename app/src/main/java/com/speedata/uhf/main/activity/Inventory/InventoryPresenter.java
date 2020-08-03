package com.speedata.uhf.main.activity.Inventory;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.speedata.uhf.main.api.ApiClient;
import com.speedata.uhf.main.api.ApiInterface;
import com.speedata.uhf.main.model.MachineryModel;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryPresenter {
    private InventoryView view;

    public InventoryPresenter(InventoryView view) {
        this.view = view;
    }

    public void getData(String group_code, String department_name1) {
        view.showLoading();

        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<List<MachineryModel>> call = apiInterface.getListInventory( group_code, department_name1 );
        call.enqueue( new Callback<List<MachineryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineryModel>> call, @NonNull Response<List<MachineryModel>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    view.onGetResult( response.body() );
                    // Get your Response
//                    logLargeString( response.body().toString() );
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineryModel>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading( t.getLocalizedMessage() );
            }
        } );
    }

    void send_Result(List<MachineryModel> inventoryList) {
        view.showLoading();

        Gson gson = new Gson();
        String json = gson.toJson( inventoryList );

//        logLargeString( json);

        //Request to server
        RequestBody body = RequestBody.create( MediaType.parse( "application/json; charset=utf-8" ), json );
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<ResponseBody> call = apiInterface.send_Inventory_Result( body );

//        logLargeString(body.toString());

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.hideLoading();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        view.insertSuccess();
//                        logLargeString(response.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        } );
    }

    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i( "RESPONSE_BODY_AAA", str.substring( 0, 3000 ) );
            logLargeString( str.substring( 3000 ) );
        } else {
            Log.i( "RESPONSE_BODY_AAA", str );    // Continuation
        }
    }
}
