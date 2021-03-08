package com.speedata.uhf.main.activity.Inventory;

import android.content.Context;
import android.database.Cursor;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.speedata.uhf.main.api.ApiClient;
import com.speedata.uhf.main.api.ApiInterface;
import com.speedata.uhf.main.helper.MyDatabaseHelper;
import com.speedata.uhf.main.model.MachineryModel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InventoryPresenter {
    private final InventoryView view;
    private static final String TAG = "InventoryPresenter";

    public InventoryPresenter(InventoryView view) {
        this.view = view;
    }

    public void getNew_Inventory_Data(String group_code, String department_name1) {
        Log.d( TAG, "getNew_Inventory_Data: started." );
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
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineryModel>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading( t.getLocalizedMessage() );
            }
        } );
    }

    public void getCurrent_Inventory_Data(String date_inventory, String department_name1) {
        Log.d( TAG, "getCurrent_Inventory_Data: started." );
        view.showLoading();

        //Request to server
        ApiInterface apiInterface = ApiClient.getApiClient().create( ApiInterface.class );
        Call<List<MachineryModel>> call = apiInterface.getCurrentListInventory( date_inventory, department_name1 );
        call.enqueue( new Callback<List<MachineryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<MachineryModel>> call, @NonNull Response<List<MachineryModel>> response) {
                view.hideLoading();
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, response.body().iterator().toString());
                    view.onGetResult(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MachineryModel>> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading( t.getLocalizedMessage() );
            }
        } );
    }

    public void send_Result(List<MachineryModel> inventoryList) {
        Log.d(TAG, "send_Result: started.");
        view.showLoading();

        Gson gson = new Gson();
        String json = gson.toJson(inventoryList);

        //Request to server
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<ResponseBody> call = apiInterface.send_Inventory_Result(body);

        call.enqueue( new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                view.hideLoading();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        view.insertSuccess();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                view.hideLoading();
                view.onErrorLoading(t.getLocalizedMessage());
            }
        } );
    }

    public void logLargeString(String str) {
        if (str.length() > 3000) {
            Log.i("RESPONSE_BODY_AAA", str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i("RESPONSE_BODY_AAA", str);    // Continuation
        }
    }

    public List<MachineryModel> getLocalData(Context context) throws ParseException {
        List<MachineryModel> listMachinery = new ArrayList<>();
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor cursor = myDB.getAllDataSQLite();

        if (cursor.getCount() == 0) {
            Toast.makeText(context, "Error. No local data", Toast.LENGTH_SHORT).show();
        } else {
            //Expected date format
            SimpleDateFormat dateFormat = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            }
            while (cursor.moveToNext()) {
                MachineryModel machineryModel = new MachineryModel();
                machineryModel.setOrdinal_numbers(cursor.getInt(0));
                machineryModel.setInventory_date(cursor.getString(1));
                machineryModel.setDepartment_code(cursor.getString(2));
                machineryModel.setAsset_code(cursor.getString(3));
                machineryModel.setDepartment_asset_code(cursor.getString(4));
                machineryModel.setDepartment_asset_name(cursor.getString(5));
                machineryModel.setRFID_code(cursor.getString(6));
                machineryModel.setStatus(cursor.getInt(7));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    machineryModel.setDate_acceptance(dateFormat.parse(cursor.getString(8)));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    machineryModel.setDate_depreciation(dateFormat.parse(cursor.getString(9)));
                }
                machineryModel.setTime_used(cursor.getInt(10));
                machineryModel.setOriginal_price(cursor.getDouble(11));
                machineryModel.setDepreciated_price(cursor.getDouble(12));
                machineryModel.setLocation(cursor.getString(13));
                machineryModel.setGroup_code(cursor.getString(14));
                machineryModel.setInventory_department(cursor.getString(15));
                listMachinery.add(machineryModel);
            }
        }

        return listMachinery;
    }
}
