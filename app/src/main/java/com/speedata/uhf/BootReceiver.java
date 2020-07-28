package com.speedata.uhf;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //Screen wake up
        PowerManager pm = (PowerManager) context.getSystemService( Context.POWER_SERVICE );
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock( PowerManager.ACQUIRE_CAUSES_WAKEUP
                | PowerManager.SCREEN_DIM_WAKE_LOCK, "BootBroadcastReceiver" );
        wl.acquire();

        //Screen unlock
        KeyguardManager km = (KeyguardManager) context.getSystemService( Context.KEYGUARD_SERVICE );
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock( "BootBroadcastReceiver" );
        kl.disableKeyguard();
        //Start interface
        if (Objects.equals( intent.getAction(), ACTION )) {
            Intent myIntent = new Intent( context, SetActivity.class );
            myIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startActivity( myIntent );
        }
    }
}
