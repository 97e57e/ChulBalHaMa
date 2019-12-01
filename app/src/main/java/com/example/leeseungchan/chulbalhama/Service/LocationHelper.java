package com.example.leeseungchan.chulbalhama.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.res.Resources;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.leeseungchan.chulbalhama.Activities.MainActivity;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationHelper {
    private static LocationHelper lHelperInstance = null;

    public static LocationHelper getLocationHelper(Context context){
        if (lHelperInstance == null) {
            lHelperInstance = new LocationHelper(context);
        }
        return lHelperInstance;
    }

    int trash = 0;

    Intent notificationIntent;
    PendingIntent pendingIntent;

    LocationListener gpsLocationListener;
    LocationManager lm;
    DistanceCalc calc;
    Calendar car;
    Context context;
    NotificationManager manager;
    public static final String CHANNEL_ID = "location_noti_channel";
    private int updateInterval = 5000;

    private boolean activityRecognitionStart = false;

    double lastLongitude;
    double lastLatitude;

    String userState = "HOME";

    /* 알람 조건 */
    boolean homeNoti = false;

    /* 조건 파악에 필요한 정보 */
    String userName;
    String habitName;
    String prepareName;
    double dest_lon;
    double dest_lat;
    double start_lon;
    double start_lat;
    double curr_lon=0;
    double curr_lat=0;
    String destination_name;
    Date startDateTime ;
    Date arrivalDateTime;
    long diff, diff2, sec, sec2;

    private LocationHelper(final Context context){
        this.context = context;
        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        calc = new DistanceCalc();
        createNotificationChannel();
        notificationIntent = new Intent(context, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(context,
                0, notificationIntent, 0);
        dbQuery();

        getLocationListener();
        Log.d("LocationHelper", "constructor");
    }

    public void dbQuery(){
        String departure_time = "";
        int today_dest = -1;
        int todays_habit = -1;
        String dest_name = "";
        String dest_cordi = "";
        String todays_habit_name = "";

        /* 유저 데이터 조회*/
        DBHelper helper = DBHelper.getInstance(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        String userSql = "select * from user";
        Cursor cUser = db.rawQuery(userSql, null);
        cUser.moveToNext();
        start_lat = calc.formattingPoint(Double.parseDouble(cUser.getString(1).split(",")[0]));
        start_lon = calc.formattingPoint(Double.parseDouble(cUser.getString(1).split(",")[1]));
        userName = cUser.getString(3);

        Log.d("\nQueryStart", "----------------------");
        Log.d("dbQuery", "유저 이름 : " + userName);
        Log.d("dbQuery", "유저 집 Lat : " + Double.toString(start_lat));
        Log.d("dbQuery", "유저 집 Lon : " + Double.toString(start_lon));

        /* 오늘의 요일은? */
        car = Calendar.getInstance();
        int dayOfWeeks = car.get(Calendar.DAY_OF_WEEK);
        int dayId = 0;
        switch (dayOfWeeks) {
            case 1:
                dayId = 6; // 일
                break;
            case 2:
                dayId = 0; //월
                break;
            case 3:
                dayId = 1; //화
                break;
            case 4:
                dayId = 2; //수
                break;
            case 5:
                dayId = 3; //목
                break;
            case 6:
                dayId = 4; //금
                break;
            case 7:
                dayId = 5; //토
                break;
        }

        /* 요일 테이블 조회. */
        try{

            String daySql = "select * from day_of_week where _id = ?";
            Cursor cDay = db.rawQuery(daySql, new String[]{Integer.toString(dayId + 1)}, null);
            cDay.moveToNext();
            departure_time = cDay.getString(2);
            today_dest = cDay.getInt(3);
            todays_habit = cDay.getInt(4);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            startDateTime = format.parse(departure_time);
            Log.d("DepartureTime Query", departure_time);

        } catch (Exception e){Log.e("LocationHelper", "Day Of Week Table error");}

        /* 요일에 설정된 목적지 조회 */
        try {
            String destSql = "select * from destinations where _id = ?";
            Cursor cDestination = db.rawQuery(destSql, new String[]{Integer.toString(today_dest)}, null);
            cDestination.moveToNext();
            dest_name = cDestination.getString(3);
            dest_cordi = cDestination.getString(1);
            Log.d("Todays Destination?", dest_name);
            destination_name = dest_name;
            dest_lat = calc.formattingPoint(Double.parseDouble(dest_cordi.split(",")[0]));
            dest_lon = calc.formattingPoint(Double.parseDouble(dest_cordi.split(",")[1]));
            Log.d("dbQuery", "목적지 Lat : " + Double.toString(dest_lat));
            Log.d("dbQuery", "목적지 Long : " + Double.toString(dest_lon));
//            dest_lat = Long.parseLong(dest_cordi.split(",")[0]);
        } catch (Exception e){Log.e("LocationHelper", "Destination Table error");}

        /* 오늘의 습관 조회 */
        try{
            String habitSql = "select * from habits where _id = ?";
            Cursor cHabit = db.rawQuery(habitSql, new String[]{Integer.toString(todays_habit)});
            cHabit.moveToNext();
            todays_habit_name = cHabit.getString(1);
            prepareName = cHabit.getString(2);
            Log.d("Todays Habit name ?", todays_habit_name);
            habitName = todays_habit_name;
        } catch (Exception e){Log.e("LocationHelper", "Habits Table error");}
    }

    public void getLocation(){
        if(ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();

            // 위치 정보 얻기 누른 시점 위도, 경도
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            longitude = calc.formattingPoint(longitude);
            latitude = calc.formattingPoint(latitude);
            lastLatitude = latitude;
            lastLongitude = longitude;

            Log.d("최초 위치 정보", "위도 : " + longitude + ", 경도 : " + latitude  );
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        updateInterval,
                        0,
                        gpsLocationListener);
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        updateInterval,
                        0,
                        gpsLocationListener);
        }
    }

    public void getLocationListener(){

        gpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                if(trash++ > 0) {
                    notiCondition();
                } else {
                    Log.e("Location Helper ", "버려~");
                }
                String provider = location.getProvider();
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();

                longitude = calc.formattingPoint(longitude);
                latitude = calc.formattingPoint(latitude);
                curr_lat = latitude;
                curr_lon = longitude;

                if(!activityRecognitionStart)
                {

                }

                lastLatitude = latitude;
                lastLongitude = longitude;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

//        getLocation();
    }

    /* set Interval */
//    public void setUpdateInterval(int interval){
//        this.updateInterval = interval;
//        lm.removeUpdates(gpsLocationListener);
//
//
//        notiCondition();
//
//        gpsLocationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//
//                String provider = location.getProvider();
//                double longitude = location.getLongitude();
//                double latitude = location.getLatitude();
//
//                longitude = calc.formattingPoint(longitude);
//                latitude = calc.formattingPoint(latitude);
//                curr_lat = latitude;
//                curr_lon = longitude;
//                Log.d("LocationHelper", "Current Lat : " + latitude);
//                Log.d("LocationHelper", "Current Lon : " + longitude);
//                //TODO 유저의 위치 vs 목적지(목적지 테이블) 위치 / 집 위치 (유저 테이블) 비교
//                //TODO 시간 비교해서 해당 습관에 대한 Notification or PopUp
//
//                notiCondition();
//
//                if(!activityRecognitionStart)
//                {
//
//
//                }
//
//                lastLatitude = latitude;
//                lastLongitude = longitude;
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//
//            public void onProviderEnabled(String provider) {
//            }
//
//            public void onProviderDisabled(String provider) {
//            }
//        };
//    }


    public void notiCondition() {
        final Notification notification;
        NotificationCompat.Builder nBuilder= new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent);

        Date currentDateTime = new Date();
        Log.e("LocationHelper", "Notification Condition");

        /* 지금 몇시 ? */
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        car = Calendar.getInstance();
        String currentTime = format.format(car.getTime());
        Log.d("CurrentTime?", currentTime);
        Log.d("Current Location" , "집과 나로부터의 거리 ? " + Double.toString(calc.distance(start_lat, start_lon, curr_lat, curr_lon, "meter")));
        Log.d("Current Location" , "학교와 나로부터의 거리 ? " + Double.toString(calc.distance(dest_lat, dest_lon, curr_lat, curr_lon, "meter")));

            /* 집일때 처리 후문 위치를 넣어놓자. 이때 최초에 집에서 나갈 준비를 하세요 라는 걸 띄워줌.*/
        if(calc.distance(start_lat, start_lon, curr_lat, curr_lon, "meter") < 50){
            userState = "HOME";
            if(!homeNoti) {
                notification = nBuilder.setContentTitle("출발 하시기 전에 " + prepareName + "을 준비하세요!").build();
                manager.notify(3, notification);
                homeNoti = true;
            }
            //            Toast.makeText(this.context,Double.toString(calc.distance(start_lat, start_lon, curr_lat, curr_lon, "meter")) + "미터~" , Toast.LENGTH_SHORT).show();
            /* 학교일때 처리 이때 '잘 했냐?' 팝업창을 띄워줌 */
        } else if(calc.distance(dest_lat, dest_lon, curr_lat, curr_lon, "meter") < 50){
            userState = "SCHOOL";
        } else{
            userState = "ROAD";
            //팝업 한번 ( 오늘 '습관이름'을 할 수 있나요? )
//            notification = nBuilder.setContentTitle("나는 어디인가.").build();
//            manager.notify(3, notification);
        }

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public String getUserState(){
        return userState;
    }

    public void removeUpdates(){
        lm.removeUpdates(gpsLocationListener);
    }

    public String getHabitName() {
        return habitName;
    }
}
