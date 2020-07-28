package com.speedata.uhf;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.speedata.libuhf.IUHFService;
import com.speedata.libuhf.UHFManager;
import com.speedata.libuhf.utils.SharedXmlUtil;
import com.speedata.uhf.floatball.FloatBallManager;
import com.speedata.uhf.floatball.FloatListManager;
import com.speedata.uhf.floatball.FloatWarnManager;
import com.yhao.floatwindow.FloatWindow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//import android.os.Message;
//import android.os.SystemClock;
//import android.widget.Toast;
//import com.tencent.bugly.Bugly;
//import com.tencent.bugly.crashreport.CrashReport;

/**
 * @author Quoc Hung
 * @date 2020/07/21
 */
public class MyApp extends Application {
    public final static String UHF_FREQ = "uhf_freq";
    public final static String UHF_SESSION = "uhf_session";
    public final static String UHF_POWER = "uhf_power";
    public final static String UHF_INV_CON = "uhf_inv_con";
    public final static String UHF_INV_TIME = "uhf_inv_time";
    public final static String UHF_INV_SLEEP = "uhf_inv_sleep";
    public static boolean isStart = false;
    public static boolean isOpenDev = false;
    public static boolean isOpenServer = true;
    public static int mPrefix = 3;
    public static int mSuffix = 3;
    public static boolean isLoop = false;
    public static String mLoopTime = "0";
    public static boolean isLongDown = false;
    /**
     * A sign that the program is initialized once,
     * and it will not be initialized during running
     */
    public static boolean isFirstInit = true;
    /**
     * Whether to activate quick mode
     */
    public static boolean isFastMode = false;
    /**
     * 单例
     */
    private static MyApp m_application;
    private IUHFService iuhfService;

    public static MyApp getInstance() {
        return m_application;
    }

    /**
     * Get the process name corresponding to the process number
     *
     * @param pid process number
     * @return process name
     */
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader( new FileReader( "/proc/" + pid + "/cmdline" ) );
            String processName = reader.readLine();
            if (!TextUtils.isEmpty( processName )) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d( "APP", "onCreate" );
        m_application = this;
        Context context = getApplicationContext();
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName( android.os.Process.myPid() );
//HUNGTEST        // 设置是否为上报进程
//        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
//        strategy.setUploadProcess(processName == null || processName.equals(packageName));
//        // 初始化Bugly
//        Bugly.init(getApplicationContext(), "d39e936d74", true, strategy);

        Log.d( "UHFService", "MyApp onCreate" );
    }

    public IUHFService getIuhfService() {
        Log.d( "zzc:", "IUHFService===" + iuhfService );
        return iuhfService;
    }

    public void releaseIuhfService() {
        if (iuhfService != null) {
            iuhfService.closeDev();
            iuhfService = null;
            UHFManager.closeUHFService();
            isFirstInit = true;
        }
    }

    public void setIuhfService() {
        try {
            iuhfService = UHFManager.getUHFService( getApplicationContext() );
            Log.d( "UHFService", "iuhfService初始化: " + iuhfService );
        } catch (Exception e) {
            e.printStackTrace();
            Handler handler = new Handler( Looper.getMainLooper() );
            handler.post( new Runnable() {
                @Override
                public void run() {
                    FloatWarnManager.getInstance( getApplicationContext(), getResources().getString( R.string.dialog_module_none ) );
                    FloatWarnManager floatWarnManager = FloatWarnManager.getFloatWarnManager();
                    if (floatWarnManager != null) {
                        FloatWindow.get( "FloatWarnTag" ).show();
                    }
                }
            } );
        }

    }

    @Override
    public void onTerminate() {
        stopService( new Intent( this, MyService.class ) );
        SharedXmlUtil.getInstance( this ).write( "server", false );
        releaseIuhfService();
        MyApp.isOpenDev = false;

        if (FloatBallManager.getFloatBallManager() != null) {
            FloatBallManager.getFloatBallManager().closeFloatBall();
        }
        if (FloatListManager.getFloatListManager() != null) {
            FloatListManager.getFloatListManager().closeFloatList();
        }
        SharedXmlUtil.getInstance( this ).write( "floatWindow", "close" );
        super.onTerminate();
    }
}
