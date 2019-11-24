package com.example.leeseungchan.chulbalhama.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.leeseungchan.chulbalhama.Activities.MainActivity;


public class WorkManagerTasks extends Worker {

    Context appContext;
    public static int called = 0;

    public WorkManagerTasks(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        this.appContext = appContext;

    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("workManager", "TestWorker::doWork() called ");
        // Start new worker
        // 작업 수행
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run your task here
                String str = "BackGround Service running... " + called;
                Toast.makeText(appContext, str, Toast.LENGTH_SHORT).show();
                Log.e("workManager", "called : " + called);
                if (!MainActivity.mainCreated) {
                    Log.e("workManager", "if statements: " + called);
                    Toast.makeText(appContext, "앱이 종료되어 있음", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(appContext, MainActivity.class);
                    appContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    Toast.makeText(appContext, "이미 앱이 켜져있음", Toast.LENGTH_SHORT).show();
                }
            }
        }, 0);
        //needs to be retried at a later time via Result.retry() 실패시 처리 필요 예상
        return Result.success();

    }
}