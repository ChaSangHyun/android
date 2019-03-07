package com.devchacar.quizlocker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast

class BootCompleteReceiver : BroadcastReceiver (){
    override fun onReceive(context: Context?, intent: Intent?){
        when {
            intent?.action == Intent.ACTION_BOOT_COMPLETED -> {
                Log.d("quizlocker", "부팅이 완료됨")

                context?.let{

                    // 퀴즈 잠금화면 설정값이 ON인지 확인
                    val pref = PreferenceManager.getDefaultSharedPreferences(context)
                    val useLockScreen = pref.getBoolean("useLockScreen", false)
                    if(useLockScreen) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            Log.d("quizlocker2", "부팅이 완료됨2")
                            it.startForegroundService(Intent(context, LockScreenService::class.java))
                        } else {
                            Log.d("quizlocker3", "부팅이 완료됨3")
                            it.startService(Intent(context, LockScreenService::class.java))
                        }
                    }
                }
            }
        }
    }
}