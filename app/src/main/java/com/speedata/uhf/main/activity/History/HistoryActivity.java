package com.speedata.uhf.main.activity.History;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.speedata.uhf.R;
import com.speedata.uhf.main.activity.Department.DepartmentActivity;
import com.speedata.uhf.main.activity.Inventory.InventoryActivity;
import com.speedata.uhf.main.helper.MyDatabaseHelper;
import com.speedata.uhf.main.model.ResultInventoryModel;

import java.util.List;

public class HistoryActivity extends AppCompatActivity implements HistoryView {
    private static final String TAG = "HistoryActivityAction";

    private static final int INTENT_ADD = 100;

    FloatingActionButton fab;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    HistoryPresenter historyPresenter;
    HistoryAdapter historyAdapter;
    MyDatabaseHelper myDB;
    HistoryAdapter.ItemClickListener itemClickListener;
    List<ResultInventoryModel> resultModelList;

    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitView();

        historyPresenter = new HistoryPresenter(this);
        historyPresenter.getData();

        myDB = new MyDatabaseHelper(this);

        swipeRefreshLayout.setOnRefreshListener(
                () -> historyPresenter.getData()
        );

        itemClickListener = ((view, position) -> {
            Cursor cursor = myDB.checkExistsDataSQLite();
            if (cursor.getCount() == 0) {
                moveToInventoryActivity(position);
            } else {
                while (cursor.moveToNext()) {
                    final String group_code = cursor.getString(0);
                    final String name_department = cursor.getString(1);

                    String message = "Phiên kiểm kê ngày: ";
                    message = message + group_code
                            + ".\nPhòng ban: " + cursor.getString(1)
                            + " chưa hoàn thành, đang lưu cục bộ!\nNhấn YES để tiếp tục phiên kiểm kê dở dang.\nNhấn NO để XÓA TOÀN BỘ dữ liệu cục bộ và tiếp tục.";
                    dialog = new AlertDialog.Builder( this );
                    dialog.setTitle( R.string.confirmed );
                    dialog.setMessage( message )
                            .setPositiveButton( "Yes", (DialogInterface dialog, int which) -> {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString( "department_name1", name_department );
                                intent.putExtra( "SQLite", bundle );
                                intent.setClass( this, InventoryActivity.class );
                                startActivity( intent );
                            } )
                            .setNegativeButton( "Cancel", (DialogInterface dialog, int which) -> {
                                Log.d(TAG, "onClick: Cancel");
                                dialog.dismiss();
                            })
                            .setNeutralButton("No", (DialogInterface dialog, int which) -> {
                                myDB.deleteAllData();
                                moveToInventoryActivity(position);
                            }).show();
                    break;
                }
            }
        });

        fab.setOnClickListener(view -> {
            Cursor cursor = myDB.checkExistsDataSQLite();
            if (cursor.getCount() == 0) {
                startActivityForResult(new Intent(this, DepartmentActivity.class), INTENT_ADD);
            } else {
                while (cursor.moveToNext()) {
                    final String group_code = cursor.getString(0);
                    final String name_department = cursor.getString(1);

                    String message = "Phiên kiểm kê ngày: ";
                    message = message + group_code
                            + ".\nPhòng ban: " + cursor.getString(1)
                            + " chưa hoàn thành, đang lưu cục bộ!\nNhấn YES để tiếp tục phiên kiểm kê dở dang.\nNhấn NO để XÓA TOÀN BỘ dữ liệu cục bộ và tạo phiên kiểm kê mới.";
                    dialog = new AlertDialog.Builder( this );
                    dialog.setTitle( R.string.confirmed );
                    dialog.setMessage( message )
                            .setPositiveButton( "Yes", (DialogInterface dialog, int which) -> {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString( "department_name1", name_department );
                                intent.putExtra( "SQLite", bundle );
                                intent.setClass( this, InventoryActivity.class );
                                startActivity( intent );
                            } )
                            .setNegativeButton( "Cancel", (DialogInterface dialog, int which) -> {
                                Log.d(TAG, "onClick: Cancel");
                                dialog.dismiss();
                            })
                            .setNeutralButton("No", (DialogInterface dialog, int which) -> {
                                myDB.deleteAllData();
                                startActivityForResult(new Intent(this, DepartmentActivity.class), INTENT_ADD);
                            }).show();
                    break;
                }
            }
        });
    }

    private void moveToInventoryActivity(int position) {
        String date_inventory = resultModelList.get(position).getInventory_date();
        String department = resultModelList.get(position).getInventory_department();

        Intent intent = new Intent(this, InventoryActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("date_inventory", date_inventory);
        bundle.putString("department_name1", department);
        intent.putExtra("history", bundle);
        startActivity(intent);
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
