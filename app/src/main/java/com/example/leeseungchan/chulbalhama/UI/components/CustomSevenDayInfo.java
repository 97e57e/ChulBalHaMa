package com.example.leeseungchan.chulbalhama.UI.components;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;

import java.util.ArrayList;

public class CustomSevenDayInfo {
    private ArrayList<LinearLayout> dayInputs = new ArrayList<>();
    private View view;
    private ArrayList<String> dayName = new ArrayList<>();

    public CustomSevenDayInfo(View v){
        this.view = v;

        dayInputs.add((LinearLayout) v.findViewById(R.id.s_mon));
        dayInputs.add((LinearLayout) v.findViewById(R.id.s_the));
        dayInputs.add((LinearLayout) v.findViewById(R.id.s_wes));
        dayInputs.add((LinearLayout) v.findViewById(R.id.s_thr));
        dayInputs.add((LinearLayout) v.findViewById(R.id.s_fri));
        dayInputs.add((LinearLayout) v.findViewById(R.id.s_sat));
        dayInputs.add((LinearLayout) v.findViewById(R.id.s_sun));
        dayName.add("월");
        dayName.add("화");
        dayName.add("수");
        dayName.add("목");
        dayName.add("금");
        dayName.add("토");
        dayName.add("일");
        setDays();
        setTimeRowFromDB();
    }

    private void setDays(){
        for(int i = 0; i < 7; i++){
            TextView v = dayInputs.get(i).findViewById(R.id.day_name);
            v.setText(dayName.get(i));
        }
    }
    
    private void setTimeRowFromDB(){
        DBHelper helper = DBHelper.getInstance(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select departure_time from day_of_week ", new String[]{});
        
        for(int i = 0; i < 7; i++){
            cursor.moveToNext();
            String time = cursor.getString(0);
            if(time != null) {
                TextView temp = dayInputs.get(i).findViewById(R.id.day_time);
                temp.setText(time);
            }
        }
        db.close();
    }
    
    public void setSomeTimeRow(ArrayList<String> timeData){
        for(int i = 0; i < timeData.size(); i++){
            TextView temp;
            if(timeData.get(i) != null){
                temp = dayInputs.get(i).findViewById(R.id.day_time);
                temp.setText(timeData.get(i));
            }
        }
    }
    
    public void setWholeTimeRow(final ArrayList<Boolean> selectedDays,
                                     final ArrayList<Integer> time){
        if(selectedDays.size() != 0) {
            String timeInput = time.get(0).toString() + ":" + time.get(1).toString();
            for (int i = 0; i < 7; i++) {
                TextView v;
                
                if (selectedDays.get(i)) {
                    v = dayInputs.get(i).findViewById(R.id.day_time);
                    v.setText(timeInput);
                }
            }
        }
    }
    
    public void updateTimeToDays(ArrayList<Boolean> selectable, ArrayList<String> times){
        DBHelper dbHelper = DBHelper.getInstance(view.getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        String sqlToGetDestID = "select _id from destinations order by _id DESC limit 1";
        Cursor c = db.rawQuery(sqlToGetDestID, null);
        c.moveToNext();
        int destId = c.getInt(0);
        String sqlToUpdate = "update day_of_week set ";
        for(int i = 0; i < 7; i++){
            if(selectable.get(i)){
                String time = "\"" + times.get(i) +"\"";
                db.execSQL(sqlToUpdate + "departure_time=" + time + ", destination_id="
                    + destId + " where day=" + "\""+dayName.get(i)+ "\"");
            }
        }
        db.close();
    }
    
    public void updateDayHabit(ArrayList<Boolean> selectable, int habitId){
        if(habitId == -1)
            return;
        
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getWritableDatabase();
        String update = "update day_of_week set habit_id=? where day=?";
        for(int i = 0; i < 7; i++){
            if(selectable.get(i)){
                db.execSQL(update, new Object[]{habitId, dayName.get(i)});
            }
        }
        db.close();
    }
    
    public void getResultTimeDataInput(ArrayList<String> times){
        for(int i = 0; i < 7; i++){
            TextView time = dayInputs.get(i).findViewById(R.id.day_time);
            String actualTime = time.getText().toString();
            if(actualTime != null){
                times.add(actualTime);
            }else{
                times.add(null);
            }
        }
    }

    public void setPlaceData(){

        DBHelper helper = DBHelper.getInstance(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql =
            "select destination_id from day_of_week";
        Cursor destId = db.rawQuery(sql, null);
        
        String nameSql = "select destination_name from destinations where _id=";

        for(int i = 0; i < 7; i++){
            Cursor destName;
            String destinationName;
            TextView temp = dayInputs.get(i).findViewById(R.id.day_place);
            temp.setVisibility(View.VISIBLE);
            if(destId.moveToNext()&&destId.getInt(0)!= 0) {
                destName = db.rawQuery(nameSql+destId.getInt(0),null);
                
                if(destName.moveToNext()&&destName.getString(0) != null) {
                    destinationName = destName.getString(0);
                    Log.e("destination name = ", destinationName);
                    temp.setText(destName.getString(0));
                }
            }
        }
        db.close();
    }

    public void pickDay(final ArrayList<Boolean> selectedDays){
        if(selectedDays.size() != 0) {
            for (int i = 0; i < 7; i++) {
                if (selectedDays.get(i)) {
                    dayInputs.get(i).setBackgroundColor(
                            view.getResources().getColor(R.color.colorTertiary));
                }else{
                    dayInputs.get(i).setBackgroundColor(
                            view.getResources().getColor(R.color.common_google_signin_btn_text_dark_default)
                    );
                }
            }
        }
    }
    
    public void showSelectedDay(int selectedHabitId){
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql ="select habit_id from day_of_week";
        Cursor habitId = db.rawQuery(sql, null);
    
        for(int i = 0; i < 7; i++){
            habitId.moveToNext();
            if (habitId.getInt(0) == selectedHabitId) {
                dayInputs.get(i).setBackgroundColor(
                    view.getResources().getColor(R.color.colorTertiary));
            }else{
                dayInputs.get(i).setBackgroundColor(
                    view.getResources().getColor(R.color.common_google_signin_btn_text_dark_default)
                );
            }
        }
        db.close();
    }

}
