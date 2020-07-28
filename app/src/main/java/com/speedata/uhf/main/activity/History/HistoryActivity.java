package com.speedata.uhf.main.activity.History;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.speedata.uhf.R;
import com.speedata.uhf.main.activity.Department.DepartmentActivity;
import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryView {

    private static final int INTENT_ADD = 100;
    private static final int INTENT_EDIT = 200;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    HistoryPresenter historyPresenter;
    HistoryAdapter historyAdapter;
    HistoryAdapter.ItemClickListener itemClickListener;
    List<ResultInventoryModel> resultModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        InitView();

        historyPresenter = new HistoryPresenter( this );
        historyPresenter.getData();

        swipeRefreshLayout.setOnRefreshListener(
                () -> historyPresenter.getData()
        );

        itemClickListener = ((view, position) -> {
            String date_inventory = resultModelList.get( position ).getInventory_date();
//            String department_name1 = resultModelList.get( position ).getGroup_code();
//            if (department_code.equals( "IT1" ) || department_code.equals( "IT2" ))
//            {
//                startActivityForResult( new Intent( this, InventoryActivity.class ), INTENT_ADD );
//            } else {
//                Toast.makeText( HistoryActivity.this, date_inventory + " " + department_code, Toast.LENGTH_SHORT ).show();
//            }
            Toast.makeText( HistoryActivity.this, "Chức năng đang bảo trì!", Toast.LENGTH_SHORT ).show();
        });

        fab.setOnClickListener( view -> {
            startActivityForResult( new Intent( this, DepartmentActivity.class ), INTENT_ADD );
        } );
    }

    private void InitView() {
        setContentView( R.layout.activity_history );

        setTitle( "Lịch sử kiểm kê" );

        fab = (FloatingActionButton) findViewById( R.id.add );
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById( R.id.swipe_refresh );
        recyclerView = (RecyclerView) findViewById( R.id.recycler_view );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );
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
    public void onGetResult(List<ResultInventoryModel> resultModels) {
        historyAdapter = new HistoryAdapter( this, resultModels, itemClickListener );
        historyAdapter.notifyDataSetChanged();
        recyclerView.setAdapter( historyAdapter );

        resultModelList = resultModels;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }
}
