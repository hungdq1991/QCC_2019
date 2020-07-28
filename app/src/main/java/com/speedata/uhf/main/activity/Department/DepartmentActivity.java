package com.speedata.uhf.main.activity.Department;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.speedata.uhf.R;
import com.speedata.uhf.main.activity.Inventory.InventoryActivity;
import com.speedata.uhf.main.model.DepartmentModel;

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
            String name1 = departmentList.get( position ).getDepartment_name1();
            String group_code = departmentList.get( position ).getGroup_code();

            if (name1.equals( "IT1" ) || name1.equals( "IT2" )) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString( "group_code", group_code );
                bundle.putString( "department_name1", name1 );
                intent.putExtras( bundle );
                intent.setClass( this, InventoryActivity.class );
                startActivity( intent );
            } else {
                Toast.makeText( DepartmentActivity.this, "Phòng ban chưa nhập dữ liệu kiểm kê!", Toast.LENGTH_SHORT ).show();
            }
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
    public void onErrorLoading(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
