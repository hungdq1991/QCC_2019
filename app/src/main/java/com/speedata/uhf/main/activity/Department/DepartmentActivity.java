package com.speedata.uhf.main.activity.Department;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.speedata.uhf.R;
import com.speedata.uhf.main.activity.Inventory.InventoryActivity;
import com.speedata.uhf.main.model.DepartmentModel;

import java.time.LocalDate;
import java.util.List;

public class DepartmentActivity extends AppCompatActivity implements DepartmentView {

    private static final int INTENT_ADD = 100;
    private static final int INTENT_EDIT = 200;

    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    DepartmentPresenter departmentPresenter;
    DepartmentAdapter departmentAdapter;
    DepartmentAdapter.ItemClickListener itemClickListener;
    List<DepartmentModel> departmentList;

    String name1 = "";
    String group_code = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_department );

        swipeRefreshLayout = findViewById( R.id.swipe_refresh );
        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        setTitle( "Danh sách phòng ban" );

        departmentPresenter = new DepartmentPresenter( this );
        departmentPresenter.getData();

        swipeRefreshLayout.setOnRefreshListener(
                () -> departmentPresenter.getData()
        );

        itemClickListener = ((view, position) -> {
            name1 = departmentList.get( position ).getDepartment_name1();
            group_code = departmentList.get( position ).getGroup_code();
            String inventory_date = LocalDate.now().toString();

            departmentPresenter.Check_Inventory_Exists( inventory_date, name1 );

//            if (name1.equals( "IT1" ) || name1.equals( "IT2" )) {
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString( "group_code", group_code );
//                bundle.putString( "department_name1", name1 );
//                intent.putExtras( bundle );
//                intent.setClass( this, InventoryActivity.class );
//                startActivity( intent );
//            } else {
//                Toast.makeText( DepartmentActivity.this, "Phòng ban chưa nhập dữ liệu kiểm kê!", Toast.LENGTH_SHORT ).show();
//            }
        });
    }

    @Override
    public void showLoading() {
        swipeRefreshLayout.setRefreshing( true );
    }

    @Override
    public void hideLoading() {
        swipeRefreshLayout.setRefreshing( false );
    }

    @Override
    public void onGetResult(List<DepartmentModel> departmentModels) {
        departmentAdapter = new DepartmentAdapter( this, departmentModels, itemClickListener );
        departmentAdapter.notifyDataSetChanged();
        recyclerView.setAdapter( departmentAdapter );

        departmentList = departmentModels;
    }

    @Override
    /**
     * @result: Kết quả Check
     *          200 : Check OK, di chuyển qua InventoryActivity.
     *          409 : Phòng ban đã kiểm kê trong ngày.
     *          404 : Chưa nhập thông tin máy móc thiết bị cho phòng ban.
     */
    public void onGetCheckDepartment(String result) {
        int result_code = Integer.parseInt( result );
        if (result_code == 200) {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString( "group_code", group_code );
            bundle.putString( "department_name1", name1 );
            intent.putExtras( bundle );
            intent.setClass( this, InventoryActivity.class );
            startActivity( intent );
        } else if (result_code == 409) {
            Toast.makeText( DepartmentActivity.this, "Phòng " + name1 + "đã kiểm kê trong ngày", Toast.LENGTH_SHORT ).show();
        } else if (result_code == 404) {
            Toast.makeText( DepartmentActivity.this, "Chưa nhập thông tin máy móc thiết bị cho phòng " + name1, Toast.LENGTH_SHORT ).show();
        }
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
