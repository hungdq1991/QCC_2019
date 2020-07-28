package com.speedata.uhf.main.api;

import com.speedata.uhf.main.model.DepartmentModel;
import com.speedata.uhf.main.model.MachineryModel;
import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @GET("getDepartment.php")
    Call<List<DepartmentModel>> getDepartment();

    @GET("getInventoryResult.php")
    Call<List<ResultInventoryModel>> getInventoryResult();

    @POST("send_Inventory_Result.php")
    Call<ResponseBody> send_Inventory_Result(@Body RequestBody requestBody);

    @FormUrlEncoded
    @POST("getListMachinery.php")
    Call<List<MachineryModel>> getListInventory(@Field("group_code") String group_code, @Field("department_name1") String department_name1);

    @FormUrlEncoded
    @POST("check_Exists_Inventory.php")
    Call<ResponseBody> check_Exists_Inventory(@Field("inventory_date") String inventory_date, @Field("department_name1") String department_name1);
}
