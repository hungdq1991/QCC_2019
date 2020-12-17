package com.speedata.uhf.main.activity.Inventory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.bean.SpdInventoryData;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.BaseActivity;
import com.speedata.uhf.InvSetActivity;
import com.speedata.uhf.MyApp;
import com.speedata.uhf.R;
import com.speedata.uhf.SearchDirectionActivity;
import com.speedata.uhf.adapter.UhfCardAdapter;
import com.speedata.uhf.adapter.UhfCardBean;
import com.speedata.uhf.libutils.ToastUtil;
import com.speedata.uhf.main.activity.History.HistoryActivity;
import com.speedata.uhf.main.helper.MyDatabaseHelper;
import com.speedata.uhf.main.model.MachineryModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Main interface
 *
 * @author Quoc Hung
 */
public class InventoryActivity extends BaseActivity implements View.OnClickListener, InventoryView {
    public static final String START_SCAN = "com.spd.action.start_uhf";
    public static final String STOP_SCAN = "com.spd.action.stop_uhf";
    private static final String CHARGING_PATH = "/sys/class/misc/bq25601/regdump/";
    /**
     * HUNG
     */
    private static final int INTENT_ADD = 100;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InventoryPresenter inventoryPresenter;
    private InventoryAdapter inventoryAdapter;
    private InventoryAdapter.ItemClickListener itemClickListener;
    private List<MachineryModel> inventoryList;

    /**
     * Search input box
     */
    private TextView mEtSearch;
    private AlertDialog alertDialog1;
    /**
     * Card search
     */
    private Button mFindBtn;
    private LinearLayout mLlFind, mLlPause;
    /**
     * Card search list
     */
    private TextView mTvListMsg;
    /**
     * Sound switch
     */
    private ToggleButton mTBtnSound;
    private TextView tagNumTv;
    private TextView tagTotal;
    private TextView speedTv;
    private TextView totalTime;
    /**
     * Export
     */
    private Button mBtSearch;
    private ImageView mIvSet;
    private ImageView mIvMenu;
    private UhfCardAdapter uhfCardAdapter;
    private final List<UhfCardBean> uhfCardBeanList = new ArrayList<>();
    private KProgressHUD kProgressHUD;
    private boolean inSearch = false;
    private IUHFService iuhfService;
    private SoundPool soundPool;
    private int soundId;
    private String model;
    /**
     * Exit timing
     */
    private long mKeyTime = 0;
    private long scant = 0;
    /**
     * System time intercepted after the inventory command is issued
     */
    private long startCheckingTime;
    private File file;
    private BufferedWriter writer;
    private long count = 0;
    private Button btnFinish;

    private static final String TAG = "InventoryActivity";
    private String group_code;
    private String department_name1;
    //Value get from another activity
    private String date_inventory;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!MyApp.isOpenServer) {
                String action = intent.getAction();
                switch (Objects.requireNonNull(action)) {
                    case START_SCAN:
                        //Start UHF scanning
                        if (inSearch) {
                            return;
                        }
                        startUhf();
                        break;
                    case STOP_SCAN:
                        if (inSearch) {
                            stopUhf();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    };
    // Listener callback reference code
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                scant++;
                long iop = System.currentTimeMillis();
                if (mTBtnSound.isChecked()) {
                    soundPool.play(soundId, 1, 1, 0, 0, 1);
                }
                Log.e("zzc", "=soundPool.play=====" + (System.currentTimeMillis() - iop));
                SpdInventoryData var1 = (SpdInventoryData) msg.obj;
                int j;
                //Position EPC in inventoryList
                int position_in_list = -1;
                for (j = 0; j < uhfCardBeanList.size(); j++) {
                    if (var1.epc.equals(uhfCardBeanList.get(j).getEpc())) {
                        int v = uhfCardBeanList.get(j).getValid() + 1;
                        uhfCardBeanList.get(j).setValid(v);
                        uhfCardBeanList.get(j).setRssi(var1.rssi);
                        //Find epc code in inventoryList
                        position_in_list = inventoryAdapter.updateRecyclerView(var1.epc, department_name1);
                        if (position_in_list != -1) {
                            inventoryAdapter.getFilter().filter(department_name1);
                            inventoryAdapter.notifyDataSetChanged();

                            count++;
                        }
                        break;
                    }
                }
                if (j == uhfCardBeanList.size()) {
                    uhfCardBeanList.add(new UhfCardBean(var1.epc, 1, var1.rssi, var1.tid));
                    if (!mTBtnSound.isChecked()) {
                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                    }
                }
                uhfCardAdapter.notifyDataSetChanged();
                updateRateCount();
            }

        }
    };
    //
    private MyDatabaseHelper myDB;

    /**
     * Extract time (hour: minute: second) from time (millisecond)
     * Time format: hour: minute: second.ms
     *
     * @param millisecond milliseconds
     * @return time string
     */
    public static String getTimeFromMillisecond(Long millisecond) {
        String milli;
        int num = 100;
        //Calculate hours based on time difference
        long hours = millisecond / (60 * 60 * 1000);
        //Time difference to calculate minutes
        long minutes = (millisecond - hours * (60 * 60 * 1000)) / (60 * 1000);
        //Time difference
        long second = (millisecond - hours * (60 * 60 * 1000) - minutes * (60 * 1000)) / 1000;
        //Time difference
        long milliSecond = millisecond - hours * (60 * 60 * 1000) - minutes * (60 * 1000) - second * 1000;
        if (milliSecond < num) {
            milli = "0" + milliSecond;
        } else {
            milli = "" + milliSecond;
        }
        if (hours == 0) {
            if (minutes == 0) {
                return second + "." + milli + "s";
            }
            return minutes + "m: " + second + "." + milli + "s";
        }
        return hours + "h: " + minutes + "m: " + second + "." + milli + "s";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        Log.d( TAG, "onCreate: started." );
        //Forced to portrait
        setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT );
        iuhfService = MyApp.getInstance().getIuhfService();
        model = UHFManager.getUHFModel();
        //
        initView();
        //
        initData();
        //
        initReceive();
        //
        inventoryPresenter = new InventoryPresenter(this);
        //
        myDB = new MyDatabaseHelper(this);
        //
        try {
            getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //When swipeRefresh, hide loading, do nothing
        swipeRefreshLayout.setOnRefreshListener(() -> {
            //
            hideLoading();
        });

        //When click item in recyclerView
        itemClickListener = ((view, position) -> {
            //TODO
//            String name = inventoryList.get( position ).getDepartment_name();
//            Toast.makeText( InventoryActivity.this, String.valueOf( position ), Toast.LENGTH_SHORT ).show();
        });
    }

    public void initView() {
        setContentView( R.layout.activity_inventory );
        mIvMenu = findViewById( R.id.iv_menu );
        mIvMenu.setOnClickListener( this );
        // Set up
        mIvSet = findViewById( R.id.iv_set );
        mIvSet.setOnClickListener( this );
        // Search button
        mBtSearch = findViewById( R.id.bt_search );
        mBtSearch.setOnClickListener( this );
        //
        mEtSearch = findViewById( R.id.et_search );
        mEtSearch.setOnClickListener( this );
        //
        mFindBtn = findViewById( R.id.btn_find );
        mFindBtn.setOnClickListener( this );
        mLlFind = findViewById( R.id.ll_find_layout );
        mLlPause = findViewById( R.id.ll_pause_layout );
        mTvListMsg = findViewById( R.id.tv_list_msg );
        mTBtnSound = findViewById( R.id.t_btn_sound );
        tagNumTv = findViewById( R.id.tv_number );
        tagTotal = findViewById( R.id.tv_total );
        speedTv = findViewById( R.id.speed_tv );
        totalTime = findViewById( R.id.totalTime );
        //
        btnFinish = findViewById( R.id.btn_finish );
        btnFinish.setOnClickListener( this );
        swipeRefreshLayout = findViewById( R.id.swipe_refresh );
        //
        recyclerView = findViewById( R.id.recycler_view );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        //
        mLlFind.setVisibility( View.VISIBLE );
        mLlPause.setVisibility( View.GONE );
        mTvListMsg.setVisibility( View.GONE );
    }

    public void initSoundPool() {
        soundPool = new SoundPool( 2, AudioManager.STREAM_MUSIC, 0 );
        soundId = soundPool.load( "/system/media/audio/ui/VideoRecord.ogg", 0 );
        Log.d( TAG, "id is " + soundId );

        // Inventory callback function
        iuhfService.setOnInventoryListener( (SpdInventoryData var1) -> {
            handler.sendMessage( handler.obtainMessage( 1, var1 ) );
            Log.d( "UHFService", "Callback" );
        } );
    }

    public void initData() {
        //Power-on
        try {
            if (iuhfService != null) {
                if (openDev()) {
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Load adapter
        uhfCardAdapter = new UhfCardAdapter( this, R.layout.item_uhf_card, uhfCardBeanList );

        MyApp.isOpenServer = false;
        file = new File( CHARGING_PATH );
    }

    /**
     * Register Broadcast
     */
    private void initReceive() {
        Log.d(TAG, "initReceive: register Broadcast.");
        IntentFilter filter = new IntentFilter();
        filter.addAction(START_SCAN);
        filter.addAction(STOP_SCAN);
        registerReceiver(receiver, filter);
    }

    private void getData() throws ParseException {
        Log.d(TAG, "getData: get data intent");
        Intent intent = getIntent();
        Bundle bundle;
        if ((bundle = intent.getBundleExtra("department")) != null) {
            Log.d(TAG, "getData: department");
            group_code = bundle.getString("group_code");
            department_name1 = bundle.getString("department_name1");
            //
            inventoryPresenter.getNew_Inventory_Data(group_code, department_name1);
        } else if ((bundle = intent.getBundleExtra("history")) != null) {
            Log.d(TAG, "getData: history");
            date_inventory = bundle.getString("date_inventory");
            department_name1 = bundle.getString("department_name1");
            //
            inventoryPresenter.getCurrent_Inventory_Data(date_inventory, department_name1);
            //
            btnFinish.setEnabled(true);
        } else if ((bundle = intent.getBundleExtra("SQLite")) != null) {
            department_name1 = bundle.getString("department_name1");

            inventoryList = inventoryPresenter.getLocalData(this);

            inventoryAdapter = new InventoryAdapter(this, inventoryList, itemClickListener);
            inventoryAdapter.notifyDataSetChanged();

            recyclerView.setAdapter(inventoryAdapter);

            if (inventoryAdapter == null) {
                Toast.makeText(this, "Is null", Toast.LENGTH_SHORT).show();
            } else {
                inventoryAdapter.getFilter().filter(department_name1);
            }

            Log.d(TAG, "department name: " + department_name1);

            mLlFind.setVisibility(View.GONE);
            mLlPause.setVisibility(View.VISIBLE);
            btnFinish.setEnabled(true);

            updateRateCount();
        }
    }

    /**
     * Start inventory
     */
    private void startUhf() {
        Log.d( TAG, "startUhf: Start search." );
        if (iuhfService == null) {
            return;
        }
        try {
            writer = new BufferedWriter( new FileWriter( file, false ) );
            writer.write( "otgon" );
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Unmask
        iuhfService.selectCard( 1, "", false );
        iuhfService.inventoryStart();
        inSearch = true;
        mLlFind.setVisibility( View.GONE );
        mLlPause.setVisibility( View.VISIBLE );
        mTvListMsg.setVisibility( View.VISIBLE );
        scant = 0;
        uhfCardBeanList.clear();
        startCheckingTime = System.currentTimeMillis();
        mFindBtn.setText( R.string.Stop_Search_Btn );
        mIvMenu.setEnabled( false );
        mBtSearch.setEnabled( false );
        mIvSet.setEnabled( false );
        mEtSearch.setEnabled( false );
        btnFinish.setEnabled( false );
        btnFinish.setBackgroundDrawable( getResources().getDrawable( R.drawable.btn_gray_shape ) );
        btnFinish.setTextColor( getResources().getColor( R.color.text_gray ) );
        uhfCardAdapter.notifyDataSetChanged();
        updateRateCount();
    }

    /**
     * Stop inventory
     */
    private void stopUhf() {
        Log.d( TAG, "stopUhf: Stop search." );
        if (iuhfService == null) {
            return;
        }
        iuhfService.inventoryStop();
        inSearch = false;
        mFindBtn.setText( R.string.Start_Search_Btn );
        mIvMenu.setEnabled( true );
        mBtSearch.setEnabled( true );
        mIvSet.setEnabled( true );
        mEtSearch.setEnabled( true );
        btnFinish.setEnabled( true );
        btnFinish.setBackgroundDrawable( getResources().getDrawable( R.drawable.btn_bg_select ) );
        btnFinish.setTextColor( getResources().getColor( R.color.text_white ) );
        try {
            writer = new BufferedWriter( new FileWriter( file, false ) );
            writer.write( "otgoff" );
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle listening events
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_set:
                if (MyApp.isFastMode && model.contains( UHFManager.FACTORY_XINLIAN )) {
                    //Turn on Quick Mode to disable settings
                    ToastUtil.customToastView( this, getResources().getString( R.string.toast_stop_fast_tips ), Toast.LENGTH_SHORT
                            , (TextView) LayoutInflater.from( this ).inflate( R.layout.layout_toast, null ) );
                } else {
                    //Assume
                    Intent intent = new Intent( this, InvSetActivity.class );
                    startActivity( intent );
                }
                break;
            case R.id.bt_search:
                //Search for
                String epc = mEtSearch.getText().toString();
                Log.d( "zzc", "epc:" + epc );
                if (epc.isEmpty()) {
                    Toast.makeText( this, getResources().getString( R.string.search_toast ), Toast.LENGTH_SHORT ).show();
                    return;
                }
                Intent intent1 = new Intent( this, SearchDirectionActivity.class );
                Bundle bundle = new Bundle();
                bundle.putString( "epcNumber", mEtSearch.getText().toString() );
                intent1.putExtras( bundle );
                startActivity( intent1 );
                break;
            case R.id.btn_find:
                //Card search
                //Inventory selection
                if (inSearch) {
                    stopUhf();
                } else {
                    startUhf();
                }
                break;
            case R.id.btn_finish:
                inventoryPresenter.send_Result(inventoryList);
                myDB.deleteAllData();
                break;
            case R.id.et_search:
                String[] items = {getResources().getString(R.string.search_dialog_item)};
                Log.d("zzc", "changdu " + uhfCardBeanList.size());
                if (uhfCardBeanList.size() != 0) {
                    items = new String[uhfCardBeanList.size()];
                    for (int i = 0; i < uhfCardBeanList.size(); i++) {
                        items[i] = uhfCardBeanList.get(i).getEpc();
                    }
                }
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                //TODO
//                alertBuilder.setTitle( getResources().getString( R.string.search_dialog_title ) );
                alertBuilder.setTitle("Chức năng đang phát triển");
                alertBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (uhfCardBeanList.size() != 0) {
                            //TODO
                            mEtSearch.setText(uhfCardBeanList.get(i).getEpc());
                        }
                        alertDialog1.dismiss();
                    }
                });
                alertDialog1 = alertBuilder.create();
                alertDialog1.show();
                break;
            case R.id.iv_menu:
//                DefaultSettingDialog defaultSettingDialog = new DefaultSettingDialog(this, iuhfService);
//                defaultSettingDialog.show();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        Log.d( TAG, "onResume: activity.onResume." );
        super.onResume();
        //Initialize the sound thread
        initSoundPool();
        MyApp.isOpenServer = false;
    }

    @Override
    protected void onPause() {
        Log.d( TAG, "onPause: activity.onPause." );
        stopUhf();
        MyApp.isOpenServer = true;
        if (iuhfService != null) {
            //Update callback
            sendUpdateService();
        }
        super.onPause();
    }

    /**
     * Power on and open the serial port
     */
    private boolean openDev() {
        if (!MyApp.isOpenDev) {
            if (iuhfService.openDev() != 0) {
                Toast.makeText( this, "Open serial port failed", Toast.LENGTH_SHORT ).show();
                new AlertDialog
                        .Builder( this )
                        .setTitle( R.string.DIA_ALERT )
                        .setMessage( R.string.DEV_OPEN_ERR )
                        .setPositiveButton( R.string.DIA_CHECK, (DialogInterface dialog, int which) -> finish() ).show();
                MyApp.isOpenDev = false;
                return true;
            } else {
                Log.d( "UHFService", "Power on successfully" );
            }
        }
        MyApp.isOpenDev = true;
        return false;
    }

    /**
     * Update display data
     */
    private void updateRateCount() {
        Log.d(TAG, "==updateRateCount==");
        long mlEndTime = System.currentTimeMillis();

        double rate = Math.ceil((scant * 1.0) * 1000 / (mlEndTime - startCheckingTime));

        long totalTimeCount = mlEndTime - startCheckingTime;

        speedTv.setText(String.format("%s" + getResources().getString(R.string.num), rate));

        count = inventoryAdapter.getItemFindCount();
        tagNumTv.setText(String.format("%s", count));
        tagTotal.setText(" / " + inventoryAdapter.getItemTotalCount(department_name1));

        totalTime.setText(String.format(getResources().getString(R.string.spend_time) + "%s", getTimeFromMillisecond(totalTimeCount)));
    }

    private void sendUpdateService() {
        Log.d( TAG, "sendUpdateService: sendBroadcast update" );
        Intent intent = new Intent();
        intent.setAction( "uhf.update" );
        this.sendBroadcast( intent );
    }

    @Override
    protected void onStop() {
        Log.d( TAG, "onStop: activity.onStop" );
        if (inSearch) {
            iuhfService.inventoryStop();
            inSearch = false;
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (iuhfService != null) {
            if (!SharedXmlUtil
                    .getInstance( this )
                    .read( "server", false )) {
                Log.d( TAG, "onDestroy: activity.onDestroy. Power off" );
                MyApp.getInstance().releaseIuhfService();
                MyApp.isOpenDev = false;
            }
        }
        soundPool.release();
        if (receiver != null) {
            unregisterReceiver( receiver );
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                int s = 2000;
                if ((System.currentTimeMillis() - mKeyTime) > s) {
                    mKeyTime = System.currentTimeMillis();
                    Toast.makeText( getApplicationContext(), "Click again to exit", Toast.LENGTH_SHORT ).show();
                } else {
                    try {
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            default:
                break;
        }
        return super.onKeyDown( keyCode, event );
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
    public void onGetResult(List<MachineryModel> machineryModels) {
        myDB.addMachinery(machineryModels);
        Log.d(TAG, "onGetResult: Size machineryModels" + machineryModels.size());

        inventoryAdapter = new InventoryAdapter(this, machineryModels, itemClickListener);
        inventoryAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(inventoryAdapter);

        if (inventoryAdapter == null) {
            Toast.makeText(this, "Is null", Toast.LENGTH_SHORT).show();
        } else {
            inventoryAdapter.getFilter().filter(department_name1);
        }

        mLlFind.setVisibility(View.GONE);
        mLlPause.setVisibility(View.VISIBLE);

//        //Set total of inventoryList
//        tagTotal.setText( " / " + inventoryAdapter.getItemCount());

        updateRateCount();

        inventoryList = machineryModels;
    }

    @Override
    public void onErrorLoading(String message) {
        Toast.makeText( this, message, Toast.LENGTH_SHORT ).show();
    }

    @Override
    public void insertSuccess() {
        //
        Toast.makeText( InventoryActivity.this, "Hoàn Thành!", Toast.LENGTH_SHORT ).show();
        //
        startActivityForResult( new Intent( this, HistoryActivity.class ), INTENT_ADD );
    }
}
