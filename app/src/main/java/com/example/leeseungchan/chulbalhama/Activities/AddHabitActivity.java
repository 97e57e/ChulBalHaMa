package com.example.leeseungchan.chulbalhama.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leeseungchan.chulbalhama.Adpater.PrepareAdapter;
import com.example.leeseungchan.chulbalhama.DBHelper;
import com.example.leeseungchan.chulbalhama.DayDialog;
import com.example.leeseungchan.chulbalhama.R;
import com.example.leeseungchan.chulbalhama.UI.components.CustomChangeDeleteItem;
import com.example.leeseungchan.chulbalhama.UI.components.CustomSevenDayInfo;
import com.example.leeseungchan.chulbalhama.VO.HabitsVO;

import java.util.ArrayList;

public class AddHabitActivity extends AppCompatActivity{

    private EditText habitName;
    private int due = -1;
    private CustomChangeDeleteItem dueItem;
    // prepare
    private ArrayList<String> prepares = new ArrayList<>();
    // day and place
    private ArrayList<Boolean> days = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_habit);
        TextView guideText;
        // set up toolbar on top
        setToolbar();

        habitName = findViewById(R.id.add_habit_name);
        
        /* due */
        LinearLayout due = findViewById(R.id.due);
        dueItem = new CustomChangeDeleteItem(due);
        dueItem.setTitle(getResources().getString(R.string.guide_habit_due));
        dueItem.setChange(getResources().getString(R.string.button_setting));
        dueItem.setVisibility(dueItem.DELETE_BTN, View.GONE);
        dueItem.getChange().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dueDialog();
            }
        });
        
        /* prepare */
        final LinearLayout prepare = findViewById(R.id.prepare);

        // prepare TextView guide text
        guideText = prepare.findViewById(R.id.guide_for_selection);
        guideText.setText(R.string.guide_ask_prepare);

        // prepare recycler view
        final RecyclerView prepareRecycle = prepare.findViewById(R.id.list);

        RecyclerView.LayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        prepareRecycle.setLayoutManager(layoutManager);

        final RecyclerView.Adapter prepareAdapter = new PrepareAdapter(prepares);
        prepareRecycle.setAdapter(prepareAdapter);

        Button prepareInputButton = prepare.findViewById(R.id.button_for_selection);
        prepareInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = prepare.findViewById(R.id.input_for_selection);
                if(text.getText().toString().length() < 1){
                    text.setHint("1자 이상 입력해주세요");
                    text.setHintTextColor(Color.RED);
                }else {
                    prepares.add(text.getText().toString());
                    text.setText("");
                    text.setHint("여기에 입력해 주세요.");
                    text.setHintTextColor(Color.GRAY);
                    prepareAdapter.notifyDataSetChanged();
                }
            }
        });


        /* day and place input*/
        final LinearLayout dayPlace = findViewById(R.id.add_habit_intro);

        final CustomSevenDayInfo customSevenDayInfo =
                new CustomSevenDayInfo(findViewById(R.id.add_habit_day));
        customSevenDayInfo.setPlaceData();

        // day and place TextView guide text
        guideText = dayPlace.findViewById(R.id.item_name);
        guideText.setText(R.string.guide_date_and_place_intro);

        TextView text = dayPlace.findViewById(R.id.item_description);
        text.setVisibility(View.INVISIBLE);

        // day and place Button to add
        Button dayPlaceInputButton = dayPlace.findViewById(R.id.button_change);
        dayPlaceInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DayDialog customDialog = new DayDialog(AddHabitActivity.this);

                customDialog.callFunction(days, customSevenDayInfo, -1);
            }
        });

        Button dayDelInputButton = dayPlace.findViewById(R.id.button_delete);
        dayPlace.removeView(dayDelInputButton);
        
        Button store = findViewById(R.id.store_habit);
        store.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkEvertThingInserted()){
                    insertHabit();
                    customSevenDayInfo.updateDayHabit(days, getNewestHabitId());
                    finish();
                }
            }
        });
    }
    
    private int getNewestHabitId(){
        DBHelper helper = DBHelper.getInstance();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor habitId = db.rawQuery("select _id from habits order by _id DESC limit 1", null);
        habitId.moveToNext();
        
        int latestHabitId = habitId.getInt(0);
        db.close();
        return latestHabitId;
    }
    
    private void setToolbar(){
        Toolbar toolbarMain = findViewById(R.id.toolbar_add_habit);
        setSupportActionBar(toolbarMain);
    
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private String getPrepare(){
        StringBuffer prepare = new StringBuffer();
        for(int i = 0; i < prepares.size(); i++){
            prepare.append(prepares.get(i) + ",");
        }

        return prepare.toString();
    }
    
    private void insertHabit(){
        DBHelper helper = DBHelper.getInstance(this);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
            "insert into habits (habit_name,  due, prepare) values(?,?,?)",
            new Object[]{
                this.habitName.getText().toString(),
                due,
                getPrepare()}
            );
        db.close();
    }
    
    
    private void dueDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        
        builder.setTitle("달성 목표");
        LayoutInflater inflater = this.getLayoutInflater();
        View inflatedView = inflater.inflate(R.layout.dialog_target, null);
        
        final EditText input = inflatedView.findViewById(R.id.input);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflatedView)
            // Add action buttons
            .setPositiveButton(R.string.button_change, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    due = Integer.parseInt(input.getText().toString());
                    dueItem.setTitle(due + " 일");
                }
            })
            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
    
        builder.create();
        builder.show();
    }
    
    private boolean checkEvertThingInserted(){
        if(!isHabitNameEmpty() && !isDueEmpty() && !isDayEmpty()){
            return true;
        }
        return false;
    }
    
    private boolean isHabitNameEmpty(){
        if(habitName.getText().toString().length() == 0){
            Toast.makeText(getApplicationContext(), "이름을 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    
    private boolean isDueEmpty(){
        if(due == -1){
            Toast.makeText(getApplicationContext(), "기한을 입력해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    
    private boolean isDayEmpty(){
        if(days.isEmpty()){
            Toast.makeText(getApplicationContext(), "요일을 설정해 주시기 바랍니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
