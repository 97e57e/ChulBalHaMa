package com.example.leeseungchan.chulbalhama.UI.components;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.CheckBox;

import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.R;

import java.util.ArrayList;

public class CustomDayCheckBox{
    
    private Context context;
    private ArrayList<CheckBox> boxes = new ArrayList<>();
    
    public CustomDayCheckBox(View view){
        this.context = view.getContext();
        boxes.add((CheckBox) view.findViewById(R.id.mon));
        boxes.add((CheckBox) view.findViewById(R.id.the));
        boxes.add((CheckBox) view.findViewById(R.id.wes));
        boxes.add((CheckBox) view.findViewById(R.id.thr));
        boxes.add((CheckBox) view.findViewById(R.id.fri));
        boxes.add((CheckBox) view.findViewById(R.id.sat));
        boxes.add((CheckBox) view.findViewById(R.id.sun));
    }
    
    public void setBoxes(){
        DBHelper dbHelper = DBHelper.getInstance();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
    
        String sql ="select departure_time from day_of_week";
        Cursor time = db.rawQuery(sql, null);
        for(int i = 0; i < 7; i++){
            time.moveToNext();
            String id = time.getString(0);
            if(id == null){
                boxes.get(i).setEnabled(false);
            }
        }
        db.close();
    }
    
    public void showSelectedBoxes(int selectedId){
        if(selectedId == -1){
            return;
        }
        
        DBHelper dbHelper = DBHelper.getInstance();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
    
        String sql ="select habit_id from day_of_week";
        Cursor habitId = db.rawQuery(sql, null);
        for(int i = 0; i < 7; i++){
            habitId.moveToNext();
            int id = habitId.getInt(0);
            if(selectedId == id){
                boxes.get(i).setChecked(true);
            }
        }
        db.close();
    }
    
    public void getResult(final ArrayList<Boolean> result){
        for(int i = 0; i< 7; i++){
            if(result.size() <= i){
                if(boxes.get(i).isChecked()){
                    result.add(true);
                }else{
                    result.add(false);
                }
            }else{
                if(boxes.get(i).isChecked()){
                    result.set(i, true);
                }else{
                    result.add(i, false);
                }
            }
        }
    }

}
